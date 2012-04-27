set term png
set output "single-v-cluster.png"
set title "Time for Triple retreival - LUBM benchmarks"
set xlabel "Query"
set ylabel "Time in ms (log)"
#set logscale y
set tics out
set style fill solid 1.0 border -1
set boxwidth 0.9
plot 'full-query/qr.dat' using ($2+$3+$4+$5+$6+$7+$8+$9+$10)/9:xticlabels(1) title 'Single' with histogram, 'cluster-query/results.dat' using ($2+$3+$4+$5+$6+$7+$8+$9+$10)/9/5:xticlabels(1) title 'Cluster (EC2)' with histogram
