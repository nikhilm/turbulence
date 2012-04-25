#!/bin/bash
DATA_DIR=`pwd`/data/lubm
outputBase="$1"

CP="`pwd`/target/classes/"
export MAVEN_OPTS="-Xms1g -Xmx2g"

export CASSANDRA_CONF="/Users/nikhilmarathe/workspace/turbulence/lubm-cassandra-conf/"
~/Downloads/software/apache-cassandra-1.0.7/bin/cassandra -p ./cpid
sleep 6
CPID=`cat cpid`
mvn exec:java -Dexec.mainClass=com.turbulence.Turbulence&
PID=$!
sleep 3
for qn in 1 2 3 4
do
	#echo "----- # $fn"
    #echo "# $fn" >> xml2rdf.$dir.times
    #for i in {0..4}
    #do
        sleep 1
    	#echo "# Run $i" >> xml2rdf.$dir.times
        cat /Users/nikhilmarathe/Downloads/datasets/lubm/u0xml/queries/q$qn.query | curl -v -X POST http://localhost:5000/api/1/query --data-binary @- | tee $outputBase.$qn.out
    #done
done
kill -TERM $PID
kill -TERM $CPID
