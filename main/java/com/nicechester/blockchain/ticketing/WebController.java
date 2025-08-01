package com.nicechester.blockchain.ticketing;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class WebController {
    private final TicketService ticketService;
    private final SmartContractUtils smartContractUtils;

    @GetMapping("/accounts")
    public String accounts(Model model) {
        var keystore = smartContractUtils.getAccounts().getKeystore();
        model.addAttribute("accounts", keystore.keySet());
        return "accounts";
    }

    @GetMapping({"/", "/menu"})
    public String menu() {
        return "menu";
    }

    @GetMapping("/mint")
    public String mintForm() {
        return "mint";
    }

    @PostMapping("/mint")
    public String mintSubmit(@RequestParam String to, @RequestParam LocalDate visitDate, @RequestParam String park, Model model) {
        // TODO: Call service to mint ticket
        try {
            java.math.BigInteger tokenId = ticketService.mint(to, visitDate, park);
            model.addAttribute("message", "Ticket minted! Token ID: " + tokenId);
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "mint";
    }

    @GetMapping("/tickets")
    public String tickets(@RequestParam(required = false) String owner, Model model) {
        // TODO: Call service to get tickets for owner
        model.addAttribute("tickets", null); // Replace with actual ticket list
        if (owner != null && !owner.isBlank()) {
            model.addAttribute("owner", owner);
            try {
                var tickets = ticketService.getTicketsOf(owner);
                model.addAttribute("tickets", tickets);
            } catch (Exception e) {
                model.addAttribute("tickets", null);
                model.addAttribute("message", "Error: " + e.getMessage());
            }
        } else {
            model.addAttribute("tickets", null);
        }
        return "tickets";
    }

    @GetMapping("/use")
    public String useForm() {
        return "use";
    }

    @PostMapping("/use")
    public String useSubmit(@RequestParam String tokenId, Model model) {
        // TODO: Call service to use ticket
        try {
            ticketService.useTicket(new java.math.BigInteger(tokenId));
            model.addAttribute("message", "Ticket used!");
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "use";
    }

    @GetMapping("/cancel")
    public String cancelForm() {
        return "cancel";
    }

    @PostMapping("/cancel")
    public String cancelSubmit(@RequestParam String tokenId, Model model) {
        // TODO: Call service to cancel ticket
        try {
            ticketService.cancelTicket(new java.math.BigInteger(tokenId));
            model.addAttribute("message", "Ticket canceled!");
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "cancel";
    }

    @GetMapping("/status")
    public String status(@RequestParam(required = false) String tokenId, Model model) {
        // TODO: Call service to get ticket status
        if (tokenId != null && !tokenId.isBlank()) {
            try {
                var status = ticketService.getTicketStatus(new java.math.BigInteger(tokenId));
                model.addAttribute("status", status);
            } catch (Exception e) {
                model.addAttribute("status", "Error: " + e.getMessage());
            }
        } else {
            model.addAttribute("status", null);
        }
        return "status";
    }

    @GetMapping("/info")
    public String info(@RequestParam(required = false) String tokenId, Model model) {
        // TODO: Call service to get ticket info
        if (tokenId != null) {
            if (tokenId != null && !tokenId.isBlank()) {
                try {
                    var info = ticketService.getTicketInfo(new java.math.BigInteger(tokenId));
                    model.addAttribute("info", info);
                } catch (Exception e) {
                    model.addAttribute("info", null);
                    model.addAttribute("message", "Error: " + e.getMessage());
                }
            } else {
                model.addAttribute("info", null);
            }
        }
        return "info";
    }

    @GetMapping("/transfer")
    public String transferForm() {
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transferSubmit(@RequestParam String from, @RequestParam String to, @RequestParam String tokenId, Model model) {
        try {
            ticketService.transferTicket(from, to, new java.math.BigInteger(tokenId));
            model.addAttribute("message", "Ticket transferred!");
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
        }
        return "transfer";
    }


    // Mock class for demonstration
    public static class TicketInfoMock {
        public String visitDate;
        public String park;
        public TicketInfoMock(String visitDate, String park) {
            this.visitDate = visitDate;
            this.park = park;
        }
        public String getVisitDate() { return visitDate; }
        public String getPark() { return park; }
    }
}
