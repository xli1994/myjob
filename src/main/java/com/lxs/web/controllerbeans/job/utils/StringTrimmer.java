package com.lxs.web.controllerbeans.job.utils;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * A util class to trim input field string, this will trim all input field when submit
 * it's very useful class
 * It auto registers
 * @author lxs
 *
 */
@FacesConverter(forClass = String.class)
public class StringTrimmer implements Converter
{

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value)
	{
		return value != null ? value.trim() : null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value)
	{
		return (String) value;
	}

}