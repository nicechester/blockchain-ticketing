package com.nicechester.blockchain.ticketing;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AccountService {
    @Autowired Wallet wallet;

    private List<Account> accounts;
    private static final String[] FULL_NAMES = {
        "Alice Marie Smith", "Bob Johnson", "Charlie Lee Williams", "Diana Ann Brown", "Edward James Jones", "Fiona Lynn Garcia", "George Paul Miller", "Hannah Grace Davis", "Ivan Ray Martinez", "Julia Kim Clark", "Kevin Allen Lewis", "Laura Jean Walker", "Michael Scott Young", "Nina Sue Allen", "Oscar Max King", "Paula Lou Wright", "Quentin Dean Scott", "Rachel Rae Green", "Sam Jude Baker", "Tina Jo Adams"
    };

    @PostConstruct
    public void init() {
        accounts = new ArrayList<>();
        int i = 0;
        for (String address : wallet.getKeystore().keySet()) {
            String name = FULL_NAMES[i % FULL_NAMES.length];
            accounts.add(new Account(address, name));
            i++;
        }
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
}
