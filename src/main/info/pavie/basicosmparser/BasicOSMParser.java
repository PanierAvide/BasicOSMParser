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

package info.pavie.basicosmparser;

import info.pavie.basicosmparser.controller.CSVExporter;
import info.pavie.basicosmparser.controller.OSMParser;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * This class is a simple command line interface.
 * It parses OSM XML and create a CSV export.
 * You have to use it like this : basicosmparser <Input OSM XML> <Output folder>
 * @author Adrien PAVIE
 */
public class BasicOSMParser {
//OTHER METHODS
	public static void main(String[] args) {
		//Check arguments
		if(args.length != 2) {
			System.out.println("Invalid parameters.\nCommand usage: basicosmparser <Input OSM XML> <Output folder>");
		}
		else {
			File input = new File(args[0]);
			File output = new File(args[1]);
			
			//Check input
			if(!input.exists() || !input.isFile() || !input.canRead()) {
				System.out.println("Can't read input OSM XML.\nCheck if file exists and is readable.");
			}
			else {
				//Check output
				if(!output.exists() || !output.isDirectory()) {
					System.out.println("Invalid destination folder.");
				}
				else {
					//Convert data
					OSMParser parser = new OSMParser();
					CSVExporter exporter = new CSVExporter();
					
					try {
						exporter.export(parser.parse(input), output);
						System.out.println("Exported data to "+output.getPath()+" without errors.");
					} catch (IOException | SAXException e) {
						System.err.println("Error during data export.");
						e.printStackTrace();
					}
				}
			}
		}
	}
}
