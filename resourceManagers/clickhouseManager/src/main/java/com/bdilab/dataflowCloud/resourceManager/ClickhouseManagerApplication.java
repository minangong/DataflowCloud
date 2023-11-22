package com.bdilab.dataflowCloud.resourceManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class ClickhouseManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClickhouseManagerApplication.class, args);
    }

}
