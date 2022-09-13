#!/bin/bash

cd `PWD`/client/target/tpe1-g10-client-1.0-SNAPSHOT
chmod -R +x ./run-admin.sh

sh ./run-admin.sh -DserverAddress=127.0.0.1:1099 -Daction=CANCEL -Dflight=AA100

