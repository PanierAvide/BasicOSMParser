BasicOSMParser
==============

Read-me
-------

BasicOSMParser is a collection of Java classes allowing to parse raw OSM XML files.
Then you are able to manipulate those Java objects in your program. This library is
very simple to understand. It uses the default Java SAX parser (org.xml.sax).
The application tests use JUnit 4 framework.

Installation
------------

In order to use BasicOSMParser, you have to put the content of src/main/ folder in the
source directory of your project. Then, add this code in your classes to import the parser :

```
import info.pavie.basicosmparser.controller.*;
import info.pavie.basicosmparser.model.*;
```

Usage
-----

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
See the Javadoc of these classes for more details about the available methods.

License
-------

Copyright 2014 Adrien PAVIE

See COPYING for complete GPL3 license.

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