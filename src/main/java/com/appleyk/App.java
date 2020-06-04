package com.appleyk;
/*
@author lss
@date 2020/5/14-13:36
*/
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.appleyk")
public class App<EnableJpaRepositories> {

    public static void main(String[] args){
        SpringApplication.run(App.class);
    }
}
