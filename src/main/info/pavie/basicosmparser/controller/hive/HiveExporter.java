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

package info.pavie.basicosmparser.controller.hive;

import info.pavie.basicosmparser.controller.OSMParser;
import info.pavie.basicosmparser.model.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import org.xml.sax.SAXException;
 
/**
 * Abstract class, containing common functions to all Hive exporters.
 * See each heriting class for more details.
 * @author Adrien PAVIE
 */
public abstract class HiveExporter extends GenericUDTF {
//ATTRIBUTES
	/** Hive String Handler **/
	protected StringObjectInspector stringOI;
	
	/** OSM XML file content **/
	protected StringBuilder osmContent;
	
	/** Parsed OSM elements **/
	protected Map<String,Element> elements;
	
//OTHER METHODS
	/**
	 * Checks if hive function call is valid, and defines stringOI attribute.
	 * @param argOIs The hive function arguments
	 * @throws UDFArgumentException If function call is invalid
	 */
	protected void checkParameterOI(ObjectInspector[] argOIs) throws UDFArgumentException {
		if(argOIs.length != 1) {
			throw new UDFArgumentException("HiveExporter UDTF takes 1 argument: STRING");
		}
		
		ObjectInspector arg1 = argOIs[0]; //First parameter, corresponding to OSM XML file path
		
		if(!(arg1 instanceof StringObjectInspector)) {
			throw new UDFArgumentException("HiveExporter UDTF takes 1 argument: STRING");
		}
		
		this.stringOI = (StringObjectInspector) arg1;
		this.osmContent = new StringBuilder();
	}
	
	/**
	 * @return The common field names (ID, UserID, Timestamp, ...).
	 */
	protected ArrayList<String> getCommonFieldNames() {
		ArrayList<String> fieldNames = new ArrayList<String>();
		fieldNames.add("ID");
		fieldNames.add("UserID");
		fieldNames.add("Timestamp");
		fieldNames.add("IsVisible");
		fieldNames.add("Version");
		fieldNames.add("ChangesetID");
		fieldNames.add("Tags");
		
		return fieldNames;
	}
	
	/**
	 * @return The common field object inspectors (for ID, UserID, Timestamp, ...)
	 */
	protected ArrayList<ObjectInspector> getCommonFieldOIs() {
		ArrayList<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaLongObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaBooleanObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaIntObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaLongObjectInspector);
		fieldOIs.add(ObjectInspectorFactory.getStandardMapObjectInspector(
				PrimitiveObjectInspectorFactory.javaStringObjectInspector,
				PrimitiveObjectInspectorFactory.javaStringObjectInspector));
		
		return fieldOIs;
	}
	
	@Override
	public void process(Object[] args) throws HiveException {
		//Get XML file content
		osmContent.append((String) stringOI.getPrimitiveJavaObject(args[0]));
	}
	
	/**
	 * Once osmFile attribute is defined, you can call this function to fill elements attribute with read data.
	 * @throws HiveException If an error occurs during parsing.
	 */
	protected void parseData() throws HiveException {
		if(osmContent == null) {
			throw new HiveException("Undefined OSM XML file");
		}
		
		OSMParser parser = new OSMParser();

		try {
			elements = parser.parse(osmContent.toString());
		} catch (IOException | SAXException e) {
			throw new HiveException(e);
		}
	}
	
	/**
	 * This methods fills a row array with common data extracted from the given element.
	 * @param row The row array (size should be > 7)
	 * @param elem The element to use
	 */
	protected void fillRow(Object[] row, Element elem) {
		row[0] = elem.getId();
		row[1] = elem.getUid();
		row[2] = elem.getTimestamp();
		row[3] = elem.isVisible();
		row[4] = elem.getVersion();
		row[5] = elem.getChangeset();
		row[6] = elem.getTags();
	}
}