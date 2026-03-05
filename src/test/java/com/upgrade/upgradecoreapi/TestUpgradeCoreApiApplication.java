package com.upgrade.upgradecoreapi;

import org.springframework.boot.SpringApplication;

public class TestUpgradeCoreApiApplication {

    public static void main(String[] args) {
        SpringApplication.from(UpgradeCoreApiApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
