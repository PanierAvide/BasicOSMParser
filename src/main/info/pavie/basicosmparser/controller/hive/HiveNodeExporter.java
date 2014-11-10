package info.pavie.basicosmparser.controller.hive;

import info.pavie.basicosmparser.model.Element;
import info.pavie.basicosmparser.model.Node;

import java.util.ArrayList;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

/**
 * This class reads an OSM XML file, and creates rows for Hive (Nodes only).
 * To use it, you need to have a JAR of this application, and in Hive :
 * ADD JAR /path/to/basicosmparser.jar;
 * CREATE TEMPORARY FUNCTION OSMImportNodes AS 'info.pavie.basicosmparser.controller.hive.HiveNodeExporter';
 * CREATE TABLE osmdata(osm_content STRING) STORED AS TEXTFILE;
 * LOAD DATA LOCAL INPATH '/path/to/data.osm' OVERWRITE INTO TABLE osmdata;
 * SELECT OSMImportNodes(osm_content) FROM osmdata;
 * @author Adrien PAVIE
 */
public class HiveNodeExporter extends HiveExporter {
//OTHER METHODS
	@Override
	public StructObjectInspector initialize(ObjectInspector[] argOIs) throws UDFArgumentException {
		//Check hive function call
		checkParameterOI(argOIs);
		
		//Expected output columns
		ArrayList<String> fieldNames = getCommonFieldNames();
		fieldNames.add("Latitude");
		fieldNames.add("Longitude");
		
		//Expected output types
		ArrayList<ObjectInspector> fieldOIs = getCommonFieldOIs();
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaDoubleObjectInspector);
		
		return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
	}
	
	@Override
	public void close() throws HiveException {
		parseData();
		
		if(elements == null) {
			throw new HiveException("No read data or error occured during parsing");
		}
		
		
		//Read nodes, an create row for each one.
		for(String id : elements.keySet()) {
			Element current = elements.get(id);
			
			if(current instanceof Node) {
				//General information
				Object[] currentRow = new Object[9];
				fillRow(currentRow, current);
				currentRow[7] = ((Node) current).getLat();
				currentRow[8] = ((Node) current).getLon();
				
				//Write into standard output
				forward(currentRow);
			}
		}
	}
}
