set term png
set output "xml2rdf-accu.png"
set title "XML to RDF conversion accuracy"
set xlabel "Query"
set ylabel "Results (#)"
set tics out
set style fill solid 1.0 border -1
set boxwidth 0.8
#plot 'graphthis.dat' using ($1-0.1):3:4:5 title 'XML data' with errorbars lw 3, 'graphthis.dat' using 2 title 'Reference' with histogram
plot 'graphthis.dat' using 3:xticlabels(1) title 'XML data' with histogram, 'graphthis.dat' using 2:xticlabels(1) title 'Reference' with histogram
