package org.caizs.nettypush.core;

import org.caizs.nettypush.core.bootstrap.LinkBootstrap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(Application.class, args);
        //启动连接
        new LinkBootstrap().start();
    }

}
