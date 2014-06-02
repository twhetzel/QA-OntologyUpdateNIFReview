import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchDataFederation {

	public static void runFederationSearch(ArrayList<String> iriFragments) throws IOException {
		for (String termId : iriFragments) {
		String prodUri = "http://nif-services.neuinfo.org/servicesv1/v1/federation/search?q="+termId;
		String betaUri = "http://beta.neuinfo.org/services/v1/federation/search?q="+termId;
		
		File file = new File("/Users/whetzel/Documents/workspace/QA-OntologyUpdateNIFReview/federation_search_results.txt"); 
				
		// if file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getName(),true);
		BufferedWriter bw = new BufferedWriter(fw);

		int prodTotalResults = queryProd(prodUri);
		System.out.println("Total results from PROD for term: "+termId+" "+prodTotalResults+" "+prodUri);
		bw.write("PROD Search results for term\t"+termId+"\t"+prodTotalResults+"\t");

		int betaTotalResults = queryBeta(betaUri);
		System.out.println("Total results from BETA for term: "+termId+" "+betaTotalResults+" "+betaUri);
		bw.write("BETA Search results for term\t"+termId+"\t"+betaTotalResults+"\t");

		//compare result totals;
		//int prodTotalResults = Integer.parseInt(prodTotal);
		//int betaTotalResults = Integer.parseInt(betaTotal);
		if (betaTotalResults > prodTotalResults) {
			System.out.println("BETA search count is bigger than PROD\n");
			bw.write("BETA search count is bigger than PROD\n");
		}
		else {
			System.out.println("PROD search count is bigger than BETA\n");
			bw.write("PROD search count is bigger than BETA\n");
		}
		bw.close();
	}
	}


private static int queryProd(String prodUri) {
	String total;
	int prodTotalResults = 0;
	try {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		URL url = new URL(prodUri);
		URLConnection con = url.openConnection();
		con.setConnectTimeout(150000); // 15 seconds
		Document doc = dBuilder.parse(con.getInputStream());
		
		//Document doc = dBuilder.parse(prodUri);
		// http://stackoverflow.com/questions/10689900/get-xml-only-immediate-children-elements-by-name by BizNuge
		doc.getDocumentElement().normalize();	
		//String root = doc.getDocumentElement().getNodeName();


		Element docEl = doc.getDocumentElement(); 
		Node childNode = docEl.getFirstChild();     
		while( childNode.getNextSibling()!=null ){          
			childNode = childNode.getNextSibling();         
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {         
				Element childElement = (Element) childNode;    
				total = childElement.getAttribute("total");
				prodTotalResults = Integer.parseInt(total);
				//System.out.println("Results Total: " + childElement.getAttribute("total"));          
			}       
			else {
				System.out.println("Result Attribute not Found");
			}
		}
	}
	catch (Exception e) {
		e.printStackTrace();
		System.out.println("Error with Federation web service for call: "+prodUri);
	}
	return prodTotalResults;
}


private static int queryBeta(String betaUri) {
	String betaTotal;
	int betaTotalResults = 0;
	String expansion = null;
	
	try {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		
		URL url = new URL(betaUri);
		URLConnection con = url.openConnection();
		con.setConnectTimeout(150000); // 15 seconds
		Document doc = dBuilder.parse(con.getInputStream());
		
		//Document doc = dBuilder.parse(betaUri);
		// http://stackoverflow.com/questions/10689900/get-xml-only-immediate-children-elements-by-name by BizNuge
		doc.getDocumentElement().normalize();	
		//String root = doc.getDocumentElement().getNodeName();

		Element docEl = doc.getDocumentElement(); 
		Node childNode = docEl.getFirstChild();  
		//System.out.println("Child Nodes: "+childNode.getNodeName());   //query
		
		Node firstChild = childNode.getFirstChild();
		//System.out.println("First child: "+childNode.getFirstChild()); //clauses
		
//		XPath xpath = XPathFactory.newInstance().newXPath();
//		XPathExpression expr = xpath.compile("/responseWrapper/descendant::*");
//		//XPathExpression expr = xpath.compile("/query/following::*");
//		
//		Object result = expr.evaluate(doc, XPathConstants.NODESET);
//		  NodeList nodes = (NodeList) result;
//		  for (int i = 0; i < nodes.getLength(); i++) {
//			  	//System.out.println(nodes.item(i).getNodeName()); 
//			  	String expansion1 = "expansion";
//			  	if (expansion1.equals(nodes.item(i).getNodeName().toString())) {
//			  		System.out.println("EXP-Value"+nodes.item(i).getNodeValue());
//			  	}
//		  }

		  
		while( childNode.getNextSibling()!=null ){          
			childNode = childNode.getNextSibling(); 
			//System.out.println("Child Nodes: "+childNode.getNodeName());
			
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {         
				Element childElement = (Element) childNode;    
				betaTotal = childElement.getAttribute("total");
				betaTotalResults = Integer.parseInt(betaTotal);
				//System.out.println("Results Total: " + childElement.getAttribute("total"));  
			}       
			else {
				System.out.println("Result Attribute not Found");
			}
		}
	}
	catch (Exception e) {
		//e.printStackTrace();
		System.out.println("Error with Federation web service for call: "+betaUri);
	}
	return betaTotalResults;
}
}


