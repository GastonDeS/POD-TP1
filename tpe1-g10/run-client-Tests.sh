#!/bin/bash

cd `pwd`/client/target/tpe1-g10-client-1.0-SNAPSHOT
chmod -R +x ./run-admin

#sh ./run-admin -DserverAddress=127.0.0.1:1099 -Daction=CONFIRM -Dflight=AA100
#sh ./run-admin -DserverAddress=127.0.0.1:1099 -Daction=RETICKETING

#sh ./run-seatAssign -DserverAddress=127.0.0.1:1099 -Daction=assign -Dpassenger=Flor00 -Dflight=AA100 -Drow=1 -Dcol=A
#sh ./run-seatAssign -DserverAddress=127.0.0.1:1099 -Daction=status -Dflight=AA100 -Drow=1 -Dcol=A
sh ./run-seatAssign -DserverAddress=127.0.0.1:1099 -Daction=alternatives -Dpassenger=Flor00 -Dflight=AA100
