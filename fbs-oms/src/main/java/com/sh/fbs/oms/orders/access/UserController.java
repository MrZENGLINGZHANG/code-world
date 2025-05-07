package com.sh.fbs.oms.orders.access;

import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/fbs/oms")
@RestController
public class UserController {
    // 根据 ID 获取单个用户信息
    @GetMapping("/user")
    public String getUser(){
        System.out.println("getUser");
        String result = "122";
        return result;
    }



}