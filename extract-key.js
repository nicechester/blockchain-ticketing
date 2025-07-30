const { Wallet } = require('@ethereumjs/wallet'),
      fs = require('fs');


const utcFile = "geth-data/keystore/UTC--2025-07-25T04-32-41.834727000Z--c515b50d564d59c1e3399b0c5c1a0c62936cfc06"
const password = "secr3t"

Wallet.fromV3(fs.readFileSync(utcFile).toString(), password, true).then((wallet) => {
    console.log("Private Key: " + Buffer.from(wallet.getPrivateKey()).toString('hex'))
    console.log("Address: " + Buffer.from(wallet.getAddress()).toString('hex'))
});

