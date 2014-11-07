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

import static org.junit.Assert.fail;
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
		data = parser.parse(new File("test/xml/sample.osm"));
	}

//TESTS
	@Test
	public void testExport() throws IOException {
		exporter.export(data, new File("test/out.csv"));
		fail("Not yet implemented");
	}
}
