package org.jasig.portlet.proxy.service;

/**
 * IFormField is an interface used to store form field information within an IContentRequest object.
 * 
 * @author mgillian
 *
 */
public interface IFormField {
	
	/**
	 * setName() sets the name of the field
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * getName() gets the name of the field
	 * @return
	 */
	public String getName();
	
	/**
	 * setValue() sets the first value of the field
	 * @param value
	 */
	public void setValue(String value);
	
	/**
	 * setValues() sets the value of the field
	 * @param value
	 */
	public void setValues(String[] values);
	
	/**
	 * getValue() returns the first value associated with the Field
	 * @return
	 */
	public String getValue();
	
	/**
	 * getValue() gets the value of the field
	 * @return
	 */
	public String[] getValues();
	
	/**
	 * isSecured() returns whether the field is encrypted and should be displayed obscured
	 * to the user
	 * @return true if field should be encrypted and secured, false otherwise
	 */
	public boolean isSecured();
	
	/**
	 * isSecured(boolean) changes whether the field should be encrypted and obscured
	 * @param isSecured
	 */
	public void isSecured(boolean isSecured);
	
	/**
	 * duplicate() copies the data from the current IFormField into the passed-in parameter
	 * @param copy a blank IFormField object that will receive a copy of the incoming data
	 */
	public void duplicate(IFormField copy);
}