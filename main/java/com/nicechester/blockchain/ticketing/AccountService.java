package com.nicechester.blockchain.ticketing;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
public class AccountService {
    @Autowired private ObjectMapper objectMapper;

    @Setter
    private List<Account> accounts;

    @PostConstruct
    public void init() {
        accounts = loadAccounts();
    }

    private void saveAccounts(List<Account> accounts) {
        File jsonFile = new File("accounts.json");
        try {
            objectMapper.writeValue(jsonFile, accounts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("{} accounts were saved to file: {}", accounts.size(), jsonFile.getAbsolutePath());
    }

    private List<Account> loadAccounts() {
        File jsonFile = new File("accounts.json");
        List<Account> accountsFromFile;
        try {
            accountsFromFile = objectMapper.readValue(jsonFile, new TypeReference<List<Account>>() {});
        } catch (IOException e) {
            log.error("Loading accounts.json has been failed. begin with empty accounts", e);
            return new ArrayList<>();
        }
        log.info("{} accounts were loaded to file: {}", accountsFromFile.size(), jsonFile.getAbsolutePath());
        return accountsFromFile;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Account getAccountByAddress(String address) {
        for (Account acc : accounts) {
            if (acc.getAddress().equalsIgnoreCase(address)) {
                return acc;
            }
        }
        return null;
    }

    public Account createAccount(String name, String address, String privateKey) {
        Account newAccount = new Account(address, privateKey, name);
        accounts.add(newAccount);
        saveAccounts(accounts);
        return newAccount;

    }
}
