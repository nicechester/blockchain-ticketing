package com.nicechester.blockchain.ticketing;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

import java.io.File;
import java.io.IOException;

@Log4j2
@Component
@Getter
public class SmartContractUtils {
    @Value("${web3j.chain-id}") private long chainId;
    @Autowired private Wallet wallet;
    @Autowired private ObjectMapper objectMapper;
    private final Web3j web3j;

    public SmartContractUtils(@Value("${web3j.client-address}") String ethereumNodeUrl) {
        this.web3j = buildWeb3j(ethereumNodeUrl);
    }

    public Web3j buildWeb3j(String ethereumNodeUrl) {
        log.info("Connecting to Ethereum node at: {}", ethereumNodeUrl);
        return Web3j.build(new HttpService(ethereumNodeUrl));
    }

    public Credentials credentials(String accountAddress) {
        String privateKey = wallet.getKeystore().get(accountAddress);
        return Credentials.create(privateKey);
    }

    public TransactionManager txManager(Web3j w3j, String accountAddress) {
        return new RawTransactionManager(w3j, credentials(accountAddress), chainId);
    }

    public TicketNFT ticketNFT() throws Exception {
        return ticketNFT(wallet.getKeystore().keySet().iterator().next());
    }

    public TicketNFT ticketNFT(String accountAddress) throws Exception {
        String contractAddress = getContractAddress();
        TicketNFT contract;
        TransactionManager tm = txManager(web3j, accountAddress);
        log.info("Try to load TicketNFT contract at address: {}", contractAddress);
        if (StringUtils.isBlank(contractAddress) || !isContractDeployed(contractAddress)) {
            contract = TicketNFT.deploy(web3j, tm,
                    new StaticGasProvider(ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT),
                    accountAddress).sendAsync().get();
            log.info("Contract deployed at address: {}", contract.getContractAddress());
            contractAddress = saveContractAddress(contract.getContractAddress());
        } else {
            contract = TicketNFT.load(contractAddress, web3j, tm, new StaticGasProvider(ManagedTransaction.GAS_PRICE, Contract.GAS_LIMIT));
            log.info("Contract loaded at address: {}", contract.getContractAddress());
        }
        return contract;
    }

    private record ContractAddress(String address) {}

    private String getContractAddress() {
        File jsonFile = new File("contract-address.json");
        ContractAddress ca;
        try {
            ca = objectMapper.readValue(jsonFile, ContractAddress.class);
        } catch (IOException e) {
            return null;
        }
        log.info("Contract address loaded from file {}: {}", jsonFile.getAbsolutePath(), ca.address());
        return ca.address();
    }

    private String saveContractAddress(String address) {
        File jsonFile = new File("contract-address.json");
        try {
            objectMapper.writeValue(jsonFile, new ContractAddress(address));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Contract address {} saved to file: {}", address, jsonFile.getAbsolutePath());
        return address;
    }

    private boolean isContractDeployed(String address) throws Exception {
        String contractAddress = address.startsWith("0x") ? address : "0x" + address;
        EthGetCode ethGetCode = web3j.ethGetCode(contractAddress, DefaultBlockParameterName.LATEST).send();
        String code = ethGetCode.getCode();
        return code != null && !code.equals("0x");
    }
}
