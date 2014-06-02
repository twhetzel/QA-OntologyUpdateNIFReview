import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;


public class Main {

	/**
	 * Test data federation queries with concepts that are in modules
	 * being removed from the NIFSTD import closure
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("Trying to load ontology...");
			shouldUseIRIMappers();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}	
	}


	/** 
	 * Loads the ontology  
	 * 
	 * @throws OWLOntologyCreationException 
	 **/
	@Test
	public static void shouldUseIRIMappers() throws OWLOntologyCreationException {
		// Load ontology from web
		//TODO Pass ontology location as a command-line argument 
		IRI ONTOLOGY = IRI.create("http://ontology.neuinfo.org/NIF/BiomaterialEntities/NIF-Subcellular.owl");
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.loadOntology(ONTOLOGY); 

		// Load ontology from local file
		/**File file = new File("/Users/whetzel/Desktop/NIF-Subcellular.owl");
        OWLOntology localOntology = manager.loadOntologyFromOntologyDocument(file);
		 */
		System.out.println("Loaded ontology: " + ontology);

		getAllClasses(manager, ontology);
		System.out.println("** Program Complete **");
	}
	

/**
 * Iterate through all classes in module/ontology file to be removed from the import closure
 * 
 * @param manager
 * @param ontology
 */
	private static void getAllClasses(OWLOntologyManager manager, OWLOntology ontology) {
		Set<OWLClass> allClasses = ontology.getClassesInSignature();
		int size = allClasses.size();
		ArrayList<String> terms = new ArrayList<String>();
		System.out.println("Total number of classes: "+size);
		for (OWLClass owlClass : allClasses) {
			//System.out.println(owlClass);
			// Get IRI Fragment
			String iri = owlClass.getIRI().getFragment();
			System.out.println("OWLCLASS IRI: "+iri+"\n");
			terms.add(iri);
		}	
		checkForData(terms);
	}

	private static void checkForData(ArrayList<String> iriFragments) {
		System.out.println("FRAGMENTS: "+iriFragments);
		try {
			SearchDataFederation.runFederationSearch(iriFragments);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
