package com.retrip.trip.infra.adapter.in.presentation.rest;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@RestController
public class TestController {

    @Value("${server.env}")
    private String env;

    @Value("${server.address}")
    private String address;

    @Value("${server.port}")
    private String port;

    @Value("${serverName}")
    private String serverName;

    @GetMapping("/rt")
    public ResponseEntity<?> retripCheck() {

        Map<String,String> responseData = new TreeMap<>();
        responseData.put("env",env);
        responseData.put("serverName",serverName);
        responseData.put("port",port);
        responseData.put("adress:",address);
        responseData.put("name","이혁진");
        responseData.put("age","30");
        responseData.put("test","test");

        return ResponseEntity.ok(responseData);

    }

    @GetMapping("/env")
    public  ResponseEntity<?> getEnv(){
        return ResponseEntity.ok(env);
    }
}
