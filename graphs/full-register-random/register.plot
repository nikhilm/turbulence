set term png
set output "register-classes.png"
set title "Time for Class Heirarchy formation - DBpedia Ontology v3.6"
set xlabel "Number of classes in the ontology"
set ylabel "Time (ms)"
set tics out
plot 'graphthis.dat' using 1:2 title '' with linespoints lw 2
