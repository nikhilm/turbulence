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

Problem 1 - Extracting semantic information
-------------------------------------------

.. image:: images/xml2rdfcode.png
    :width: 12cm

Problem 1 - Extracting semantic information
-------------------------------------------

.. image:: images/xml2rdfcode1.png
    :width: 12cm

Problem 2 - Data management
---------------------------

* Large scale & Big data
* Should be:
    * Quick
    * Scalable
    * Fault-tolerant
    * Highly available

Background
----------

* XML to RDF - loosely structured, needs a template to convert
* YARS2 [#yars2]_ - federated repository for semantic data with high performance
  distributed queries
* Cassandra/Hadoop/MapReduce based approaches to implementing distributed
  semantic storage

.. [#yars2] \A. Harth, J. Umbrich, A. Hogan, and S. Decker, "Yars2: A federated repository for querying graph structured data from the web"

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

.. raw:: pdf

    Spacer 0, 30

* Perform semantic reasoning and relationships

.. raw:: pdf

    Spacer 0, 20

* Cache it once

.. raw:: pdf

    Spacer 0, 20

* Make it available to all peers

Clusterspace
------------

.. image:: images/cs1.png
    :width: 12cm

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
    :width: 13cm

Object Properties
-----------------

.. image:: images/xml2rdf2.png
    :width: 13cm

Inferred object references
--------------------------

.. image:: images/xml2rdf3.png
    :width: 13cm

Inferred properties
-------------------

.. image:: images/xml2rdf4.png
    :width: 13cm

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

===========   ======   ====   ==== ====
 Concept      C1        C2     C3   ...
===========   ======   ====   ==== ====
MusicArtist   **A2**   *A5*   *A6*  ...
Record        **R1**   *R6*   *R2*  ...
...
===========   ======   ====   ==== ====

Storage layout - Data triples
-----------------------------

======= ========== =============== ==== ====
Subject  Predicate           Object(s)
------- ---------- -------------------------
 ..        ..          Col          Col  Col
======= ========== =============== ==== ====
R1        type      Record
R1        title     "OK Computer"
R1        maker     A2
A2        type      MusicArtist
A2        name      "Radiohead"
A2        bio       "..."
======= ========== =============== ==== ====

====== ========= ===== === ===
Object Predicate Subject(s)
------ --------- -------------
..     ..          Col Col Col
====== ========= ===== === ===
A2     maker      R1
====== ========= ===== === ===

Handling queries
----------------

.. raw:: pdf

    Spacer 0, 50

.. code-block:: sparql

    SELECT ?R WHERE {
        ?R a Manifestation .
        ?R maker ?A .
        ?A name "Radiohead" .
    }

Query part 1
------------

.. raw:: pdf

    Spacer 0, 20

.. code-block:: sparql

    ?R a Manifestation

.. raw:: pdf

    Spacer 0, 10

Record and Track are also Manifestations

.. raw:: pdf

    Spacer 0, 10

.. image:: images/csmanifest.png
    :width: 8cm

Get instances
-------------

===========   ======   ====   ==== ====
 Concept      C1        C2     C3   ...
===========   ======   ====   ==== ====
MusicArtist     A2      A5     A6   ...
**Record**      R1      R6     R2   ...
===========   ======   ====   ==== ====

.. code-block:: sparql

    R1 a Manifestation
    R6 a Manifestation
    R2 a Manifestation

Query part 2
------------

.. code-block:: sparql

    ?R maker ?A .
    (?R is now {R1, R6, R2})

======= ========== =============== ==== ====
Subject  Predicate           Object(s)
------- ---------- -------------------------
 ..        ..          Col          Col  Col
======= ========== =============== ==== ====
R1        maker     **A2**
R1        title     "OK Computer"
...       ...       ...
R2        maker     **A5** (say)
...       ...       ...
R6        maker     **A9** (say)
======= ========== =============== ==== ====

Query part 3
------------

.. code-block:: sparql

    ?A name "Radiohead"
    (?A is now {A2, A5, A9})

======= ========== =============== ==== ====
Subject  Predicate           Object(s)
------- ---------- -------------------------
 ..        ..          Col          Col  Col
======= ========== =============== ==== ====
R1        type      Record
...
A5        name       The Beatles
...
**A2**  **name**   **"Radiohead"**
...
A9        name       Stars
...
======= ========== =============== ==== ====

Finishing up the query
----------------------

.. raw:: pdf

    Spacer 0, 60

.. code-block:: sparql

    ?A is { A2 }
    ?R maker ?A => ?R maker A2
    ?R is { R1 }

So the answer is ?R = { R1 }

Evaluation
----------

* Most tests use the LUBM benchmark suite [#lubm]_, one uses the DBpedia [#db]_ ontology
* neo4j [#neo4j]_ used as the graph database for the Clusterspace
* Apache Jena [#jena]_ and OWLAPI [#owlapi]_ for handling RDF and queries
* Pellet [#pellet]_ - DL-reasoner
* Tested on Macbook Pro and Amazon EC2 (4 machine cluster)

.. [#lubm] \Y. Guo, Z.Pan, and J.Heflin, "LUBM: A benchmark for OWL knowledge base systems"
.. [#db] http://dbpedia.org
.. [#neo4j] http://neo4j.org
.. [#jena] http://incubator.apache.org/jena/
.. [#owlapi] http://owlapi.sourceforge.net
.. [#pellet] http://clarkparsia.com/pellet

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
