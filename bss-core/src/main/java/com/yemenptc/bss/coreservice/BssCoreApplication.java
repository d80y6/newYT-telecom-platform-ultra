package com.yemenptc.bss.coreservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.yemenptc.bss")
@EnableScheduling
public class BssCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(BssCoreApplication.class, args);
    }
}
