set term png
set output "qr.png"
set title "Time for Triple retreival - LUBM benchmarks"
set xlabel "Query"
set ylabel "Time in ms (log)"
#set logscale y
set tics out
plot 'qr.dat' using ($2+$3+$4+$5+$6+$7+$8+$9+$10)/9:xticlabels(1) title '' with imp lw 8
