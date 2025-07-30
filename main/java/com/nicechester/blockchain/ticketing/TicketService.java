package com.nicechester.blockchain.ticketing;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Log4j2
@Component
public class TicketService {
    private final SmartContractUtils smartContractUtils;
    private final Web3j web3j;
    private final TicketNFT contract;

    public record TicketInfo(BigInteger tokenId, String park, LocalDate visitDate, TicketStatus status) {}

    public TicketService(SmartContractUtils smartContractUtils) throws Exception {
        this.smartContractUtils = smartContractUtils;
        this.web3j = smartContractUtils.getWeb3j();
        this.contract = smartContractUtils.ticketNFT();
    }

    public BigInteger mint(String to, LocalDate visitDate, String park) {
        if (contract == null) {
            throw new IllegalStateException("Contract not available in test mode.");
        }
        BigInteger visitTimestamp = BigInteger.valueOf(visitDate.toEpochDay());
        try {
            TransactionReceipt receipt = contract.mint(to, visitTimestamp, park).send();
            // Parse the TicketMinted event from the receipt
            List<TicketNFT.TicketMintedEventResponse> events = contract.getTicketMintedEvents(receipt);
            if (!events.isEmpty()) {
                return events.get(0).tokenId;
            } else {
                throw new RuntimeException("TicketMinted event not found in transaction receipt.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to mint ticket: " + e.getMessage(), e);
        }
    }

    public void useTicket(BigInteger tokenId) throws Exception {
        contract.useTicket(tokenId).send();
    }

    public void cancelTicket(BigInteger tokenId) throws Exception {
        contract.cancelTicket(tokenId).send();
    }

    public enum TicketStatus { Valid, Used, Canceled }

    public TicketStatus getTicketStatus(BigInteger tokenId) {
        try {
            return TicketStatus.values()[contract.getTicketStatus(tokenId).send().intValue()];
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TicketInfo getTicketInfo(BigInteger tokenId) {
        try {
            var tuple = contract.getTicketInfo(tokenId).send();
            LocalDate visitDate = LocalDate.ofEpochDay(tuple.component1().longValue());
            String park = tuple.component2().toString();
            TicketStatus ticketStatus = getTicketStatus(tokenId);
            return new TicketInfo(tokenId, park, visitDate, ticketStatus);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public TransactionReceipt transferTicket(String from, String to, BigInteger tokenId) throws Exception {
        TicketNFT ticketNFT = smartContractUtils.ticketNFT(from);
        return ticketNFT.transferFrom(from, to, tokenId).send();
    }


    public List<TicketInfo> getTicketsOf(String account) {
        List<BigInteger> tix;
        try {
            tix = contract.getTicketsOf(account).send();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tix.stream()
                .map(this::getTicketInfo)
                .toList();
    }

    public String getContractAddress() {
        if (contract == null) {
            throw new InternalError("Contract not available in test mode.");
        }
        return contract.getContractAddress();
    }

    public EthAccounts getEthAccounts() {
        EthAccounts result = new EthAccounts();
        try {
            result = this.web3j.ethAccounts().send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public EthGetBalance getBalance(String address) {
        EthGetBalance result;
        try {
            result = this.web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
