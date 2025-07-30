package com.nicechester.blockchain.ticketing;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping("mint")
    public ResponseEntity<String> mintTicket(@RequestParam String to, @RequestParam LocalDate visitDate, @RequestParam String park) {
        try {
            BigInteger tokenId = ticketService.mint(to, visitDate, park);
            return ResponseEntity.ok(String.format("Ticket minted for: %s (tokenId=%d)", to, tokenId));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/{tokenId}/use")
    public String useTicket(@PathVariable BigInteger tokenId) throws Exception {
        ticketService.useTicket(tokenId);
        return "Ticket used";
    }

    @PostMapping("/{tokenId}/cancel")
    public String cancelTicket(@PathVariable BigInteger tokenId) throws Exception {
        ticketService.cancelTicket(tokenId);
        return "Ticket canceled";
    }

    @GetMapping("/{tokenId}/status")
    public TicketService.TicketStatus getTicketStatus(@PathVariable BigInteger tokenId) throws Exception {
        return ticketService.getTicketStatus(tokenId);
    }

    @GetMapping("/{tokenId}/info")
    public TicketService.TicketInfo getTicketInfo(@PathVariable BigInteger tokenId) throws Exception {
        return ticketService.getTicketInfo(tokenId);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferTicket(@RequestParam String from,
                                                 @RequestParam String to,
                                                 @RequestParam BigInteger tokenId) {
        try {
            TransactionReceipt receipt = ticketService.transferTicket(from, to, tokenId);
            return ResponseEntity.ok(receipt.getTransactionHash());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("tickets/{account}")
    public List<TicketService.TicketInfo> getTicketsOf(@PathVariable String account) {
        return ticketService.getTicketsOf(account);
    }

    @GetMapping("address")
    public ResponseEntity<String> getContractAddress() {
        return ResponseEntity.ok(ticketService.getContractAddress());
    }

    @GetMapping("accounts")
    public EthAccounts getAccounts() {
        return ticketService.getEthAccounts();
    }

    @GetMapping("balance")
    public EthGetBalance getBalance(@RequestParam String accountId) {
        return ticketService.getBalance(accountId);
    }
}
