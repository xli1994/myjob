package com.lxs.web.controllerbeans.job.utils;

import java.util.ResourceBundle;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is a util class to load configuration data from properties "generalConfig.properties"
 * 
 * @author lxs
 *
 */
public class ConfigUtil
{
	private static final Logger logger = LogManager.getLogger(ConfigUtil.class);
	
	private static final String configBundleName = "config";
	private static ResourceBundle config ;
	
	//property name for upload file location dir
	private static final String UPLOAD_FILE_DIR = "uploadFileDir";
	
	//solr sever 
	private static final String SOLR_SERVER_URL = "solrServerUrl";
	private static final String SOLR_CORE_NAME_JOB = "solrCoreName_job";
	private static final String SOLR_CORE_NAME_APPLICANTS = "solrCoreName_applicants";
	
	static
	{
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		config = app.getResourceBundle(context, configBundleName);
	}
	
	private static String getConfig(String configKey)
	{
		return config.getString(configKey);
	}
	
	public static String getUploadFileDir()
	{
		return getConfig(UPLOAD_FILE_DIR);
	}
	
	public static String getSolrServerUrl()
	{
		return getConfig(SOLR_SERVER_URL);
	}
	
	public static String getSolrCoreNameJob()
	{
		return getConfig(SOLR_CORE_NAME_JOB);
	}	
	
	public static String getSolrCoreNameApplicants()
	{
		return getConfig(SOLR_CORE_NAME_APPLICANTS);
	}
}
