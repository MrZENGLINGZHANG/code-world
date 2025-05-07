package com.sh.fbs.clients.contollers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/fbs/client")
@RestController
public class ClientController{

    @GetMapping("/hello")
    @ResponseBody
    public String hello() {
        return "Hello World";
    }

}
