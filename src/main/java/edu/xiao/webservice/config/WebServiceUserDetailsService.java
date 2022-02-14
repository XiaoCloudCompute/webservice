package edu.xiao.webservice.config;

import edu.xiao.webservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;

@Service
public class WebServiceUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        edu.xiao.webservice.model.User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User: %s, not found", username)));
        return new User(user.getUsername(), user.getPassword(), new ArrayList<>());
    }
}
