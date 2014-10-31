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

import java.util.ArrayList;
import java.util.List;

/**
 * A relation is a meta OSM object. It allows to combine other elements.
 * @author Adrien PAVIE
 */
public class Relation extends Element {
//ATTRIBUTES
	/** The relation members **/
	private List<Member> members;

//CONSTRUCTOR
	public Relation(long id) {
		super(id);
		members = new ArrayList<Member>();
	}

//ACCESSORS
	@Override
	public String getId() {
		return "R"+id;
	}
	
	/**
	 * @param e The element
	 * @return The role of this element, or null if no role
	 */
	public String getMemberRole(Element e) {
		String result = null;
		
		boolean found = false;
		int index = 0;
		while(!found && index < members.size()) {
			if(members.get(index).elem == e) {
				found = true;
				result = members.get(index).role;
			}
			index++;
		}
		
		if(!found) {
			throw new RuntimeException("Element "+e.getId()+" not found");
		}
		
		return result;
	}
	
	/**
	 * @return The list of member elements
	 */
	public List<Element> getMembers() {
		List<Element> elems = new ArrayList<Element>(members.size());
		for(Member m : members) {
			elems.add(m.elem);
		}
		return elems;
	}

//MODIFIERS
	/**
	 * Adds a new member
	 * @param role The role of the member
	 * @param e The element
	 */
	public void addMember(String role, Element e) {
		if(e == null) {
			throw new NullPointerException("Element can't be null");
		}
		
		members.add(new Member(role, e));
	}
	
	/**
	 * Removes a member
	 * @param e The element to remove
	 */
	public void removeMember(Element e) {
		boolean found = false;
		int index = 0;
		while(!found && index < members.size()) {
			if(members.get(index).elem == e) {
				found = true;
				members.remove(index);
			}
			index++;
		}
	}
	
//INNER CLASS Member
	private class Member {
	//ATTRIBUTES
		/** The member role **/
		private String role;
		/** The member object **/
		private Element elem;
		
	//CONSTRUCTOR
		/**
		 * Default constructor
		 * @param role The member role
		 * @param elem The member object
		 */
		private Member(String role, Element elem) {
			this.role = role;
			this.elem = elem;
		}
	}
}
