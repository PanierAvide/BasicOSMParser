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
 * This class allows you to export a Map of {@link Element}s as a CSV file.
 * Fields are separated with a ';'
 * @author Adrien PAVIE
 */
public class CSVExporter {
//OTHER METHODS
	/**
	 * Exports a map of Elements as a CSV file.
	 * The CSV contains the following fields : object ID, last user ID, last edit timestamp,
	 * 		is the object visible, the object version, the last changeset ID, and a column for each tag.
	 * @param elements The element objects to export
	 * @param csvOutput The file to write result in
	 * @throws IOException If an error occurs during CSV writing
	 */
	public void export(Map<String,Element> elements, File csvOutput) throws IOException {
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
		StringBuilder csvBuild = new StringBuilder();
		csvBuild.append("ID;UserID;timestamp;isVisible;version;changesetID"); //Default columns

		//Create columns for each used tag
		String[] usedTagsOrdered = usedTags.toArray(new String[usedTags.size()]);
		for(String tag : usedTagsOrdered) {
			csvBuild.append(";\""+tag+"\"");
		}
		
		//Create CSV entries for each element
		for(String id : elements.keySet()) {
			currentElem = elements.get(id);
			csvBuild.append('\n'+id+';'
					+currentElem.getUid()+';'
					+currentElem.getTimestamp()+';'
					+currentElem.isVisible()+';'
					+currentElem.getVersion()+';'
					+currentElem.getChangeset());
			
			for(String tag : usedTagsOrdered) {
				csvBuild.append(";\""+globalTags.get(id+"-"+tag)+"\""); //The value for given key, or null if undefined
			}
		}
		
		//Write CSV
		Writer w = new OutputStreamWriter(new FileOutputStream(csvOutput));
		w.write(csvBuild.toString());
		w.close();
	}
}
