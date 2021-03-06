set term png
set output "qm.png"
set title "Time for ClusterSpace mapping - LUBM benchmarks"
set xlabel "Query"
set ylabel "Time in ms (log scale)"
set logscale y
set tics out
plot 'qm.dat' using ($2+$3+$4+$5+$6+$7+$8+$9+$10)/9:xticlabels(1) title '' with imp lw 8
