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

import java.util.HashMap;
import java.util.Map;

/**
 * An element is a generic OSM object.
 * @author Adrien PAVIE
 */
public abstract class Element {
//ATTRIBUTES
	/** The object ID, unique per object type **/
	protected long id;
	/** The last editor name **/
	protected String user;
	/** The last editor ID **/
	protected long uid;
	/** The last edition time **/
	protected String timestamp;
	/** Is this object visible or deleted ? **/
	protected boolean visible;
	/** The version of the object (default: 1) **/
	protected int version;
	/** The last changeset ID **/
	protected long changeset;
	/** The objects tags, which describe it **/
	protected Map<String,String> tags;

//CONSTRUCTOR
	/**
	 * Default constructor, initializes ID, version, visibility and tags. 
	 * @param id The element ID
	 */
	public Element(long id) {
		this.id = id;
		version = 1;
		visible = true;
		tags = new HashMap<String,String>();
	}

//ACCESSORS
	/**
	 * @return the ID, with format : X000000, where X is the object type (N: node, W: way, R: relation)
	 */
	public abstract String getId();

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the user ID
	 */
	public long getUid() {
		return uid;
	}

	/**
	 * @return the last edit timestamp
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * @return Is the object visible in data ?
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @return the last edit changeset ID
	 */
	public long getChangeset() {
		return changeset;
	}

	/**
	 * @return the tags
	 */
	public Map<String, String> getTags() {
		return tags;
	}
	
	@Override
	public String toString() {
		return "Element "+getId()+" ("+getTags()+")";
	}

//MODIFIERS
	/**
	 * @param user The last user who edited the object
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @param uid the last user ID
	 */
	public void setUid(long uid) {
		this.uid = uid;
	}

	/**
	 * @param timestamp the last edit timestamp
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @param visible The new visibility
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @param changeset The last changeset ID
	 */
	public void setChangeset(long changeset) {
		this.changeset = changeset;
	}

	/**
	 * Add a tag
	 * @param key The tag key
	 * @param value The tag value
	 */
	public void addTag(String key, String value) {
		tags.put(key, value);
	}
	
	/**
	 * Remove the given tag
	 * @param key The tag key
	 */
	public void deleteTag(String key) {
		tags.remove(key);
	}
}
