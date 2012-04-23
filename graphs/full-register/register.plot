set term png
set output "register.png"
set title "Time for complete Clusterspace formation"
set xlabel "Number of roots"
set ylabel "Time (s)"
set tics out
plot 'register.dat' using 1:($2+$3+$4+$5+$6+$7+$8+$9+$10+$11)/10 title 'Total' with imp lw 2
