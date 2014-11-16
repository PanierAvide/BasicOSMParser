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
import info.pavie.basicosmparser.TestSuite;
import info.pavie.basicosmparser.model.Element;

import java.io.File;
import java.io.IOException;
import java.util.Map;

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
		data = parser.parse(new File("res/xml/sample.osm"));
	}

//TESTS
	@Test
	public void testExport() throws IOException {
		File output = new File("res/");
		String result, expected;
		exporter.export(data, output);
		
		//Test nodes.csv
		result = TestSuite.readTextFile(new File(output.getPath()+File.separator+"nodes.csv"));
		expected =
"ID;UserID;timestamp;isVisible;version;changesetID;latitude;longitude;tags\n"+
"N298884269;46882;2008-09-21T21:37:45Z;true;1;676636;54.0901746;12.2482632;\"[]\"\n"+
"N298884272;46882;2008-09-21T21:37:45Z;true;1;676636;54.0901447;12.2516513;\"[]\"\n"+
"N261728686;36744;2008-05-03T13:39:23Z;true;1;323878;54.0906309;12.2441924;\"[]\"\n"+
"N1831881213;75625;2012-07-20T09:43:19Z;true;1;12370172;54.0900666;12.2539381;\"[traffic_sign=city_limit,name=Neu Broderstorf]\"\n"
;
		assertEquals(expected, result);
		
		//Test ways.csv
		result = TestSuite.readTextFile(new File(output.getPath()+File.separator+"ways.csv"));
		expected =
"ID;UserID;timestamp;isVisible;version;changesetID;nodes;tags\n"+
"W26659127;55988;2010-03-16T11:47:08Z;true;5;4142606;\"[N298884269,N298884272,N261728686]\";\"[highway=unclassified,name=Pastower Straße]\"\n"
;
		assertEquals(expected, result);
		
		//Test relations.csv
		result = TestSuite.readTextFile(new File(output.getPath()+File.separator+"relations.csv"));
		expected =
"ID;UserID;timestamp;isVisible;version;changesetID;members;tags\n"+
"R56688;56190;2011-01-12T14:23:49Z;true;28;6947637;\"[N298884269=stop,N261728686=null,W26659127=path,N298884272=null]\";\"[ref=123,route=bus,name=Küstenbus Linie 123,type=route,operator=Regionalverkehr Küste,network=VVW]\"\n"
;
		assertEquals(expected, result);
	}
}
