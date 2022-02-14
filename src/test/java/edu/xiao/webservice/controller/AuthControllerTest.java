package edu.xiao.webservice.controller;

import com.google.gson.Gson;
import edu.xiao.webservice.model.User;
import edu.xiao.webservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void authError() throws Exception {
        Map<String, String> load = new HashMap<>();
        mvc.perform(MockMvcRequestBuilders.post("/authenticated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(load))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(equalTo("")));
    }

    @Test
    public void authSuccess() throws Exception {
        User user = new User();
        user.setUsername("derektest@gmail.com");
        user.setPassword("123456");
        user.setFirstName("huanlin");
        user.setLastName("xiao");
        userRepository.save(user);

        Map<String, String> load = new HashMap<>();
        load.put("username", "derektest@gmail.com");
        load.put("password", "123456");

        mvc.perform(MockMvcRequestBuilders.post("/authenticated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(load))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
