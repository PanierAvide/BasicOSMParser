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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.xml.sax.SAXException;
 
/**
 * This class reads an OSM XML file, and creates rows for Hive (Nodes only).
 * To use it, you need to have a JAR of this application, and in Hive :
 * ADD JAR /path/to/basicosmparser.jar;
 * CREATE TEMPORARY FUNCTION OSMImportNodes AS 'info.pavie.basicosmparser.controller.HiveExporter';
 * SELECT OSMImportNodes("/path/to/data.osm");
 * @author Adrien PAVIE
 */
public class HiveExporter extends GenericUDTF {
//ATTRIBUTES
	/** Hive String Handler **/
	StringObjectInspector stringOI;
	
	/** OSM XML file path **/
	String osmFile;
	
//OTHER METHODS
	@Override
	public StructObjectInspector initialize(ObjectInspector[] argOIs) throws UDFArgumentException {
		if(argOIs.length != 1) {
			throw new UDFArgumentException("HiveExporter UDTF takes 1 argument: STRING");
		}
		
		ObjectInspector arg1 = argOIs[0]; //First parameter, corresponding to OSM XML file path
		
		if(!(arg1 instanceof StringObjectInspector)) {
			throw new UDFArgumentException("HiveExporter UDTF takes 1 argument: STRING");
		}
		
		this.stringOI = (StringObjectInspector) arg1;
		
		//Expected output columns
		ArrayList<String> fieldNames = new ArrayList<String>();
		ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
		fieldNames.add("ID");
		fieldNames.add("UserID");
		fieldNames.add("Timestamp");
		fieldNames.add("IsVisible");
		fieldNames.add("Version");
		fieldNames.add("ChangesetID");
		fieldNames.add("Tags");
		fieldNames.add("Latitude");
		fieldNames.add("Longitude");
		
		//Expected output types
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaLongObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaBooleanObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaLongObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);
		
		return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
	}

	@Override
	public void process(Object[] args) throws HiveException {
		//Get XML file path
		osmFile = (String) stringOI.getPrimitiveJavaObject(args[0]);
	}
	
	@Override
	public void close() throws HiveException {
		if(osmFile == null) {
			throw new HiveException("Undefined OSM XML file");
		}
		
		OSMParser parser = new OSMParser();
		try {
			Map<String,Element> elements = parser.parse(new File(osmFile));
			
			//Read nodes, an create row for each one.
			for(String id : elements.keySet()) {
				Element current = elements.get(id);
				
				if(current instanceof Node) {
					//General information
					Object[] currentRow = new Object[9];
					currentRow[0] = id;
					currentRow[1] = current.getUid();
					currentRow[2] = current.getTimestamp();
					currentRow[3] = current.isVisible();
					currentRow[4] = current.getVersion();
					currentRow[5] = current.getChangeset();
					currentRow[7] = ((Node) current).getLat();
					currentRow[8] = ((Node) current).getLon();
					
					//Save tags as an array
					StringBuilder tags = new StringBuilder("[");
					boolean firstTag = true;
					for(String key : current.getTags().keySet()) {
						if(!firstTag) {
							tags.append(",");
						} else {
							firstTag = false;
						}
						
						tags.append(key+"="+current.getTags().get(key));
					}
					tags.append("]");
					currentRow[6] = tags.toString();
					
					//Write into standard output
					forward(currentRow);
				}
			}
		} catch (IOException | SAXException e) {
			throw new HiveException(e);
		}
	}
}