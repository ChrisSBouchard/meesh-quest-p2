package cmsc420.meeshquest.part2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cmsc420.xml.XmlUtility;

/**
 * Skeleton implementation of MeeshQuest, Part 2, CMSC 420-0201, Fall 2019.
 * This does the following: (1) opens the input/output files;
 * (2) validates and parses the xml input;
 * (3) iterates through the input command nodes, but doesn't do anything
 * (4) prints the results.
 */
public class MeeshQuest {

	// --------------------------------------------------------------------------------------------
	//  Uncomment these to read from standard input and output (USE THESE FOR YOUR FINAL SUBMISSION)
	//	private static final boolean USE_STD_IO = true; 
	//	private static String inputFileName = "";
	//	private static String outputFileName = "";
	// --------------------------------------------------------------------------------------------
	//  Uncomment these to read from a file (USE THESE FOR YOUR TESTING ONLY)
	private static final boolean USE_STD_IO = false;
	private static String inputFileName = "test/mytest-input-5.xml";
	private static String outputFileName = "test/mytest-output-5.xml";
	// --------------------------------------------------------------------------------------------

	public static void main(String[] args) {

		// configure to read from file rather than standard input/output
		if (!USE_STD_IO) {
			try {
				System.setIn(new FileInputStream(inputFileName));
				System.setOut(new PrintStream(outputFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		// results will be stored here
		Document resultsDoc = null;
		try {
			// generate XML document for results
			resultsDoc = XmlUtility.getDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}


		try {
			Element root = resultsDoc.createElement("results");
			resultsDoc.appendChild(root);
			// validate and parse XML input
			Document input = XmlUtility.validateNoNamespace(System.in);
			// get input document root node
			Element rootNode = input.getDocumentElement();
			int spatialWidth = Integer.parseInt(rootNode.getAttribute("spatialWidth"));
			int spatialHeight = Integer.parseInt(rootNode.getAttribute("spatialHeight"));
			// get list of all nodes in document
			SGKDTree geoTree = new SGKDTree(resultsDoc);
			BSTree searchTree = new BSTree(resultsDoc);
			final NodeList nl = rootNode.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				// process only commands (ignore comments)
				if (nl.item(i).getNodeType() == Document.ELEMENT_NODE) {
					// get next command to process
					Element command = (Element) nl.item(i); // (ignore warning - just a skeleton)

					if(command.getNodeName().equals("createCity")) {
						//create city
						String cityName = command.getAttribute("name");
						int x = Integer.parseInt(command.getAttribute("x"));
						int y = Integer.parseInt(command.getAttribute("y"));
						int radius = Integer.parseInt(command.getAttribute("radius"));
						String color = command.getAttribute("color");
						City c = new City(cityName, x, y, radius, color);
						//Create City
						if(x > spatialWidth || y > spatialHeight) {
							Element error = resultsDoc.createElement("error");
							error.setAttribute("type", "cityOutOfBounds");
							root.appendChild(error);

							Element commandName = resultsDoc.createElement("command");
							commandName.setAttribute("name", command.getNodeName());
							error.appendChild(commandName);

							Element parameters = resultsDoc.createElement("parameters");
							error.appendChild(parameters);

							Element name = resultsDoc.createElement("name");
							name.setAttribute("value", cityName);
							parameters.appendChild(name);

							Element xElement = resultsDoc.createElement("x");
							xElement.setAttribute("value", ""+x);
							parameters.appendChild(xElement);

							Element yElement = resultsDoc.createElement("y");
							yElement.setAttribute("value", ""+y);
							parameters.appendChild(yElement);

							Element rElement = resultsDoc.createElement("radius");
							rElement.setAttribute("value", ""+radius);
							parameters.appendChild(rElement);

							Element coElement = resultsDoc.createElement("color");
							coElement.setAttribute("value", color);
							parameters.appendChild(coElement);


						} else if(geoTree.find(c) != null) {
							
							Element error = resultsDoc.createElement("error");
							error.setAttribute("type", "duplicateCityCoordinates");
							root.appendChild(error);

							Element errorCommand = resultsDoc.createElement("command");
							errorCommand.setAttribute("name", command.getNodeName());
							error.appendChild(errorCommand);

							Element parameters = resultsDoc.createElement("parameters");
							error.appendChild(parameters);

							Element cElement = resultsDoc.createElement("name");
							cElement.setAttribute("value", cityName);
							parameters.appendChild(cElement);

							Element xElement = resultsDoc.createElement("x");
							xElement.setAttribute("value", ""+x);
							parameters.appendChild(xElement);

							Element yElement = resultsDoc.createElement("y");
							yElement.setAttribute("value", ""+y);
							parameters.appendChild(yElement);

							Element rElement = resultsDoc.createElement("radius");
							rElement.setAttribute("value", ""+radius);
							parameters.appendChild(rElement);

							Element coElement = resultsDoc.createElement("color");
							coElement.setAttribute("value", color);
							parameters.appendChild(coElement);
							
							
						} else if(searchTree.containsCity(cityName)) {
							Element error = resultsDoc.createElement("error");
							error.setAttribute("type", "duplicateCityName");
							root.appendChild(error);

							Element errorCommand = resultsDoc.createElement("command");
							errorCommand.setAttribute("name", command.getNodeName());
							error.appendChild(errorCommand);

							Element parameters = resultsDoc.createElement("parameters");
							error.appendChild(parameters);

							Element cElement = resultsDoc.createElement("name");
							cElement.setAttribute("value", cityName);
							parameters.appendChild(cElement);

							Element xElement = resultsDoc.createElement("x");
							xElement.setAttribute("value", ""+x);
							parameters.appendChild(xElement);

							Element yElement = resultsDoc.createElement("y");
							yElement.setAttribute("value", ""+y);
							parameters.appendChild(yElement);

							Element rElement = resultsDoc.createElement("radius");
							rElement.setAttribute("value", ""+radius);
							parameters.appendChild(rElement);

							Element coElement = resultsDoc.createElement("color");
							coElement.setAttribute("value", color);
							parameters.appendChild(coElement);


						} else {

							geoTree.insert(c);
							searchTree.insert(c);

							//city created
							Element success = resultsDoc.createElement("success");
							root.appendChild(success);

							Element com = resultsDoc.createElement("command");
							com.setAttribute("name", "createCity");
							success.appendChild(com);

							Element parameters = resultsDoc.createElement("parameters");
							success.appendChild(parameters);

							Element cElement = resultsDoc.createElement("name");
							cElement.setAttribute("value", cityName);
							parameters.appendChild(cElement);

							Element xElement = resultsDoc.createElement("x");
							xElement.setAttribute("value", ""+x);
							parameters.appendChild(xElement);

							Element yElement = resultsDoc.createElement("y");
							yElement.setAttribute("value", ""+y);
							parameters.appendChild(yElement);

							Element rElement = resultsDoc.createElement("radius");
							rElement.setAttribute("value", ""+radius);
							parameters.appendChild(rElement);

							Element coElement = resultsDoc.createElement("color");
							coElement.setAttribute("value", color);
							parameters.appendChild(coElement);

							Element output = resultsDoc.createElement("output");
							success.appendChild(output);
						}

					} else if(command.getNodeName().equals("printBinarySearchTree")) {
						if(searchTree.isEmpty()) {
							Element printsearchtreeerror = resultsDoc.createElement("error");
							root.appendChild(printsearchtreeerror);
							printsearchtreeerror.setAttribute("type", "mapIsEmpty");

							Element commandName = resultsDoc.createElement("command");
							commandName.setAttribute("name", command.getNodeName());
							printsearchtreeerror.appendChild(commandName);

							Element parameters = resultsDoc.createElement("parameters");
							printsearchtreeerror.appendChild(parameters);
						} else {
							Element success = resultsDoc.createElement("success");
							root.appendChild(success);

							Element commandName = resultsDoc.createElement("command");
							commandName.setAttribute("name", "printBinarySearchTree");
							success.appendChild(commandName);

							Element parameters = resultsDoc.createElement("parameters");
							success.appendChild(parameters);

							Element output = resultsDoc.createElement("output");
							success.appendChild(output);

							Element binarysearchtree = resultsDoc.createElement("binarysearchtree");
							output.appendChild(binarysearchtree);

							binarysearchtree.appendChild(searchTree.print());
						}
					} else if(command.getNodeName().equals("printKdTree") ) {
						if(geoTree.isEmpty()) {
							Element error = resultsDoc.createElement("error");
							root.appendChild(error);
							error.setAttribute("type", "mapIsEmpty");

							Element commandName = resultsDoc.createElement("command");
							commandName.setAttribute("name", command.getNodeName());
							error.appendChild(commandName);

							Element parameters = resultsDoc.createElement("parameters");
							error.appendChild(parameters);
						} else {
							Element success = resultsDoc.createElement("success");
							root.appendChild(success);

							Element commandName = resultsDoc.createElement("command");
							commandName.setAttribute("name", "printKdTree");
							success.appendChild(commandName);

							Element parameters = resultsDoc.createElement("parameters");
							success.appendChild(parameters);

							Element output = resultsDoc.createElement("output");
							success.appendChild(output);

							geoTree.print(output);
						}
					} else if(command.getNodeName().equals("deleteCity")) {
					
						String cityName = command.getAttribute("name");
						if(searchTree.containsCity(cityName)) {

							Element success = resultsDoc.createElement("success");
							root.appendChild(success);

							Element commandName = resultsDoc.createElement("command");
							commandName.setAttribute("name", command.getNodeName());
							success.appendChild(commandName);

							Element parameters = resultsDoc.createElement("parameters");
							success.appendChild(parameters);

							Element name = resultsDoc.createElement("name");
							name.setAttribute("value", cityName);
							parameters.appendChild(name);

							Element output = resultsDoc.createElement("output");
							success.appendChild(output);

							City deletedNode = searchTree.getCity(cityName);

							Element cityDeleted = resultsDoc.createElement("cityDeleted");
							cityDeleted.setAttribute("color", deletedNode.getColor());
							cityDeleted.setAttribute("name", deletedNode.getName());
							cityDeleted.setAttribute("radius", ""+deletedNode.getRadius());
							cityDeleted.setAttribute("x", ""+deletedNode.getX());
							cityDeleted.setAttribute("y", ""+deletedNode.getY());

							geoTree.delete(deletedNode);
							searchTree.delete(cityName);

							output.appendChild(cityDeleted);
						} else {
							Element error = resultsDoc.createElement("error");
							error.setAttribute("type", "cityDoesNotExist");
							root.appendChild(error);

							Element commandName = resultsDoc.createElement("command");
							commandName.setAttribute("name", command.getNodeName());
							error.appendChild(commandName);

							Element parameters = resultsDoc.createElement("parameters");
							error.appendChild(parameters);

							Element name = resultsDoc.createElement("name");
							name.setAttribute("value", cityName);
							parameters.appendChild(name);

						}
					} else if(command.getNodeName().equals("clearAll")) {
						searchTree.clear();
						geoTree.clear();

						Element success = resultsDoc.createElement("success");
						root.appendChild(success);

						Element commandName = resultsDoc.createElement("command");
						commandName.setAttribute("name", command.getNodeName());
						success.appendChild(commandName);

						Element parameters = resultsDoc.createElement("parameters");
						success.appendChild(parameters);

						Element output = resultsDoc.createElement("output");
						success.appendChild(output);
					} else if(command.getNodeName().equals("listCities")) {

						String sortCat = command.getAttribute("sortBy");

						if(searchTree.isEmpty()) {
							Element error = resultsDoc.createElement("error");
							error.setAttribute("type", "noCitiesToList");
							root.appendChild(error);

							Element commandName = resultsDoc.createElement("command");
							commandName.setAttribute("name", command.getNodeName());
							error.appendChild(commandName);

							Element parameters = resultsDoc.createElement("parameters");
							error.appendChild(parameters);

							Element sortBy = resultsDoc.createElement("sortBy");
							sortBy.setAttribute("value", sortCat);
							parameters.appendChild(sortBy);

						} else {

							Element success = resultsDoc.createElement("success");
							root.appendChild(success);

							Element com = resultsDoc.createElement("command");
							com.setAttribute("name", "listCities");
							success.appendChild(com);

							Element parameters = resultsDoc.createElement("parameters");
							success.appendChild(parameters);

							Element sortBy = resultsDoc.createElement("sortBy");
							sortBy.setAttribute("value", sortCat);
							parameters.appendChild(sortBy);

							Element output = resultsDoc.createElement("output");
							success.appendChild(output);

							Element cityList = resultsDoc.createElement("cityList");
							output.appendChild(cityList);

							ArrayList<City> a = new ArrayList<City>();

							searchTree.inorderBST(a);

							for(City c : a) {
								Element city = resultsDoc.createElement("city");
								city.setAttribute("name", c.getName());
								city.setAttribute("x", ""+c.getX());
								city.setAttribute("y", ""+c.getY());
								city.setAttribute("radius", ""+c.getRadius());
								city.setAttribute("color", c.getColor());
								cityList.appendChild(city);
							}

						}

					} else if(command.getNodeName().equals("nearestNeighbor")) {
						
						int x = Integer.parseInt(command.getAttribute("x"));
						int y = Integer.parseInt(command.getAttribute("y"));
						
						if(x > spatialWidth || y > spatialHeight) {
							Element error = resultsDoc.createElement("error");
							error.setAttribute("type", "queryOutOfBounds");
							root.appendChild(error);
							
							Element comElt = resultsDoc.createElement("command");
							comElt.setAttribute("name", command.getNodeName());
							error.appendChild(comElt);
							
							Element parameters = resultsDoc.createElement("parameters");
							error.appendChild(parameters);
							
							Element xElt = resultsDoc.createElement("x");
							xElt.setAttribute("value", ""+x);
							parameters.appendChild(xElt);
							
							Element yElt = resultsDoc.createElement("y");
							yElt.setAttribute("value", ""+ y);
							parameters.appendChild(yElt);
							
						}  else if(geoTree.isEmpty()) {
							
							Element error = resultsDoc.createElement("error");
							root.appendChild(error);
							error.setAttribute("type", "mapIsEmpty");

							Element commandName = resultsDoc.createElement("command");
							commandName.setAttribute("name", command.getNodeName());
							error.appendChild(commandName);

							Element parameters = resultsDoc.createElement("parameters");
							error.appendChild(parameters);
							
							Element xElt = resultsDoc.createElement("x");
							xElt.setAttribute("value", "" +x);
							parameters.appendChild(xElt);
							
							Element yElt = resultsDoc.createElement("y");
							yElt.setAttribute("value", ""+y);
							parameters.appendChild(yElt);
							
							
						} else {
						
							
							Element success = resultsDoc.createElement("success");
							root.appendChild(success);
							
							Element comElt = resultsDoc.createElement("command");
							comElt.setAttribute("name", command.getNodeName());
							success.appendChild(comElt);
							
							Element parameters = resultsDoc.createElement("parameters");
							success.appendChild(parameters);
							
							Element xElt = resultsDoc.createElement("x");
							xElt.setAttribute("value", ""+x);
							parameters.appendChild(xElt);
							
							Element yElt = resultsDoc.createElement("y");
							yElt.setAttribute("value", ""+ y);
							parameters.appendChild(yElt);
							
							Element output = resultsDoc.createElement("output");
							success.appendChild(output);
							
							City query = new City("Query", x, y, 0, "color");
							City closest = geoTree.nearNeigh(query);
							
							Element neighbor = resultsDoc.createElement("nearestNeighbor");
							neighbor.setAttribute("color", closest.getColor());
							neighbor.setAttribute("name", closest.getName());
							neighbor.setAttribute("radius", ""+closest.getRadius());
							neighbor.setAttribute("x", ""+closest.getX());
							neighbor.setAttribute("y", ""+closest.getY());
							
							output.appendChild(neighbor);
							
						}
						
					}

				}
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {

			Element fatalError = resultsDoc.createElement("fatalError");
			resultsDoc.appendChild(fatalError);

		} finally {
			try {
				// print the contents of your results document
				XmlUtility.print(resultsDoc);
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}
	}
}

