package com.fcfs.moduleuser.global.config;

import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.fcfs.moduleuser.client")
public class FeignConfig {
}
