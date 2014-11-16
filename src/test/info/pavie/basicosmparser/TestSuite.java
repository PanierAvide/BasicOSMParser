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

import info.pavie.basicosmparser.controller.TestCSVExporter;
import info.pavie.basicosmparser.controller.TestOSMParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		TestOSMParser.class,
		TestCSVExporter.class
		})

public class TestSuite {
//OTHER METHODS
	/**
	 * Reads a text file, and returns it as a string
	 * @param f The text file to read
	 * @return The read text
	 * @throws FileNotFoundException If file doesn't exist
	 */
	public static String readTextFile(File f) throws FileNotFoundException {
		Scanner s = new Scanner(f);
		StringBuilder result = new StringBuilder();
		while(s.hasNextLine()) {
			result.append(s.nextLine()+"\n");
		}
		s.close();
		return result.toString();
	}
}