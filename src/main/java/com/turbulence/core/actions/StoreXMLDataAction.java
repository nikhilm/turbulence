package com.turbulence.core.actions;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections.IteratorUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import org.jdom2.input.sax.XMLReaders;

import org.jdom2.input.SAXBuilder;

import org.jdom2.JDOMException;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Expander;

import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;

import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;

import org.neo4j.helpers.Predicate;

import org.neo4j.index.lucene.QueryContext;

import org.neo4j.kernel.StandardExpander;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.Uniqueness;

import org.xml.sax.SAXException;

import com.hp.hpl.jena.graph.Triple;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import com.hp.hpl.jena.util.iterator.Filter;

import com.hp.hpl.jena.util.IteratorCollection;

import com.hp.hpl.jena.vocabulary.RDF;

import com.turbulence.core.ClusterSpace;
import com.turbulence.core.TurbulenceDriver;

class DatatypePropertyMatcher {
    private static final Log logger =
        LogFactory.getLog(DatatypePropertyMatcher.class);
    private Set<Relationship> properties;

    DatatypePropertyMatcher(Node clazz) {
        TraversalDescription trav = Traversal.description()
            .breadthFirst()
            .uniqueness(Uniqueness.RELATIONSHIP_GLOBAL)
            .evaluator(Evaluators.returnWhereLastRelationshipTypeIs(ClusterSpace.PublicRelTypes.DATATYPE_RELATIONSHIP))
            .relationships(ClusterSpace.PublicRelTypes.DATATYPE_RELATIONSHIP)
            .relationships(ClusterSpace.PublicRelTypes.EQUIVALENT_CLASS)
            .relationships(ClusterSpace.PublicRelTypes.IS_A, Direction.OUTGOING);

        properties = IteratorCollection.iteratorToSet(trav.traverse(clazz).relationships().iterator());
    }

    public Relationship match(final String possibleName) {

        Filter<Relationship> filter = new Filter<Relationship>() {
            public boolean accept(Relationship o) {
                String iri = (String) o.getProperty("IRI");
                String base = null;
                try {
                    URI uri = new URI(iri);
                    if (uri.getFragment() != null)
                        base = uri.getFragment();
                    else
                        base = new File(uri.getPath()).getName();
                } catch (URISyntaxException e) {
                    return false;
                }

                //logger.warn(possibleCanon + " " + base);
                // conditions
                if (base.equals(possibleName))
                    return true;

                return false;
            }
        };

        List<Relationship> matches = IteratorCollection.iteratorToList(filter.filterKeep(properties.iterator()));
        if (matches.size() > 0)
            return matches.get(0);

        return null;
    }
}

class ObjectPropertyMatcher {
    private static final Log logger =
        LogFactory.getLog(ObjectPropertyMatcher.class);
    private Set<Relationship> properties;

    ObjectPropertyMatcher(Node clazz) {
        TraversalDescription trav = Traversal.description()
            .breadthFirst()
            .uniqueness(Uniqueness.RELATIONSHIP_GLOBAL)
            .evaluator(Evaluators.returnWhereLastRelationshipTypeIs(ClusterSpace.PublicRelTypes.OBJECT_RELATIONSHIP))
            .relationships(ClusterSpace.PublicRelTypes.OBJECT_RELATIONSHIP)
            .relationships(ClusterSpace.PublicRelTypes.EQUIVALENT_CLASS)
            .relationships(ClusterSpace.PublicRelTypes.IS_A, Direction.OUTGOING);

        properties = IteratorCollection.iteratorToSet(trav.traverse(clazz).relationships().iterator());
    }

    public Relationship match(final String possibleName) {
        Filter<Relationship> filter = new Filter<Relationship>() {
            public boolean accept(Relationship o) {
                String iri = (String) o.getProperty("IRI");
                String base = null;
                try {
                    URI uri = new URI(iri);
                    if (uri.getFragment() != null)
                        base = uri.getFragment();
                    else
                        base = new File(uri.getPath()).getName();
                } catch (URISyntaxException e) {
                    return false;
                }

                if (base.equals(possibleName))
                    return true;

                return false;
            }
        };

        List<Relationship> matches = IteratorCollection.iteratorToList(filter.filterKeep(properties.iterator()));
        if (matches.size() > 0)
            return matches.get(0);

        return null;
    }
}

interface Resolver {
    public Resource resolve();
}

class LookupResolver implements Resolver {
    String typeIRI;
    String key;
    Map<String, Map<String, Resource>> lookup;
    LookupResolver(String typeIRI, String key, Map<String, Map<String, Resource>> lookup) {
        this.typeIRI = typeIRI;
        this.key = key;
        this.lookup = lookup;
    }

