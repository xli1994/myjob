package com.lxs.web.controllerbeans.job.utils;

import java.util.HashMap;
import java.util.Map;

import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is to illustrate how to get message from properties, it is singleton,
 * but here just show how to get
 * 
 * @author lxs
 *
 */
//@Named("messageProvider")
public class MessageProvider
{
	private static final Logger logger = LogManager.getLogger(MessageProvider.class);

	//a map to hold all bundles
	private static Map<String, ResourceBundle> map = new HashMap<String, ResourceBundle>();

	private MessageProvider()
	{
	}

	//bundleName is <var>validatorMsg</var> name defined in faces-config.xml
	public static ResourceBundle getBundle(String bundleName)
	{

		if (map.get(bundleName) == null)
		{
			FacesContext context = FacesContext.getCurrentInstance();
			Application app = context.getApplication();
			ResourceBundle bundle = app.getResourceBundle(context, bundleName);
			map.put(bundleName, bundle);
		}

		return map.get(bundleName);
	}

	public static String getValue(String msgKey, String bundleName)
	{
		return getBundle(bundleName).getString(msgKey);
	}
}
