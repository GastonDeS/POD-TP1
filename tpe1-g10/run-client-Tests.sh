#!/bin/bash

cd `PWD`/client/target/tpe1-g10-client-1.0-SNAPSHOT
chmod -R +x ./run-admin.sh

sh ./run-admin.sh -DserverAddress=127.0.0.1:1099 -Daction=CANCEL -Dflight=AA101
sh ./run-admin.sh -DserverAddress=127.0.0.1:1099 -Daction=RETICKETING

#sh ./run-seatAssign.sh -DserverAddress=127.0.0.1:1099 -Daction=assign -Dpassenger=Flor0 -Dflight=AA100 -Drow=1 -Dcol=B

