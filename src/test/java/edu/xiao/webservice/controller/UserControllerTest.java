package edu.xiao.webservice.controller;

import com.google.gson.Gson;
import edu.xiao.webservice.repository.UserRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Order(1)
    public void createUserSuccess() throws Exception {
        Map<String, String> load = new HashMap<>();
        load.put("username", "derek@gmail.com");
        load.put("password", "123456");
        load.put("first_name", "huanlin");
        load.put("last_name", "xiao");

        mvc.perform(MockMvcRequestBuilders.post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(load))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(201));
    }

    @Test
    @Order(2)
    public void createUserDuplicateError() throws Exception {
        Map<String, String> load = new HashMap<>();
        load.put("username", "derek@gmail.com");
        load.put("password", "123456");
        load.put("first_name", "huanlin");
        load.put("last_name", "xiao");

        mvc.perform(MockMvcRequestBuilders.post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(load))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    @Order(3)
    public void createUserNameError() throws Exception {
        Map<String, String> load = new HashMap<>();
        load.put("username", "derek");
        load.put("password", "123456");
        load.put("first_name", "huanlin");
        load.put("last_name", "xiao");

        mvc.perform(MockMvcRequestBuilders.post("/v1/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(load))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(400));
    }

    @Test
    @Order(4)
    public void getUserNoAuthError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/v1/user/self")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401));
    }

    @Test
    @Order(5)
    @WithUserDetails("derek@gmail.com")
    public void getUserSuccess() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/v1/user/self")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Order(6)
    public void updateUserNoAuthError() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/v1/user/self")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(401));
    }
}
