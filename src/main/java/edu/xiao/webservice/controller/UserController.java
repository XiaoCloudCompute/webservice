package edu.xiao.webservice.controller;

import com.timgroup.statsd.StatsDClient;
import edu.xiao.webservice.bean.UserRequestBean;
import edu.xiao.webservice.bean.UserResponseBean;
import edu.xiao.webservice.model.User;
import edu.xiao.webservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


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
}
