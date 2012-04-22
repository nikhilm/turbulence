#!/bin/bash
DATA_DIR=`pwd`/data/lubm
onto="$1"

CP="`pwd`/target/classes/"
export MAVEN_OPTS="-Xms1g -Xmx2g"

mvn exec:java -Dexec.mainClass=com.turbulence.Turbulence&
PID=$!
sleep 3
for fn in `ls /Users/nikhilmarathe/Downloads/datasets/lubm/u0xml/mega.*.xml`
do
    echo "# $fn" >> xml2rdf.times
    for i in {0..9}
    do
        sleep 1
    	echo "# Run $i" >> xml2rdf.times
        cat $fn | curl -v -X POST http://localhost:5000/api/1/store_xml_data -H "Content-Type: application/xml" -o $fn.rdf -w "%{time_total}" --data @- >> xml2rdf.times
    done
done
kill -TERM $PID
