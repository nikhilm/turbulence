set term png
set output "xml2rdf.png"
set title "Time for XML to RDF conversion"
set xlabel "File"
set ylabel "Time (s)"
set tics out
set style fill solid 1.0 border -1
set boxwidth 0.8
plot 'xml2rdf-indiv.times' using 3:xticlabels(1) title 'Time' with histogram
