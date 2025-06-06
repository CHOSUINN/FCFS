package com.fcfs.moduleuser.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.fcfs.moduleuser.client")
public class FeignConfig {
}
