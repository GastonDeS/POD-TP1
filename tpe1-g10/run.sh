#!/bin/bash

mvn clean install

cd ./client/target
tar -xzf tpe1-g10-client-1.0-SNAPSHOT-bin.tar.gz
chmod -R +x ./tpe1-g10-client-1.0-SNAPSHOT

cd ../../server/target
tar -xzf tpe1-g10-server-1.0-SNAPSHOT-bin.tar.gz
chmod -R +x ./tpe1-g10-server-1.0-SNAPSHOT
cd ../..

open -a Terminal "`pwd`/run-registry.sh"
open -a Terminal "`pwd`/run-server.sh"
open -a Terminal "`pwd`/run-client.sh"
