#!/bin/sh
docker run -d --name geth -p 8545:8545 -v ./geth-data:/root/.ethereum ethereum/client-go:release-1.10-arm64 \
        --networkid 6198076965 --http --http.api eth,net,web3,miner,personal --http.addr 0.0.0.0 --mine \
        --miner.etherbase 0x6f237e128e43326090541404b861f59a4e2dea72 --nodiscover --txlookuplimit=0
