#!/bin/bash
DATA_DIR=`pwd`/data/lubm
onto="$1"
base=$2
n=$3

CP="`pwd`/target/classes/"
export MAVEN_OPTS="-Xms1g -Xmx2g"

for fn in `ls $base.$n.*`
do
	#echo "# fn: $fn" >> total.times
	echo "# fn: $fn" >> class.times
	echo "# fn: $fn" >> roots.num
	for i in {1..10}
    do
    	echo "$fn $i"
    	#echo "# Run $i" >> total.times
    	echo "# Run $i" >> class.times
    	echo "# Run $i" >> roots.num
        cp $fn $DATA_DIR/ontologies/f629feb10f1c89a69b79ca383a6da366c2c64000
        rm -r $DATA_DIR/clusterspace.db
        mvn exec:java -Dexec.mainClass=com.turbulence.Turbulence&
        PID=$!
        sleep 5
        echo "url=$onto" | time curl -v -X POST http://localhost:5000/api/1/register_schema --data @- > sample.output
        kill -TERM $PID
        #grep -Po '(?<=<tt>).*(?=</tt>)' sample.output >> total.times
        grep -Po '(?<=<ct>).*(?=</ct>)' sample.output >> class.times
        grep -Po '(?<=<roots>).*(?=</roots>)' sample.output >> roots.num
        sleep 2
    done
done
