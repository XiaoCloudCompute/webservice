package edu.xiao.webservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @ResponseBody
    @RequestMapping("/healthz")
    public String healthz(){
        return "abc";
    }
}
