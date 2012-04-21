#!/bin/bash
DATA_DIR=`pwd`/data/lubm
onto="$1"

CP="`pwd`/target/classes/"
export MAVEN_OPTS="-Xms1g -Xmx2g"

rm -r $DATA_DIR/clusterspace.db
mvn exec:java -Dexec.mainClass=com.turbulence.Turbulence&
PID=$!
sleep 5
echo "url=$onto" | time curl -v -X POST http://localhost:5000/api/1/register_schema --data @-
kill -TERM $PID
