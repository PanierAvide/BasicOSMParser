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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

/**
 * This class allows you to export a Map of {@link Element}s as several CSV files.
 * Three CSV files are created :  nodes.csv, ways.csv, relations.csv
 * Those three CSV all contain generic informations about all elements (ID, last user ID, timestamp, ..., and tags).
 * Nodes.csv contains nodes coordinates.
 * Ways.csv contains the nodes list for each way.
 * Relations.csv contains the members list for each relation, and members roles.
 * @author Adrien PAVIE
 */
public class CSVExporter {
//OTHER METHODS
	/**
	 * Exports a map of Elements as CSV files.
	 * @param elements The element objects to export
	 * @param outputFolder The folder where CSV files will be written
	 * @throws IOException If an error occurs during CSV writing
	 */
	public void export(Map<String,Element> elements, File outputFolder) throws IOException {
		Element currentElem = null;
		
		//Create output
		StringBuilder csvNodesBuild = new StringBuilder("ID;UserID;timestamp;isVisible;version;changesetID;latitude;longitude;tags");
		StringBuilder csvWaysBuild = new StringBuilder("ID;UserID;timestamp;isVisible;version;changesetID;nodes;tags");
		StringBuilder csvRelsBuild = new StringBuilder("ID;UserID;timestamp;isVisible;version;changesetID;members;tags");

		//Create CSV entries for each element
		for(String id : elements.keySet()) {
			currentElem = elements.get(id);
			
			/*
			 * CSV content (depends of object type)
			 */
			if(currentElem instanceof Node) {
				//Node element (define latitude, longitude)
				Node currentNode = (Node) currentElem;
				addInformations(csvNodesBuild, currentElem);
				
				csvNodesBuild.append(";"+currentNode.getLat()+";"+currentNode.getLon()+";");
				
				addTags(csvNodesBuild, currentElem);
			}
			else if(currentElem instanceof Way) {
				//Way element (list nodes)
				Way currentWay = (Way) currentElem;
				addInformations(csvWaysBuild, currentElem);
				
				csvWaysBuild.append(";\"["+
				currentWay
						.getNodes()
						.get(0)
						.getId());
				
				for(int i=1; i < currentWay.getNodes().size(); i++) {
					csvWaysBuild.append(","+currentWay.getNodes().get(i).getId());
				}
				
				csvWaysBuild.append("]\";");
				
				addTags(csvWaysBuild, currentElem);
			}
			else if(currentElem instanceof Relation) {
				//Relation element (list members and roles)
				Relation currentRel = (Relation) currentElem;
				addInformations(csvRelsBuild, currentElem);
				
				csvRelsBuild.append(";\"[");
				
				//List members and roles
				for(int i=0; i < currentRel.getMembers().size(); i++) {
					if(i > 0) {
						csvRelsBuild.append(",");
					}
					
					//Member
					csvRelsBuild.append(currentRel.getMembers().get(i).getId()
							+"=");
					
					//Role
					String role = currentRel.getMemberRole(currentRel.getMembers().get(i));
					if(role.equals("")) { role = "null"; }
					csvRelsBuild.append(role);
				}
				csvRelsBuild.append("]\";");
				
				addTags(csvRelsBuild, currentElem);
			}
			else {
				throw new RuntimeException("Unexpected kind of Element: "+currentElem.getClass().toString());
			}
		}
		
		//Write CSV
		File csvNodes = new File(outputFolder.getPath()+File.separator+"nodes.csv");
		File csvWays = new File(outputFolder.getPath()+File.separator+"ways.csv");
		File csvRels = new File(outputFolder.getPath()+File.separator+"relations.csv");
		writeTextFile(csvNodes, csvNodesBuild.toString());
		writeTextFile(csvWays, csvWaysBuild.toString());
		writeTextFile(csvRels, csvRelsBuild.toString());
	}
	
	/**
	 * Adds the tags of an Element in the given StringBuilder
	 * @param sb The string builder
	 * @param elem The element
	 */
	private void addTags(StringBuilder sb, Element elem) {
		//Start tags array
		sb.append("\"[");
		
		boolean firstTag = true;
		
		//Add each tag
		for(String key : elem.getTags().keySet()) {
			if(!firstTag) {
				sb.append(",");
			} else {
				firstTag = false;
			}
			
			sb.append(key+"="+elem.getTags().get(key));
		}
		
		//End array
		sb.append("]\"");
	}
	
	/**
	 * Adds common informations about a Element in a StringBuilder
	 * @param sb The string builder
	 * @param elem The element
	 */
	private void addInformations(StringBuilder sb, Element elem) {
		sb.append('\n'+elem.getId()+';'
				+elem.getUid()+';'
				+elem.getTimestamp()+';'
				+elem.isVisible()+';'
				+elem.getVersion()+';'
				+elem.getChangeset());
	}
	
	/**
	 * Writes a text file.
	 * @param output The file to write in
	 * @param text The text to write
	 * @throws IOException If an error occurs during writing
	 */
	private void writeTextFile(File output, String text) throws IOException {
		Writer w = new OutputStreamWriter(new FileOutputStream(output));
		w.write(text);
		w.close();
	}
}