    public Resource resolve() {
        if (lookup.containsKey(typeIRI) && lookup.get(typeIRI).containsKey(key))
            return lookup.get(typeIRI).get(key);
        return null;
    }
}

class ConcreteResolver implements Resolver {
    Resource resource;
    ConcreteResolver(Resource r) {
        this.resource = r;
    }

    public Resource resolve() {
        return resource;
    }
}

class MyTriple {
    private Resource subject;
    private Property predicate;
    private List<Resolver> possibleObjects;

    public MyTriple(Resource subject, Property predicate, List<Resolver> object) {
        this.subject = subject;
        this.predicate = predicate;
        this.possibleObjects = object;
    }

    public MyTriple(Resource subject, Property predicate, Resolver object) {
        this.subject = subject;
        this.predicate = predicate;
        this.possibleObjects = Arrays.asList(object);
    }

    public Statement resolve(Model m) {
        for (Resolver r : possibleObjects) {
            Resource object = r.resolve();
            if (object != null) {
                return m.createStatement(subject, predicate, object);
            }
        }
        return null;
    }
}

public class StoreXMLDataAction implements Action {
    private static final Log logger =
        LogFactory.getLog(StoreXMLDataAction.class);


    private InputStream input;
    private Index<Node> conceptIndex;
    private Map<String, Map<String, Resource>> typePropInstances;
    private List<MyTriple> needResolution;
    private Model model;

    protected StoreXMLDataAction(InputStream in) {
        input = in;
    }

    private boolean matchApproximate(String needle, Set<Node> haystack) {
        return false;
    }

    private Resolver toRDF(Element element) {
        // try to find concept
        IndexHits<Node> hits = conceptIndex.query("CONCEPT", new QueryContext("*" + element.getName()).sortByScore());

        /*Map<String, Set<Node>> msn = new HashMap<String, Set<Node>>();
        for (Node n : hits) {
            for (String c : getClassCover(n))
        }*/
        // try to match based on number of properties matching
        // element children and compared with object/datatype properties
        // and consider superclasses also

        // for now only deal with the first hit
        Node n = hits.hasNext() ? hits.next() : null;

        if (n == null)
            return null;

        Resource subject = model.createResource();

        // reference to something defined elsewhere
        if (element.getChildren().isEmpty() && element.getAttributes().isEmpty()) {
            String text = element.getTextTrim();
            if (!text.isEmpty()) {
                return new LookupResolver((String)n.getProperty("IRI"), text, typePropInstances);
            }
            return null; // return null because this is a useless tag if it doesn't have anything
        }

        // rdf:type
        model.add(model.createStatement(subject, RDF.type, model.createResource((String)n.getProperty("IRI"))));

        DatatypePropertyMatcher dpMatcher = new DatatypePropertyMatcher(n);
        ObjectPropertyMatcher opMatcher = new ObjectPropertyMatcher(n);
        for (Attribute attr : element.getAttributes()) {
            Relationship rel = dpMatcher.match(attr.getName());
            if (rel == null)
                continue;
            model.add(model.createLiteralStatement(subject, model.createProperty((String)rel.getProperty("IRI")), attr.getValue()));
            String subjectType = (String) n.getProperty("IRI");
            if (!typePropInstances.containsKey(subjectType))
                typePropInstances.put(subjectType, new HashMap<String, Resource>());
            typePropInstances.get(subjectType).put(attr.getValue(), subject);
        }

        for (Element child : element.getChildren()) {
            // a possible data/object property
            if (child.getChildren().isEmpty()) {
                Relationship dpRel = dpMatcher.match(child.getName());

                Relationship opRel = opMatcher.match(child.getName());

                if (dpRel != null) {
                    model.add(model.createLiteralStatement(subject, model.createProperty((String)dpRel.getProperty("IRI")), child.getText()));
                    String subjectType = (String) n.getProperty("IRI");
                    if (!typePropInstances.containsKey(subjectType))
                        typePropInstances.put(subjectType, new HashMap<String, Resource>());
                    typePropInstances.get(subjectType).put(child.getTextTrim(), subject);
                }
                if (opRel != null) {
                    TraversalDescription cover = Traversal.description().breadthFirst().relationships(ClusterSpace.PublicRelTypes.EQUIVALENT_CLASS).relationships(ClusterSpace.PublicRelTypes.IS_A, Direction.INCOMING);
                    Set<Node> ranges = IteratorCollection.iteratorToSet(cover.traverse(opRel.getEndNode()).nodes().iterator());
                    List<Resolver> resolvers = new LinkedList<Resolver>();
                    for (Node r : ranges) {
                        // because the property could be any of the subclass
                        // types as well, we need to add a resolver for each
                        // pick the one that succeeds
                        Resolver res = new LookupResolver((String)r.getProperty("IRI"), child.getTextTrim(), typePropInstances);
                        resolvers.add(res);
                    }
                    //logger.warn("opRel " + n.getProperty("IRI") + " " + child.getName() + " match " + opRel.getProperty("IRI"));
                    needResolution.add(new MyTriple(subject, model.createProperty((String)opRel.getProperty("IRI")), resolvers));
                }

                // if the child is empty, and it isn't a relationship
                // then it is a useless tag even if it is a concept
                // otherwise the above conditionals have handled this child
                continue;
            }

            Relationship opRel = opMatcher.match(child.getName());
            if (opRel != null) {
                for (Element subChild : child.getChildren()) {
                    Resolver val = toRDF(subChild);
                    if (val == null)
                        continue;

                    if (val instanceof ConcreteResolver)
                        model.add(model.createStatement(subject, model.createProperty((String)opRel.getProperty("IRI")), val.resolve()));
                    else
                        needResolution.add(new MyTriple(subject, model.createProperty((String)opRel.getProperty("IRI")), val));
                }

                // FIXME
                // it is possible that a relationship's subchildren are
                // a direct representation of an instance, without being
                // wrapped in concept tags. This case is currently not handled
                // example:
                // <hasFriend><name>Nikhil</name></hasFriend>
                // rather than
                // <hasFriend><Friend><name>Nikhil</name></Friend></hasFriend>
                //
                // the approach would be to find the cover of the opRel's
                // range, and see for each of them, their data/object
                // properties and compare them against the sub tags and go for
                // a statistical match
                continue;
            }
            
            // otherwise this could be an concept, in which case we'll try to
            // infer the relationship based on range
            IndexHits<Node> ranges = conceptIndex.query("CONCEPT", new
                    QueryContext("*" + child.getName()).sortByScore()); final
                Node cn = ranges.hasNext() ? ranges.next() : null;

            if (cn != null) {
                // try to find a compatible relationship
                Expander expander = StandardExpander.DEFAULT.add(ClusterSpace.PublicRelTypes.OBJECT_RELATIONSHIP, Direction.OUTGOING);

                PathFinder<Path> pf = GraphAlgoFactory.allSimplePaths(expander, 1);
                TraversalDescription trav = Traversal.description()
                    .breadthFirst()
                    .uniqueness(Uniqueness.RELATIONSHIP_GLOBAL)
                    .relationships(ClusterSpace.PublicRelTypes.EQUIVALENT_CLASS)
                    .relationships(ClusterSpace.PublicRelTypes.IS_A, Direction.OUTGOING);

                String rel = null;
                for (Node srcClazz : trav.traverse(n).nodes()) {
                    for (Node destClazz : trav.traverse(cn).nodes()) {
                        Path path = pf.findSinglePath(srcClazz, destClazz);
                        if (path != null) {
                            // FIXME we probably want to do scoring, but for now
                            // choose the first one
                            rel = (String) path.lastRelationship().getProperty("IRI");
                            break;
                        }
                    }
                }

                // Get the 'resource' that this tag is
                Resolver object = toRDF(child);
                if (rel != null && object != null)
                    needResolution.add(new MyTriple(subject, model.createProperty(rel), object));
            }

            // it is possible that
            // the concept
            // child has subtags
            // they can either define a complete new instance
            // whose type we can infer from the subtag name
            // or we will have to infer based on tag collection
        }

        return new ConcreteResolver(subject);
    }

