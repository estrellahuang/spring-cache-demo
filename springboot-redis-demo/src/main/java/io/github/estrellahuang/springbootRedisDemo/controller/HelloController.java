package io.github.estrellahuang.springbootRedisDemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试类
 * @author Huang Yuxin
 * @date 2019-06-18
 */
@RestController
public class HelloController {

    @GetMapping("/test")
    public Object test(){
        return "Hello World!";
    }
}
