package com.sh.fbs.basic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@MapperScan(basePackages = "com.sh.fbs.basic.**.*.infra")
@EnableDiscoveryClient
public class FbsBasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(FbsBasicApplication.class, args);
    }

}
