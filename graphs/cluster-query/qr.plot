set term png
set output "qr.png"
set title "Time for Triple retrieval in the cluster"
set xlabel "Query"
set ylabel "Time in ms (log scale)"
set logscale y
set tics out
set autoscale y
plot 'results.dat' using ($2+$3+$4+$5+$6+$7+$8+$9+$10)/9/5:xticlabels(1) title '' with imp lw 8
