package com.nicechester.blockchain.ticketing;

import lombok.Value;

@Value
public class Account {
    private final String address;
    private final String privateKey;
    private final String name;
}

