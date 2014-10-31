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

package info.pavie.basicosmparser.model;

/**
 * A node is an OSM element with coordinates.
 * @author Adrien PAVIE
 */
public class Node extends Element {
//ATTRIBUTES
	/** The latitude **/
	private double lat;
	/** The longitude **/
	private double lon;
	
//CONSTRUCTOR
	/**
	 * Default constructor
	 * @param id The object ID
	 * @param lat The latitude
	 * @param lon The longitude
	 */
	public Node(long id, double lat, double lon) {
		super(id);
		this.lat = lat;
		this.lon = lon;
	}

//ACCESSORS
	@Override
	public String getId() {
		return "N"+id;
	}

	/**
	 * @return the latitude
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @return the longitude
	 */
	public double getLon() {
		return lon;
	}

//MODIFIERS
	/**
	 * @param lat the new latitude
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * @param lon the new longitude
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}
}
