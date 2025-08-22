// File: test/java/com/nicechester/blockchain/ticketing/TicketControllerTest.java
package com.nicechester.blockchain.ticketing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TicketControllerTest {

    private TicketService ticketService;
    private TicketController ticketController;

    @BeforeEach
    void setUp() {
        ticketService = mock(TicketService.class);
        ticketController = new TicketController(ticketService);
    }
    @Test
    void testMintTicket() throws Exception {
        String to = "0xabc";
        LocalDate visitDate = LocalDate.now();
        String park = "MagicPark";
        String sku = "SKU123";

        when(ticketService.mint(eq(to), eq(visitDate), eq(park), eq(sku))).thenReturn(BigInteger.ONE);

        ResponseEntity<String> response = ticketController.mintTicket(to, visitDate, park, sku);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Ticket minted for: 0xabc (tokenId=1)", response.getBody());
    }

    @Test
    void testUseTicket() throws Exception {
        BigInteger tokenId = BigInteger.ONE;
        doNothing().when(ticketService).useTicket(tokenId);

        ticketController.useTicket(tokenId);

        verify(ticketService).useTicket(tokenId);
    }

    @Test
    void testGetTicketInfo() throws Exception {
        BigInteger tokenId = BigInteger.ONE;
        String sku = "SKU123";
        TicketService.TicketInfo ticketInfo = new TicketService.TicketInfo(tokenId, "Park", LocalDate.now(), sku, TicketService.TicketStatus.Valid);

        when(ticketService.getTicketInfo(tokenId)).thenReturn(ticketInfo);

        TicketService.TicketInfo response = ticketController.getTicketInfo(tokenId);

        assertEquals(ticketInfo, response);
    }

    @Test
    void testGetTicketsOf() throws Exception {
        String account = "0xabc";
        String sku = "SKU123";
        TicketService.TicketInfo ticketInfo = new TicketService.TicketInfo(BigInteger.ONE, "Park", LocalDate.now(), sku, TicketService.TicketStatus.Valid);

        when(ticketService.getTicketsOf(account)).thenReturn(List.of(ticketInfo));

        List<TicketService.TicketInfo> response = ticketController.getTicketsOf(account);

        assertEquals(List.of(ticketInfo), response);
    }

    @Test
    void testGetTicketStatus() throws Exception {
        BigInteger tokenId = BigInteger.ONE;
        TicketService.TicketStatus status = TicketService.TicketStatus.Valid;

        when(ticketService.getTicketStatus(tokenId)).thenReturn(status);

        TicketService.TicketStatus response = ticketController.getTicketStatus(tokenId);

        assertEquals(status, response);
    }
}