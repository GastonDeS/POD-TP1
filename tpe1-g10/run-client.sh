#!/bin/bash

cd `pwd`/client/target/tpe1-g10-client-1.0-SNAPSHOT
chmod -R +x ./run-admin
sh ./run-admin -DserverAddress=127.0.0.1:1099 -Daction=MODELS -DinPath=`pwd`/../../src/main/resources/filesCsv/planes.csv
sh ./run-admin -DserverAddress=127.0.0.1:1099 -Daction=FLIGHTS -DinPath=`pwd`/../../src/main/resources/filesCsv/flights.csv

sh ./run-seatMap -DserverAddress=127.0.0.1:1099 -DflightCode=AA100 -DoutPath=`pwd`/../../src/main/resources/filesCsv/output.csv

sh ./run-notifications -DserverAddress=127.0.0.1:1099 -Dflight=AA100 -Dpassenger=Flor00

