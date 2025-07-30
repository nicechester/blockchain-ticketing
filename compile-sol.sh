#!/bin/zsh
# compile-sol.sh
# Script to compile the TicketNFT.sol Solidity contract using solc

# Check if solc is installed
if ! command -v solc &> /dev/null; then
  echo "solc is not installed. Installing with Homebrew..."
  brew install solidity
fi

# Create build directory if it doesn't exist
mkdir -p build

echo "Compiling TicketNFT.sol..."
solc --abi --bin TicketNFT.sol -o build --include-path node_modules --base-path . --overwrite

echo "Compilation complete. ABI and BIN files are in the build/ directory."

if ! command -v web3j &> /dev/null; then
  echo "web3j is not installed. Installing with Homebrew..."
  brew tap web3j/web3j
  brew install web3j
fi

web3j generate solidity -a build/TicketNFT.abi -b build/TicketNFT.bin -o main/java -p com.nicechester.blockchain.ticketing