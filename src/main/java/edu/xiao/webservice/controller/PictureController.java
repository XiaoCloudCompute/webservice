package edu.xiao.webservice.controller;

import edu.xiao.webservice.bean.PictureResponseBean;
import edu.xiao.webservice.model.Picture;
import edu.xiao.webservice.model.User;
import edu.xiao.webservice.repository.UserRepository;
import edu.xiao.webservice.service.PictureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/v2")
public class PictureController {
    private static final Logger LOG = LoggerFactory.getLogger(PictureController.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    PictureService pictureService;

    @PostMapping("/user/self/pic")
    public ResponseEntity<?> uploadPicture(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s, not found", username)));
            Picture pic = pictureService.upload(file, user);
            return new ResponseEntity<>(PictureResponseBean.createBeanFromPicture(pic), HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
            return new ResponseEntity<>("", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/self/pic")
    public ResponseEntity<?> getPicture(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s, not found", username)));
            Picture pic = pictureService.get(user);
            return new ResponseEntity<>(PictureResponseBean.createBeanFromPicture(pic), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/user/self/pic")
    public ResponseEntity<?> deletePicture(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s, not found", username)));
            pictureService.delete(user);
            return new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }
}
