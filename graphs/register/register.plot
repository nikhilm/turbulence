set term png
set output "register.png"
set title "Time for Full Ontology Registration - DBpedia Ontology v3.6"
set xlabel "Number of classes in the ontology"
set ylabel "Time (s)"
set tics out
plot 'register.dat' using 1:($2+$3+$4+$5+$6+$7+$8+$9+$10+$11)/10 title 'Total' with linespoints lw 2, 'class-form.dat' using 1:($2+$3+$4+$5+$6+$7+$8+$9+$10+$11)/10 title 'Classes only' with linespoints lw 2
