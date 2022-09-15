#!/bin/bash

cd `PWD`/client/target/tpe1-g10-client-1.0-SNAPSHOT
chmod -R +x ./run-admin
sh ./run-admin -DserverAddress=127.0.0.1:1099 -Daction=MODELS -DinPath=`PWD`/../../src/main/resources/filesCsv/planes.csv
sh ./run-admin -DserverAddress=127.0.0.1:1099 -Daction=FLIGHTS -DinPath=`PWD`/../../src/main/resources/filesCsv/flights.csv

sh ./run-seatMap -DserverAddress=127.0.0.1:1099 -DflightCode=AA100 -DoutPath=`PWD`/../../src/main/resources/filesCsv/output.csv

sh ./run-notification -DserverAddress=127.0.0.1:1099 -Dflight=AA100 -Dpassenger=Flor00

