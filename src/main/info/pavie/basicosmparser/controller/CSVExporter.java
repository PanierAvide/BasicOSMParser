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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class allows you to export a Map of {@link Element}s as several CSV files.
 * Four CSV files are created : elements.csv, nodes.csv, ways.csv, relations.csv
 * Elements.csv contains generic informations about all elements (ID, last user ID, timestamp, ..., and tags).
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
		//Create the global tags map, which will contain all tags values for each element
		Map<String,String> globalTags = new HashMap<String,String>(elements.size()*2);
		
		//Create the set of all used tags
		Set<String> usedTags = new HashSet<String>();
		
		//Fill these with current data
		Element currentElem = null;
		for(String id : elements.keySet()) {
			currentElem = elements.get(id);
			usedTags.addAll(currentElem.getTags().keySet()); //Add all keys to usedTags
			
			//Create entries in globalTags
			for(String key : currentElem.getTags().keySet()) {
				globalTags.put(currentElem.getId()+"-"+key, currentElem.getTags().get(key));
			}
		}
		
		//Create output
		StringBuilder csvElementsBuild = new StringBuilder("ID;UserID;timestamp;isVisible;version;changesetID");
		StringBuilder csvNodesBuild = new StringBuilder("ID;latitude;longitude");
		StringBuilder csvWaysBuild = new StringBuilder("ID;nodes");
		StringBuilder csvRelsBuild = new StringBuilder("ID;members;roles");

		//Create columns for each used tag
		String[] usedTagsOrdered = usedTags.toArray(new String[usedTags.size()]);
		for(String tag : usedTagsOrdered) {
			csvElementsBuild.append(";\""+tag+"\"");
		}
		
		//Create CSV entries for each element
		for(String id : elements.keySet()) {
			currentElem = elements.get(id);
			
			/*
			 * Elements.csv content
			 */
			csvElementsBuild.append('\n'+id+';'
					+currentElem.getUid()+';'
					+currentElem.getTimestamp()+';'
					+currentElem.isVisible()+';'
					+currentElem.getVersion()+';'
					+currentElem.getChangeset());
			
			String currentValue;
			for(String tag : usedTagsOrdered) {
				currentValue = globalTags.get(id+"-"+tag);
				String toAppend = (currentValue == null) ? ";null" : ";\""+currentValue+"\"";
				csvElementsBuild.append(toAppend); //The value for given key, or null if undefined
			}
			
			/*
			 * Other csv content (depends of object type)
			 */
			if(currentElem instanceof Node) {
				//Node element (define latitude, longitude)
				Node currentNode = (Node) currentElem;
				csvNodesBuild.append('\n'+id+';'+currentNode.getLat()+';'+currentNode.getLon());
			}
			else if(currentElem instanceof Way) {
				//Way element (list nodes)
				Way currentWay = (Way) currentElem;
				csvWaysBuild.append('\n'+id+";\"["+
				currentWay
						.getNodes()
						.get(0)
						.getId());
				
				for(int i=1; i < currentWay.getNodes().size(); i++) {
					csvWaysBuild.append(","+currentWay.getNodes().get(i).getId());
				}
				
				csvWaysBuild.append("]\"");
			}
			else if(currentElem instanceof Relation) {
				//Relation element (list members and roles)
				Relation currentRel = (Relation) currentElem;
				csvRelsBuild.append('\n'+id+";\"["+currentRel.getMembers().get(0).getId());
				
				//List members
				for(int i=1; i < currentRel.getMembers().size(); i++) {
					csvRelsBuild.append(","+currentRel.getMembers().get(i).getId());
				}
				csvRelsBuild.append("]\";\"["+currentRel.getMemberRole(currentRel.getMembers().get(0)));
				
				//List roles
				for(int i=1; i < currentRel.getMembers().size(); i++) {
					csvRelsBuild.append(","+currentRel.getMemberRole(currentRel.getMembers().get(i)));
				}
				csvRelsBuild.append("]\"");
			}
			else {
				throw new RuntimeException("Unexpected kind of Element: "+currentElem.getClass().toString());
			}
		}
		
		//Write CSV
		File csvElements = new File(outputFolder.getPath()+File.separator+"elements.csv");
		File csvNodes = new File(outputFolder.getPath()+File.separator+"nodes.csv");
		File csvWays = new File(outputFolder.getPath()+File.separator+"ways.csv");
		File csvRels = new File(outputFolder.getPath()+File.separator+"relations.csv");
		writeTextFile(csvElements, csvElementsBuild.toString());
		writeTextFile(csvNodes, csvNodesBuild.toString());
		writeTextFile(csvWays, csvWaysBuild.toString());
		writeTextFile(csvRels, csvRelsBuild.toString());
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
