package com.nicechester.blockchain.ticketing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        List<Account> accounts = List.of(
                new Account("0xabc", "key1", "Alice Marie Smith"),
                new Account("0xdef", "key2", "Bob Johnson"),
                new Account("0x123", "key3", "Charlie Lee Williams")
        );
        accountService = new AccountService();
        accountService.setAccounts(accounts);
    }

    @Test
    void testGetAccounts_SizeAndNames() {
        List<Account> accounts = accountService.getAccounts();
        assertEquals(3, accounts.size());
        assertEquals("Alice Marie Smith", accounts.get(0).getName());
        assertEquals("Bob Johnson", accounts.get(1).getName());
        assertEquals("Charlie Lee Williams", accounts.get(2).getName());
        assertEquals("0xabc", accounts.get(0).getAddress());
        assertEquals("0xdef", accounts.get(1).getAddress());
        assertEquals("0x123", accounts.get(2).getAddress());
    }

    @Test
    void testGetAccountByAddress_Found() {
        Account acc = accountService.getAccountByAddress("0xdef");
        assertNotNull(acc);
        assertEquals("Bob Johnson", acc.getName());
        assertEquals("0xdef", acc.getAddress());
    }

    @Test
    void testGetAccountByAddress_NotFound() {
        Account acc = accountService.getAccountByAddress("0xnotfound");
        assertNull(acc);
    }
}

