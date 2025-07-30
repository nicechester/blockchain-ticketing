package com.nicechester.blockchain.ticketing;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "wallet")
public class Accounts {
    private Map<String, String> keystore;
}
