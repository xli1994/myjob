package com.lxs.web.controllerbeans.job.utils;


public interface CommonConstants
{
	String JOB_NOT_FOUND_PAGE = "/myjob/joblist/jobnotfound.jsf";
	String ERROR_PAGE = "/myjob/joblist/error.jsf";
	
	//use "f:" + File.separator + "temp_f" + File.separator: move to ConfigUtil
	//String UPLOAD_FILE_DIR = "F:" + File.separator + "eclipseWorkSpace"+ File.separator +"uploadedResume"
	//		+ File.separator;
	
	//pagination size, used for get applicatins list
	int PAGINATION_SIZE = 5;
	
	//search job status parameter
	String ACTIVE = "active";
	String INACTIVE = "inactive";
			
	//view job page name from search result list page
	String ADMIN_VIEW_JOB_PAGE ="preview";
	String PUBLIC_VIEW_JOB_PAGE ="viewjob";
	
}
