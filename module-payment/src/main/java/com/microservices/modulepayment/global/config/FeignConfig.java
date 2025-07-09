package com.microservices.modulepayment.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.fcfs.moduleorder.client")
public class FeignConfig {
}
