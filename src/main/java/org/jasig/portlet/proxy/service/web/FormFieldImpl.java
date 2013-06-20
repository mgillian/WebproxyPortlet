package org.jasig.portlet.proxy.service.web;

import org.jasig.portlet.proxy.service.IFormField;

public class FormFieldImpl implements IFormField {
	
	private String name;
	private String[] values;
	private boolean secured;
	
	public FormFieldImpl() {
		name = "blankField";
		values = new String[1];
		secured = false;
	}
	
	public FormFieldImpl(String name, String[] values) {
		this(name, values, false);
	}
	
	public FormFieldImpl(String name, String[] values, boolean secured) {
		this.name = name;
		this.values = values;
		this.secured = secured;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setValue(String value) {
		this.values[0] = value;
	}
	
	@Override
	public void setValues(String[] values) {
		this.values = values;
	}

	@Override
	public String getValue() {
		return this.values[0];
	}
	
	@Override
	public String[] getValues() {
		return values;
	}

	@Override
	public boolean isSecured() {
		return this.secured;
	}

	@Override
	public void isSecured(boolean isSecured) {
		this.secured = isSecured;
	}

	@Override
	public void duplicate(IFormField copy) {
		copy.setName(this.name);
		String[] values = this.getValues();
		String[] copiedValues = new String[values.length];
		System.arraycopy(values, 0, copiedValues, 0, values.length);
		copy.setValues(copiedValues);
	}
}