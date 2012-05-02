Semantic Mapping of XML Metadata for Cloud-based Large Scale Data Management
============================================================================
.. class:: subtitle

    |                        Nikhil Marathe (200801011)
    |                    Supervisor: Prof. Sourish Dasgupta

.. raw:: pdf

    PageBreak cutePage

Today
-----

.. image:: images/net-custom.png
    :width: 12cm

Future Semantic Web
-------------------

.. image:: images/net-future.png
    :width: 12cm

Wishful thinking?
-----------------

In the mean time

.. image:: images/net-interim.png
    :width: 12cm

The problem
------------

.. image:: images/xml2rdfcode.png
    :width: 12cm

The problem
------------

* Large scale & Big data
* Should be:
    * Quick
    * Scalable
    * Fault-tolerant
    * Highly available

Background
----------

* XML to RDF - loosely structured, needs a template to convert
* YARS2 - federated repository for semantic data with high performance
  distributed queries
* Cassandra/Hadoop/MapReduce based approaches to implementing distributed
  semantic storage

Solution?
---------

.. image:: images/architecture.png
    :width: 12cm

Solution?
---------

* Convert XML to RDF based on certain ontology based constraints
* Use a Clusterspace for caching reasoning information
* Store data in the Cloud using Apache Cassandra

Clusterspace
------------

* Perform semantic reasoning and relationships
* Cache it once
* Make it available to all peers

Clusterspace
------------

.. image:: images/cs1.png
    :width: 11cm

Clusterspace
------------

.. image:: images/cs2.png

Clusterspace
------------

.. image:: images/cs3.png

Clusterspace
------------

.. image:: images/cs4.png

XML to RDF conversion
---------------------

Assumptions
^^^^^^^^^^^

* The tag names should be class or property names from a known ontology.
* Attribute names should be a datatype property name from an known ontology.
* The top level tags should be class names.
* The conversion considers only one level of nesting in associating data.

Datatype Properties
-------------------

.. image:: images/xml2rdf1.png
    :width: 10cm

Object Properties
-----------------

.. image:: images/xml2rdf2.png
    :width: 9cm

Inferred object references
--------------------------

.. image:: images/xml2rdf3.png
    :width: 9cm

Inferred properties
-------------------

.. image:: images/xml2rdf4.png
    :width: 9cm

Distributed RDF store
---------------------

Apache Cassandra is a distributed key-value database with upto two-level
nesting

.. code-block:: python

    { key : (column name, value) }
    OR
    { key1: { key2: (column name, value) } }

* Completely decentralized
* Data is replicated and eventually consistent
* Data is partitioned across nodes by key
* Schema-less - columns can be added dynamically

Storage layout - Concepts
-------------------------

===========   ==== ==== ==== ====
 Concept      C1   C2   C3   ...
===========   ==== ==== ==== ====
MusicArtist   A1   A5   A6   ...
Record        R9   R6   R2   ...
Track         T7   T3   T4   ...
...
===========   ==== ==== ==== ====

Storage layout - triples
------------------------

* One for S-P-O

===== ========== =============== ==== ====
  S       P           O
----- ---------- -------------------------
 ..      ..          Col          Col  Col
===== ========== =============== ==== ====
R2      type      Record
R2      title     "OK Computer"
R2      maker     A5
A5      type      MusicArtist
A5      name      "Radiohead"
A5      bio       "..."
A5      made      R2              R5   R7
===== ========== =============== ==== ====

Storage layout - triples
------------------------

* One for O-P-S

===== ====== ===
  O     P     S
----- ------ ---
..    ..     Col
===== ====== ===
A5    maker   R2
...
===== ====== ===

Evaluation
----------

* Most tests use the LUBM benchmark, one uses the DBpedia ontology
* System was implemented in Java
    * neo4j used as the graph database for the Clusterspace
    * Apache Jena and OWLAPI for handling RDF and queries
    * Pellet - DL-reasoner
* Tested on Macbook Pro (2.3Ghz quad-core, 8GB RAM)
* Distributed test on Amazon EC2, 4 large instances, Ubuntu, (dual core, 8GB
  RAM)

Evaluation
----------

.. image:: images/register-classes.png
    :width: 10cm

Evaluation
----------

.. image:: images/xml2rdf.png
    :width: 10cm

Evaluation
----------

.. image:: images/xml2rdf-accu.png
    :width: 10cm

Evaluation
----------

.. image:: images/qm.png
    :width: 10cm

Evaluation
----------

.. image:: images/single-v-cluster.png
    :width: 10cm

Conclusion
----------

* A basic attempt at conversion of XML also performs relatively well.
* The Clusterspace approach has been shown to allow high-availability and
  efficient caching of inferences obtained from an OWL reasoner
* Distributed databases are good for storage, but not with **centralized
  computation** over the data, due to network I/O.

Future work
-----------

* More accurate and free form XML to RDF conversion based on tools from
  Information Retrieval.
* Use MapReduce (**distributed computation**) over Cassandra for query processing and triple retrieval.
* Real time updates to consumers.

References
----------

\Y. Guo, Z.Pan, and J.Heflin
    "LUBM: A benchmark for OWL knowledge base systems"

\D. V. Deursen, C. Poppe, G. Martens, E. Mannens, and R. V. D. Walle
    "XML to RDF Conversion: A Generic Approach"

\A. Harth, J. Umbrich, A. Hogan, and S. Decker
    "Yars2: A federated repository for querying graph structured data from the web"

\G. Ladwig and A. Harth
    "CumulusRDF: Linked data management on key-value stores"

\J. Urbani, S. Kotoulas, E. Oren, and F. Harmelen
    "Scalable distributed reasoning using MapReduce"

\J. Myung, J. Yeon, and S.G. Lee
    "SPARQL basic graph pattern processing with iterative MapReduce"

.. raw:: pdf

    PageBreak coverPage

.. class:: title

    Thank You
