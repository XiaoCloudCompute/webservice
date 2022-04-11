package edu.xiao.webservice.controller;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishResult;
import com.timgroup.statsd.StatsDClient;
import edu.xiao.webservice.bean.UserRequestBean;
import edu.xiao.webservice.bean.UserResponseBean;
import edu.xiao.webservice.model.User;
import edu.xiao.webservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.concurrent.TimeUnit;


@RestController
@RequestMapping("/v1")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private StatsDClient statsDClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AmazonDynamoDB amazonDynamoDB;

    @Autowired
    AmazonSNS amazonSNS;

    @Value("${aws.sns.topic_arn}")
    private String snsTopicARN;

    @Value("${domain}")
    private String domain;

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<length; i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @PostMapping("/user")
    public ResponseEntity<?> createUser(@RequestBody UserRequestBean userRequestBean) {
        try {
            long start = System.currentTimeMillis();
            LOG.info("enter user.post");
            statsDClient.incrementCounter("user.post");
            User user = new User();
            user.setFirstName(userRequestBean.getFirst_name());
            user.setLastName(userRequestBean.getLast_name());
            user.setUsername(userRequestBean.getUsername());
            user.setPassword(passwordEncoder.encode(userRequestBean.getPassword()));
            user = userRepository.save(user);

            try {
                DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
                Table table = dynamoDB.getTable("users");
                Item item = new Item()
                        .withPrimaryKey("username", user.getUsername())
                        .withString("token", getRandomString(20))
                        .withString("msg_type", "create_user")
                        .withString("domain", domain)
                        .withNumber("expire_time", (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1)) / 1000L);
                table.putItem(item);

                PublishResult result = amazonSNS.publish(snsTopicARN, item.toJSON());
            } catch (Exception e) {
                LOG.error(e.getMessage());
            }

            long end = System.currentTimeMillis();
            statsDClient.recordExecutionTime("user.post", end - start);
            return new ResponseEntity<>(UserResponseBean.createBeanFromUser(user), HttpStatus.CREATED);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/self")
    public ResponseEntity<?> getUser(Authentication authentication) {
        try {
            long start = System.currentTimeMillis();
            LOG.info("enter user.self.get");
            statsDClient.incrementCounter("user.self.get");
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s, not found", username)));
            long end = System.currentTimeMillis();
            statsDClient.recordExecutionTime("user.self.get", end - start);
            return new ResponseEntity<>(UserResponseBean.createBeanFromUser(user), HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/user/self")
    public ResponseEntity<?> updateUser(@RequestBody UserRequestBean userRequestBean, Authentication authentication) {
        try {
            long start = System.currentTimeMillis();
            LOG.info("enter user.self.put");
            statsDClient.incrementCounter("user.self.put");
            if (userRequestBean.getId() != null || userRequestBean.getAccount_created() != null || userRequestBean.getAccount_updated() != null) {
                throw new Exception("try to modify other value");
            }
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s, not found", username)));
            user.setUsername(userRequestBean.getUsername());
            user.setFirstName(userRequestBean.getFirst_name());
            user.setLastName(userRequestBean.getLast_name());
            user.setPassword(passwordEncoder.encode(userRequestBean.getPassword()));
            userRepository.save(user);
            long end = System.currentTimeMillis();
            statsDClient.recordExecutionTime("user.self.put", end - start);
            return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/verifyUserEmail")
    public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam String token) {
        try {
            long start = System.currentTimeMillis();
            LOG.info("enter verifyUserEmail");
            statsDClient.incrementCounter("verifyUserEmail");
            User user = userRepository.findByUsername(email).orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s, not found", email)));
            user.setVerified(true);
            userRepository.save(user);

            DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
            Table table = dynamoDB.getTable("users");
            GetItemSpec spec = new GetItemSpec().withPrimaryKey("username", email);
            Item outcome = table.getItem(spec);
            if (!outcome.get("token").equals(token)) {
                throw new UsernameNotFoundException(String.format("User: %s, wrong token", email));
            }

            long end = System.currentTimeMillis();
            statsDClient.recordExecutionTime("verifyUserEmail", end - start);
            return new ResponseEntity<>("", HttpStatus.OK);
        } catch (Exception e) {
            LOG.error(e.getMessage());
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
    }
}
