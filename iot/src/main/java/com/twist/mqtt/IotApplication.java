package com.twist.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description: ${description}
 * @author: chenyingjie
 * @create: 2019-01-07 19:05
 **/
@SpringBootApplication(scanBasePackages = {"com.twist.mqtt"})
public class IotApplication {
    public static void main(String[] args) {
        SpringApplication.run(IotApplication.class);
    }
}
