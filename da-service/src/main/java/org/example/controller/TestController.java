package org.example.controller;

import com.alibaba.fastjson.JSON;
import org.example.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @ClassName: TestController
 * @Description: TODO
 * @Author: xiaoliyu
 * @DateTime: 2024/5/28 14:18
 **/
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping("/opcua/{key}/{value}")
    public String testOpcUa(@PathVariable String key, @PathVariable String value) {
        testService.setDpValueItem(key, value);
        return "Ok...";
    }
}
