package com.bdilab.dataflowCloud.workspace;

import com.bdilab.dataflowCloud.clickhouseClient.ClickhouseManagerClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientProperties;

@SpringBootApplication
@EnableFeignClients(defaultConfiguration = FeignClientProperties.FeignClientConfiguration.class, clients={ClickhouseManagerClient.class})
public class WorkspaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkspaceApplication.class, args);
    }

}
