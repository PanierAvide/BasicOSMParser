/*
	Copyright 2014 Adrien PAVIE
	
	This file is part of BasicOSMParser.
	
	BasicOSMParser is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	
	BasicOSMParser is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with BasicOSMParser. If not, see <http://www.gnu.org/licenses/>.
 */

package info.pavie.basicosmparser.controller;

import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Node;
import info.pavie.basicosmparser.model.Relation;
import info.pavie.basicosmparser.model.Way;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * OSMParser parses XML file (OSM database extracts) and creates corresponding Java objects.
 * @author Adrien PAVIE
 */
public class OSMParser extends DefaultHandler {
//ATTRIBUTES
	/** The parsed OSM elements **/
	private Map<String,Element> elements;
	/** The current read element **/
	private Element current;

//CONSTRUCTOR
	public OSMParser() {
		super();
	}
	
//OTHER METHODS
	/**
	 * Parses a XML file and creates OSM Java objects
	 * @param f The OSM database extract, in XML format, as a file
	 * @return The corresponding OSM objects as a Map. Keys are elements ID, and values are OSM elements objects.
	 * @throws IOException If an error occurs during file reading
	 * @throws SAXException If an error occurs during parsing
	 */
	public Map<String,Element> parse(File f) throws IOException, SAXException {
		//File check
		if(!f.exists() || !f.isFile()) {
			throw new FileNotFoundException();
		}
		
		if(!f.canRead()) {
			throw new IOException("Can't read file");
		}
		
		return parse(new InputSource(new FileReader(f)));
	}
	
	/**
	 * Parses a XML file and creates OSM Java objects
	 * @param s The OSM database extract, in XML format, as a String
	 * @return The corresponding OSM objects as a Map. Keys are elements ID, and values are OSM elements objects.
	 * @throws IOException If an error occurs during file reading
	 * @throws SAXException If an error occurs during parsing
	 */
	public Map<String,Element> parse(String s) throws SAXException, IOException {
		return parse(new InputSource(new ByteArrayInputStream(s.getBytes("UTF-8"))));
	}
	
	/**
	 * Parses a XML input and creates OSM Java objects
	 * @param input The OSM database extract, in XML format, as an InputSource
	 * @return The corresponding OSM objects as a Map. Keys are elements ID, and values are OSM elements objects.
	 * @throws IOException If an error occurs during reading
	 * @throws SAXException If an error occurs during parsing
	 */
	public Map<String,Element> parse(InputSource input) throws SAXException, IOException {
		//Init elements set
		elements = new HashMap<String,Element>();

		//Start parsing
		XMLReader xr = XMLReaderFactory.createXMLReader();
		xr.setContentHandler(this);
		xr.setErrorHandler(this);
	    xr.parse(input);
		
		return elements;
	}
	
	/**
	 * Get an object ID, in this format: X000000, where X is the object type (N for nodes, W for ways, R for relations).
	 * @param type The object type (node, way or relation)
	 * @param ref The object ID in a given type
	 * @return The object ID, unique for all types
	 */
	private String getId(String type, String ref) {
		String result = null;
		
		switch(type) {
			case "node":
				result="N"+ref;
				break;
			case "way":
				result="W"+ref;
				break;
			case "relation":
				result="R"+ref;
				break;
			default:
				throw new RuntimeException("Unknown element type: "+type);
		}
		
		return result;
	}
	
/*
 * Event handlers
 */
//	@Override
//	public void startDocument() throws SAXException {
//		// TODO Auto-generated method stub
//		super.startDocument();
//	}
//
//	@Override
//	public void endDocument() throws SAXException {
//		// TODO Auto-generated method stub
//		super.endDocument();
//	}
//	
//	@Override
//	public void characters(char[] ch, int start, int length) throws SAXException {
//		// TODO Auto-generated method stub
//		super.characters(ch, start, length);
//	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		super.endElement(uri, localName, qName);
		
