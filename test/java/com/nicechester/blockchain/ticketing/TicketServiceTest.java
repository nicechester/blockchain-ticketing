// File: test/java/com/nicechester/blockchain/ticketing/TicketServiceTest.java
package com.nicechester.blockchain.ticketing;

import io.reactivex.Flowable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.RawTransactionManager;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class TicketServiceTest {

    private Web3j web3j;
    private Credentials credentials;
    private RawTransactionManager txManager;
    private TicketNFT contract;
    private TicketService ticketService;

    @BeforeEach
    void setUp() throws Exception {
        SmartContractUtils smartContractUtils = mock(SmartContractUtils.class);
        web3j = mock(Web3j.class);
        credentials = mock(Credentials.class);
        txManager = mock(RawTransactionManager.class);
        contract = mock(TicketNFT.class);
        when(smartContractUtils.getWeb3j()).thenReturn(web3j);
        when(smartContractUtils.ticketNFT()).thenReturn(contract);
        when(contract.ticketMintedEventFlowable(any(EthFilter.class)))
                .thenReturn(Flowable.empty());
        when(contract.transferEventFlowable(any(EthFilter.class)))
                .thenReturn(Flowable.empty());
        // Mock contract deployment check
        EthGetCode ethGetCode = mock(EthGetCode.class);
        when(ethGetCode.getCode()).thenReturn("0x1234");
        when(web3j.ethGetCode(anyString(), any())).thenReturn(new org.web3j.protocol.core.Request("", null, null, null) {
            @Override
            public EthGetCode send() {
                return ethGetCode;
            }
        });

        // Use a partial mock to inject the contract
        ticketService = Mockito.spy(new TicketService(smartContractUtils) {
            {
                // Override contract with mock
                java.lang.reflect.Field f = TicketService.class.getDeclaredField("contract");
                f.setAccessible(true);
                f.set(this, contract);
            }
        });
    }

    @Test
    void testMint() throws Exception {
        String to = "0xabc";
        LocalDate visitDate = LocalDate.now();
        String park = "MagicPark";
        String sku = "SKU123";
        TransactionReceipt receipt = new TransactionReceipt();

        // Mock the contract mint call
        when(contract.mint(anyString(), any(BigInteger.class), anyString(), anyString()))
                .thenReturn(new RemoteFunctionCall<>(null, () -> receipt));

        // Mock the static event extraction
        try (MockedStatic<TicketNFT> ticketNFTMockedStatic = mockStatic(TicketNFT.class)) {
            TicketNFT.TicketMintedEventResponse eventResponse = new TicketNFT.TicketMintedEventResponse();
            eventResponse.to = to;
            eventResponse.tokenId = BigInteger.ONE;
            eventResponse.visitDate = BigInteger.valueOf(visitDate.toEpochDay());
            eventResponse.park = park;
            ticketNFTMockedStatic.when(() -> TicketNFT.getTicketMintedEvents(receipt))
                    .thenReturn(List.of(eventResponse));

            assertDoesNotThrow(() -> ticketService.mint(to, visitDate, park, sku));
        }
    }

    @Test
    void testUseTicket() throws Exception {
        when(contract.useTicket(any()))
                .thenReturn(new RemoteFunctionCall<>(null, () -> null));
        assertDoesNotThrow(() -> ticketService.useTicket(BigInteger.ONE));
    }

    @Test
    void testCancelTicket() throws Exception {
        when(contract.cancelTicket(any()))
                .thenReturn(new RemoteFunctionCall<>(null, () -> null));
        assertDoesNotThrow(() -> ticketService.cancelTicket(BigInteger.ONE));
    }

    @Test
    void testGetTicketStatus() throws Exception {
        when(contract.getTicketStatus(any())).thenReturn(new RemoteFunctionCall<>(null, () -> BigInteger.valueOf(TicketService.TicketStatus.Valid.ordinal())));
        assertEquals(TicketService.TicketStatus.Valid, ticketService.getTicketStatus(BigInteger.ONE));
    }

    @Test
    void testGetTicketInfo() throws Exception {
        BigInteger visidDate = BigInteger.valueOf(LocalDate.now().toEpochDay());
        String sku = "SKU123";
        Tuple3<BigInteger, String, String> tuple = new Tuple3<>(visidDate, "Park", sku);
        when(contract.getTicketInfo(any())).thenReturn(new RemoteFunctionCall<>(null, () -> tuple));
        when(contract.getTicketStatus(any())).thenReturn(new RemoteFunctionCall<>(null, () -> BigInteger.valueOf(TicketService.TicketStatus.Valid.ordinal())));
        var ticketInfo = new TicketService.TicketInfo(BigInteger.ONE, "Park", LocalDate.now(), sku, TicketService.TicketStatus.Valid);
        assertEquals(ticketInfo, ticketService.getTicketInfo(BigInteger.ONE));
    }

    @Test
    void testGetTicketsOf() throws Exception {
        String account = "0xabc";
        BigInteger visidDate = BigInteger.valueOf(LocalDate.now().toEpochDay());
        String sku = "SKU123";
        Tuple3<BigInteger, String, String> tuple = new Tuple3<>(visidDate, "Park", sku);
        when(contract.getTicketInfo(any())).thenReturn(new RemoteFunctionCall<>(null, () -> tuple));
        when(contract.getTicketStatus(any())).thenReturn(new RemoteFunctionCall<>(null, () -> BigInteger.valueOf(TicketService.TicketStatus.Valid.ordinal())));
        var ticketInfo = new TicketService.TicketInfo(BigInteger.ONE, "Park", LocalDate.now(), sku, TicketService.TicketStatus.Valid);
        when(contract.getTicketsOf(anyString()))
                .thenReturn(new RemoteFunctionCall<>(null, () -> List.of(BigInteger.ONE)));
        List<TicketService.TicketInfo> result = ticketService.getTicketsOf(account);
        assertEquals(List.of(ticketInfo), result);
    }
}