package info.pavie.basicosmparser.controller.hive;

import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Relation;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

/**
 * This class reads an OSM XML file, and creates rows for Hive (Relations only).
 * To use it, you need to have a JAR of this application, and in Hive :
 * ADD JAR /path/to/basicosmparser.jar;
 * CREATE TEMPORARY FUNCTION OSMImportRelations AS 'info.pavie.basicosmparser.controller.hive.HiveRelationExporter';
 * CREATE TABLE osmdata(osm_content STRING) STORED AS TEXTFILE;
 * LOAD DATA LOCAL INPATH '/path/to/data.osm' OVERWRITE INTO TABLE osmdata;
 * SELECT OSMImportRelations(osm_content) FROM osmdata;
 * @author Adrien PAVIE
 */
public class HiveRelationExporter extends HiveExporter {
//OTHER METHODS
	@Override
	public StructObjectInspector initialize(ObjectInspector[] argOIs) throws UDFArgumentException {
		//Check hive function call
		checkParameterOI(argOIs);
		
		//Expected output columns
		ArrayList<String> fieldNames = getCommonFieldNames();
		fieldNames.add("Members");
		
		//Expected output types
		ArrayList<ObjectInspector> fieldOIs = getCommonFieldOIs();
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		
		return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
	}
	
	@Override
	public void close() throws HiveException {
		parseData();
		
		if(elements == null) {
			throw new HiveException("No read data or error occured during parsing");
		}
		
		
		//Read relations, an create row for each one.
		for(String id : elements.keySet()) {
			Element current = elements.get(id);
			
			if(current instanceof Relation) {
				//General information
				Object[] currentRow = new Object[8];
				fillRow(currentRow, current);
				
				//Create members list
				StringBuilder memberList = new StringBuilder("[");
				boolean firstElem = true;
				
				for(Element e : ((Relation) current).getMembers()) {
					if(!firstElem) {
						memberList.append(",");
					} else {
						firstElem = false;
					}
					
					//Member
					memberList.append(e.getId()+"=");
					
					//Role
					String role = ((Relation) current).getMemberRole(e);
					if(role.equals("")) { role = "null"; }
					memberList.append(role);
				}
				memberList.append("]");
				
				currentRow[7] = memberList.toString();
				
				//Write into standard output
				forward(currentRow);
			}
		}
	}
}
