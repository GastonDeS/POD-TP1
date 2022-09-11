#!/bin/bash

cd `PWD`/client/target/tpe1-g10-client-1.0-SNAPSHOT
chmod -R +x ./run-admin.sh
sh ./run-admin.sh -DserverAddress=127.0.0.1:1099 -Daction=MODELS -DinPath=/Users/gastondeschant/facultad/POD/POD-TP1/tpe1-g10/client/src/main/resources/filesCsv/planes.csv
sh ./run-admin.sh -DserverAddress=127.0.0.1:1099 -Daction=FLIGHTS -DinPath=/Users/gastondeschant/facultad/POD/POD-TP1/tpe1-g10/client/src/main/resources/filesCsv/flights.csv