		if(localName.equals("node") || localName.equals("way") || localName.equals("relation")) {
			//Add element to list, and delete current
			if(current != null) {
				if( (localName.equals("way") && ((Way) current).getNodes().size() >= 2)
						|| localName.equals("node")
						|| (localName.equals("relation") && ((Relation) current).getMembers().size() > 0)) {
					
					elements.put(current.getId(), current);
				}
				current = null;
			}
		}
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		
		//Case of node
		if(localName.equals("node")) {
			Node n = new Node(
							Long.parseLong(attributes.getValue("id")),
							Double.parseDouble(attributes.getValue("lat")),
							Double.parseDouble(attributes.getValue("lon"))
							);
			n.setUser(attributes.getValue("user"));
			
			if(attributes.getValue("uid") != null) {
				n.setUid(Long.parseLong(attributes.getValue("uid")));
			}
			
			n.setVisible(Boolean.parseBoolean(attributes.getValue("visible")));
			
			if(attributes.getValue("version") != null) {
				n.setVersion(Integer.parseInt(attributes.getValue("version")));
			}
			
			if(attributes.getValue("changeset") != null) {
				n.setChangeset(Long.parseLong(attributes.getValue("changeset")));
			}
			
			n.setTimestamp(attributes.getValue("timestamp"));
			current = n;
		}
		//Case of way
		else if(localName.equals("way")) {
			Way w = new Way(Long.parseLong(attributes.getValue("id")));
			w.setUser(attributes.getValue("user"));
			
			if(attributes.getValue("uid") != null) {
				w.setUid(Long.parseLong(attributes.getValue("uid")));
			}
			
			w.setVisible(Boolean.parseBoolean(attributes.getValue("visible")));
			
			if(attributes.getValue("version") != null) {
				w.setVersion(Integer.parseInt(attributes.getValue("version")));
			}
			
			if(attributes.getValue("changeset") != null) {
				w.setChangeset(Long.parseLong(attributes.getValue("changeset")));
			}
			
			w.setTimestamp(attributes.getValue("timestamp"));
			current = w;
		}
		//Case of way node
		else if(localName.equals("nd")) {
			((Way) current).addNode((Node) elements.get("N"+attributes.getValue("ref")));
		}
		//Case of relation
		else if(localName.equals("relation")) {
			Relation r = new Relation(Long.parseLong(attributes.getValue("id")));
			r.setUser(attributes.getValue("user"));
			
			if(attributes.getValue("uid") != null) {
				r.setUid(Long.parseLong(attributes.getValue("uid")));
			}
			
			r.setVisible(Boolean.parseBoolean(attributes.getValue("visible")));
			
			if(attributes.getValue("version") != null) {
				r.setVersion(Integer.parseInt(attributes.getValue("version")));
			}
			
			if(attributes.getValue("changeset") != null) {
				r.setChangeset(Long.parseLong(attributes.getValue("changeset")));
			}
			
			r.setTimestamp(attributes.getValue("timestamp"));
			current = r;
		}
		//Case of relation member
		else if(localName.equals("member")) {
			String refMember = getId(attributes.getValue("type"), attributes.getValue("ref"));
			
			//If member isn't contained in data, create stub object
			Element elemMember = null;
			if(!elements.containsKey(refMember)) {
				switch(attributes.getValue("type")) {
					case "node":
						elemMember = new Node(Long.parseLong(attributes.getValue("ref")), 0, 0);
						break;
					case "way":
						elemMember = new Way(Long.parseLong(attributes.getValue("ref")));
						break;
					case "relation":
						elemMember = new Relation(Long.parseLong(attributes.getValue("ref")));
						break;
				}
			} else {
				elemMember = elements.get(refMember);
			}
			
			//Add member
			((Relation) current).addMember(
					attributes.getValue("role"),
					elemMember
					);
		}
		//Case of tag
		else if(localName.equals("tag")) {
			if(current != null) {
				current.addTag(attributes.getValue("k"), attributes.getValue("v"));
			}
		}
	}
	
	/**
	 * Displays some statistics about given elements
	 * @param elements The elements
	 */
	public static void printStatistics(Map<String,Element> elements) {
		int nbNodes = 0, nbWays = 0, nbRels = 0;
		
		for(Element e : elements.values()) {
			if(e instanceof Node) { nbNodes++; }
			else if(e instanceof Way) { nbWays++; }
			else if(e instanceof Relation) { nbRels++; }
		}
		
		System.out.println("= Elements statistics =");
		System.out.println("* Nodes:\t"+nbNodes);
		System.out.println("* Ways:\t\t"+nbWays);
		System.out.println("* Relations:\t"+nbRels);
	}
}
