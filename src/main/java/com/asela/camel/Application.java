package com.asela.camel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.asela.camel")
public class Application {

    public static void main(String[] args) throws Exception {

        SpringApplication.run(Application.class, args);
        Thread.currentThread().join();

    }

}
