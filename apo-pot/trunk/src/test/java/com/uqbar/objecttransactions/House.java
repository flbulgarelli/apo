package com.uqbar.objecttransactions;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.uqbar.commons.utils.Transactional;

/**
 * @author jfernandes
 */
@Transactional
public class House {
	private Boolean frontDoorClosed = true;
	private Boolean backDoorClosed = true;
	private Person[] persons = new Person[] {};
	
	public void closeFrontDoor() {
		this.frontDoorClosed = true;
	}
	
	public void openFrontDoor() {
		this.frontDoorClosed = false;
	}
	
	public void closeBackDoor() {
		this.backDoorClosed = true;
	}
	
	public void openBackDoor() {
		this.backDoorClosed = false;
	}
	
	public boolean isBackDoorClosed() {
		return backDoorClosed;
	}
	
	public boolean isFrontDoorClosed() {
		return frontDoorClosed;
	}
	
	public List<Person> getPeople() {
		return Arrays.asList(this.persons);
	}
	
	public void addPerson(Person person) {
		this.persons = (Person[]) ArrayUtils.add(this.persons, person);
	}

	public void setBackDoorClosed(Boolean backDoorClosed) {
		this.backDoorClosed = backDoorClosed;
	}


	public void setFrontDoorClosed(Boolean frontDoorClosed) {
		this.frontDoorClosed = frontDoorClosed;
	}

	public Person[] getPersons() {
		return persons;
	}

	public void setPersons(Person[] persons) {
		this.persons = persons;
	}

}

