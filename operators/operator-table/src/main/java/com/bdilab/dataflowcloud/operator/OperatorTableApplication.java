package com.bdilab.dataflowcloud.operator;

import com.bdilab.dataflowCloud.clickhouseClient.ClickhouseManagerClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientProperties;

@EnableFeignClients(defaultConfiguration = FeignClientProperties.FeignClientConfiguration.class, clients={ClickhouseManagerClient.class})
@SpringBootApplication
public class OperatorTableApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperatorTableApplication.class, args);
    }

}
