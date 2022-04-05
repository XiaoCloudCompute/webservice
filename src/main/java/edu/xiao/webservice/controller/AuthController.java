package edu.xiao.webservice.controller;

import com.timgroup.statsd.StatsDClient;
import edu.xiao.webservice.bean.UserRequestBean;
import edu.xiao.webservice.config.JwtUtil;
import edu.xiao.webservice.model.User;
import edu.xiao.webservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {
    private final static Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StatsDClient statsDClient;

    @PostMapping("/authenticated")
    public ResponseEntity<?> authenticateUser(@RequestBody UserRequestBean userRequestBean) {
        try {
            long start = System.currentTimeMillis();
            LOG.info("enter authenticated");
            statsDClient.incrementCounter("authenticated.post");
            User user = userRepository.findByUsername(userRequestBean.getUsername()).orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s, not found",userRequestBean.getUsername())));
            String jwt = new JwtUtil().generateAccessToken(user);
            Map<String, String> res = new HashMap<>();
            res.put("jwt", jwt);
            long end = System.currentTimeMillis();
            statsDClient.recordExecutionTime("authenticated.post", end - start);
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);
        }
    }
}
