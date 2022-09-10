#!/bin/bash

#mvn clean install

cd ./client/target
tar -xzf tpe1-g10-client-1.0-SNAPSHOT-bin.tar.gz
chmod -R +x ./tpe1-g10-client-1.0-SNAPSHOT

cd ../../server/target
tar -xzf tpe1-g10-server-1.0-SNAPSHOT-bin.tar.gz
chmod -R +x ./tpe1-g10-server-1.0-SNAPSHOT
cd ../..

open -a Terminal "run-registry.sh"
open -a Terminal "run-server.sh"
open -a Terminal "run-client.sh"
