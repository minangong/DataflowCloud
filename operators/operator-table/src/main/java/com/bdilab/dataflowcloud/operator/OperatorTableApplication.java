package com.bdilab.dataflowcloud.operator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class OperatorTableApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperatorTableApplication.class, args);
    }

}
