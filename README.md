# Ticketing Java (Spring Boot + Ethereum)

This is a minimal Java backend for an Ethereum-based ticketing system. It uses Spring Boot and web3j to interact with a Solidity ERC-721 smart contract for ticketing.

## Features
- Minimal Spring Boot REST API
- Sample Solidity ERC-721 contract for tickets
- Java code to deploy and interact with the contract using web3j

## Requirements
- Java 17+
- Maven
- Node.js & npm (for Ganache)
- Homebrew (for Geth and solc)

## Getting Started

### 1. Build the project
```sh
mvn clean install
```

### 2. Start a local Ethereum node

#### Option A: Ganache (recommended for fast local development)
```sh
chmod +x run-ganache.sh
./run-ganache.sh
```

#### Option B: Geth (for real node simulation)
```sh
chmod +x run-geth.sh
./run-geth.sh
```

### 3. Compile the Solidity contract
```sh
chmod +x compile-sol.sh
./compile-sol.sh
```
This will generate `build/TicketNFT.abi` and `build/TicketNFT.bin`.

### 4. (Optional) Generate the Java wrapper for the contract
```sh
web3j generate solidity -a build/TicketNFT.abi -b build/TicketNFT.bin -o main/java -p com.example.ticketing
```

### 5. Configure your wallet and node
Edit `main/resources/application.properties`:
```
web3j.client-address=http://127.0.0.1:8545
wallet.private-key=YOUR_PRIVATE_KEY_HERE
```

### 6. Run the application
```sh
mvn spring-boot:run
```

## Next Steps
- Implement REST endpoints for ticket operations
- Deploy and interact with the smart contract from your Java code

---

### Scripts
- `run-ganache.sh`: Start Ganache local blockchain
- `run-geth.sh`: Start Geth local node
- `compile-sol.sh`: Compile Solidity contract

---

For more details, see comments in each script and the main application class.
