package com.sh.fbs.oms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = "com.sh.fbs.oms")
public class FbsOmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FbsOmsApplication.class, args);
    }


}
