BasicOSMParser
==============

Read-me
-------

BasicOSMParser is a collection of Java classes allowing to parse raw OSM XML files.
Then you are able to manipulate those Java objects in your program. You can also export
data as CSV files. This library is very simple to understand. It uses the default Java
SAX parser (org.xml.sax). The application tests use JUnit 4 framework.

Installation (for developers)
------------------------------

In order to use BasicOSMParser, you can download the BasicOSMParser.jar file. Alternatively,
you can put the content of src/main/ folder in the source directory of your project.
Then, add this code in your classes to import the parser :

```
import info.pavie.basicosmparser.controller.*;
import info.pavie.basicosmparser.model.*;
```

Usage
-----

### In another project

Here is a simple example of how to use the parser. You just need to create a new parser
object, and then call the <code>parse</code> method.

```
OSMParser p = new OSMParser();						//Initialization of the parser
File osmFile = new File("/path/to/your/data.osm");	//Create a file object for your OSM XML file

try {

	Map<String,Element> result = p.parse(osmFile);		//Parse OSM data, and put result in a Map object

} catch (IOException | SAXException e) {
	e.printStackTrace();								//Input/output errors management
}
```

The parser returns a <code>Map<String,Element></code> object. This is a collection of key/value pairs.
In this collection, keys are read OSM object identifiers, and values are read OSM objects.
Keys are in a specific format : a letter followed by several digits. The letter corresponds to the kind
of OSM object ('N' for nodes, 'W' for ways, 'R' for relations), and following digits are the OSM numeric ID.
The read OSM objects are represented by Element objects, which can be Node, Way or Relation depending of the
OSM object. You can access the different attributes of objects : ID, user ID, timestamp, version, object tags, ...

You can also directly pass a string which contains XML content, or use an InputSource object.
See the Javadoc of these classes for more details about the available methods.

If you want to get parsed data as several CSV files, use a <code>CSVExporter</code> object :

```
Map<String,Element> result = p.parse(osmFile);
CSVExporter exporter = new CSVExporter();
exporter.export(result, new File("/output/path/for/csv/"));	//Throws IOException if error occurs during writing
```

See the Javadoc of CSVExporter to know more about output CSV format.

### As a data consumer

If you only want to use this parser to create CSV files, you can execute the JAR with the following command :

```
java -jar BasicOSMParser.jar /path/to/data.osm /path/to/output/folder/
```

The command will parse the given OSM XML file, and create the CSV files in the output folder.

License
-------

Copyright 2014 Adrien PAVIE

See LICENSE for complete GPL3 license.

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