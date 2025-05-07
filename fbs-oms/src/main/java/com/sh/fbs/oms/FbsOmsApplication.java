package com.sh.fbs.oms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan(basePackages = "com.sh.fbs.oms")
@EnableDiscoveryClient
public class FbsOmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FbsOmsApplication.class, args);
    }

}