    private void addTriple(MyTriple trip) {
        needResolution.add(trip);
    }

    public Result perform() {
        Result r = new Result();

        conceptIndex = TurbulenceDriver.getClusterSpace().index().forNodes("conceptIndex");
        needResolution = new LinkedList<MyTriple>();
        typePropInstances = new HashMap<String, Map<String, Resource>>();
        model = ModelFactory.createDefaultModel();

        try {
            SAXBuilder b = new SAXBuilder(XMLReaders.NONVALIDATING);
            Document document = b.build(input);
            for (Element el : document.getRootElement().getChildren()) {
                toRDF(el);
            }

            for (Map.Entry<String, Map<String, Resource>> entry : typePropInstances.entrySet()) {
                logger.warn(entry.getKey());
                for (Map.Entry<String, Resource> subEntry : entry.getValue().entrySet()) {
                    logger.warn("    " + subEntry.getKey() + ": " + subEntry.getValue());
                }
            }

            for (MyTriple t : needResolution) {
                Statement s = t.resolve(model);
                if (s != null)
                    model.add(s);
            }
            r.success = true;
        } catch (JDOMException e) {
            r.success = false;
            r.error = TurbulenceError.BAD_XML_DATA;
            r.message = e.getMessage();
        } catch (IOException e) {
            r.success = false;
            r.error = TurbulenceError.IO_ERROR;
            r.message = e.getMessage();
        }

        model.write(System.out, "RDF/XML-ABBREV");
        return r;
    }
}
