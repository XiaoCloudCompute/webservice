package edu.xiao.webservice.controller;

import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private StatsDClient statsDClient;

    @ResponseBody
    @RequestMapping("/healthz")
    public String healthz(){
        long start = System.currentTimeMillis();
        LOG.info("enter healthz.get");
        statsDClient.incrementCounter("healthz.get");
        long end = System.currentTimeMillis();
        statsDClient.recordExecutionTime("healthz.get", end - start);
        return "";
    }
}
