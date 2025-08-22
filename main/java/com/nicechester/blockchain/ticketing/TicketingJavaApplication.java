package com.nicechester.blockchain.ticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({Wallet.class})
public class TicketingJavaApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketingJavaApplication.class, args);
    }
}
