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

import static org.junit.Assert.assertEquals;
import info.pavie.basicosmparser.model.Element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link CSVExporter}.
 * @author Adrien PAVIE
 */
public class TestCSVExporter {
//ATTRIBUTES
	private CSVExporter exporter;
	private OSMParser parser;
	private Map<String,Element> data;
	
//SETUP
	@Before
	public void setUp() throws Exception {
		exporter = new CSVExporter();
		parser = new OSMParser();
		data = parser.parse(new File("test/xml/sample.osm"));
	}

//TESTS
	@Test
	public void testExport() throws IOException {
		File output = new File("test/");
		exporter.export(data, output);
		
		//Test elements.csv
		String result = readTextFile(new File(output.getPath()+File.separator+"elements.csv"));
		String expected =
"ID;UserID;timestamp;isVisible;version;changesetID;\"traffic_sign\";\"ref\";\"highway\";\"route\";\"name\";\"type\";\"network\";\"operator\"\n"+
"N298884269;46882;2008-09-21T21:37:45Z;true;1;676636;null;null;null;null;null;null;null;null\n"+
"N298884272;46882;2008-09-21T21:37:45Z;true;1;676636;null;null;null;null;null;null;null;null\n"+
"N261728686;36744;2008-05-03T13:39:23Z;true;1;323878;null;null;null;null;null;null;null;null\n"+
"N1831881213;75625;2012-07-20T09:43:19Z;true;1;12370172;\"city_limit\";null;null;null;\"Neu Broderstorf\";null;null;null\n"+
"W26659127;55988;2010-03-16T11:47:08Z;true;5;4142606;null;null;\"unclassified\";null;\"Pastower Straße\";null;null;null\n"+
"R56688;56190;2011-01-12T14:23:49Z;true;28;6947637;null;\"123\";null;\"bus\";\"Küstenbus Linie 123\";\"route\";\"VVW\";\"Regionalverkehr Küste\"\n"
;
		assertEquals(expected, result);
		
		//Test nodes.csv
		result = readTextFile(new File(output.getPath()+File.separator+"nodes.csv"));
		expected =
"ID;latitude;longitude\n"+
"N298884269;54.0901746;12.2482632\n"+
"N298884272;54.0901447;12.2516513\n"+
"N261728686;54.0906309;12.2441924\n"+
"N1831881213;54.0900666;12.2539381\n"
;
		assertEquals(expected, result);
		
		//Test ways.csv
		result = readTextFile(new File(output.getPath()+File.separator+"ways.csv"));
		expected =
"ID;nodes\n"+
"W26659127;\"[N298884269,N298884272,N261728686]\"\n"
;
		assertEquals(expected, result);
		
		//Test relations.csv
		result = readTextFile(new File(output.getPath()+File.separator+"relations.csv"));
		expected =
"ID;members;roles\n"+
"R56688;\"[N298884269,N261728686,W26659127,N298884272]\";\"[stop,,path,]\"\n"
;
		assertEquals(expected, result);
	}

//OTHER METHODS
	/**
	 * Reads a text file, and returns it as a string
	 * @param f The text file to read
	 * @return The read text
	 * @throws FileNotFoundException If file doesn't exist
	 */
	private String readTextFile(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		StringBuilder result = new StringBuilder();
		while(s.hasNextLine()) {
			result.append(s.nextLine()+"\n");
		}
		s.close();
		return result.toString();
	}
}
