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
    private final AccountService accountService;

    @GetMapping("/accounts")
    public String accounts(Model model) {
        model.addAttribute("accounts", accountService.getAccounts());
        return "accounts";
    }

    @GetMapping({"/", "/menu"})
    public String menu(Model model) {
        model.addAttribute("accounts", accountService.getAccounts());
        return "menu";
    }

    @GetMapping("/mint")
    public String mintForm(@RequestParam(required = false) String to, Model model) {
        model.addAttribute("to", to);
        if (to != null && !to.isBlank()) {
            try {
                var tickets = ticketService.getTicketsOf(to);
                model.addAttribute("tickets", tickets);
                model.addAttribute("toAccount", accountService.getAccountByAddress(to));
            } catch (Exception e) {
                model.addAttribute("tickets", null);
                model.addAttribute("message", "Error loading tickets: " + e.getMessage());
            }
        }
        model.addAttribute("accounts", accountService.getAccounts());
        return "mint";
    }

    @PostMapping("/mint")
    public String mintSubmit(@RequestParam String to, @RequestParam LocalDate visitDate, @RequestParam String park, @RequestParam String sku, Model model) {
        try {
            java.math.BigInteger tokenId = ticketService.mint(to, visitDate, park, sku);
            model.addAttribute("message", "Ticket minted! Token ID: " + tokenId);
            model.addAttribute("to", to);
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("to", to);
        }
        // Always show tickets for this account after mint
        if (to != null && !to.isBlank()) {
            try {
                var tickets = ticketService.getTicketsOf(to);
                model.addAttribute("tickets", tickets);
            } catch (Exception e) {
                model.addAttribute("tickets", null);
                model.addAttribute("message", "Error loading tickets: " + e.getMessage());
            }
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
    public String useSubmit(@RequestParam String tokenId, @RequestParam(required = false) String to, Model model) {
        try {
            ticketService.useTicket(new java.math.BigInteger(tokenId));
            model.addAttribute("message", "Ticket used!");
            model.addAttribute("to", to);
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("to", to);
        }
        model.addAttribute("owner", to);
        return "use";
    }

    @GetMapping("/cancel")
    public String cancelForm() {
        return "cancel";
    }

    @PostMapping("/cancel")
    public String cancelSubmit(@RequestParam String tokenId, @RequestParam(required = false) String to, Model model) {
        try {
            ticketService.cancelTicket(new java.math.BigInteger(tokenId));
            model.addAttribute("message", "Ticket canceled!");
            model.addAttribute("to", to);
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("to", to);
        }
        model.addAttribute("owner", to);
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
    public String transferForm(@RequestParam String tokenId, @RequestParam String from, Model model) {
        model.addAttribute("accounts", accountService.getAccounts());
        model.addAttribute("from", from);
        model.addAttribute("fromAccount", accountService.getAccountByAddress(from));
        model.addAttribute("tokenId", tokenId);
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transferSubmit(@RequestParam String from, @RequestParam String to, @RequestParam String tokenId, Model model) {
        try {
            ticketService.transferTicket(from, to, new java.math.BigInteger(tokenId));
            model.addAttribute("from", from);
            model.addAttribute("to", to);
            model.addAttribute("tokenId", tokenId);
            return "transfered";
        } catch (Exception e) {
            model.addAttribute("message", "Error: " + e.getMessage());
            model.addAttribute("from", from);
            model.addAttribute("tokenId", tokenId);
            // Optionally, you can add 'to' as well if needed
            return "transfer";
        }
    }

    @GetMapping("/create-account")
    public String createAccountForm() {
        return "create-account";
    }

    @PostMapping("/create-account")
    public String createAccountSubmit(@RequestParam String name, Model model) {
        Account account = smartContractUtils.createAccount(name);
        if (account != null) {
            model.addAttribute("account", account);
            model.addAttribute("message", "Account created successfully!");
        } else {
            model.addAttribute("message", "Failed to create account. See logs for details.");
        }
        return "create-account";
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
