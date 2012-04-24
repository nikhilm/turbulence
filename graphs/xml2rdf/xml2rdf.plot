set term png
set output "xml2rdf.png"
set title "Time for XML to RDF conversion"
set xlabel "Class Instances, Property Instances"
set ylabel "Time (ms)"
set tics out
plot 'graphthis.dat' using 2:xticlabels(1) title 'Time' with linespoints lw 2
