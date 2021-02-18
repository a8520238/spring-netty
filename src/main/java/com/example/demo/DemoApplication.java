package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.SpringVersion;

//@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        String version = SpringVersion.getVersion();
        String version1 = SpringBootVersion.getVersion();
        System.out.println(version);
        System.out.println(version1);
        SpringApplication.run(DemoApplication.class, args);
    }

}
