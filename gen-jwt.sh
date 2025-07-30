#!/bin/bash
# Generate a random 32-byte hex string and save to jwt.hex
openssl rand -hex 32 > jwt.hex
echo "jwt.hex generated:"
cat jwt.hex
