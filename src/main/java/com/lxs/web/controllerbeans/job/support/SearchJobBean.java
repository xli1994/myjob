package com.lxs.web.controllerbeans.job.support;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.business.service.SearchService;
import com.lxs.web.controllerbeans.job.utils.CommonConstants;
import com.lxs.web.controllerbeans.job.utils.CommonUtils;

@ManagedBean(name = "searchJobBean", eager = true)
@ViewScoped
public class SearchJobBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SearchJobBean.class);
	
	@ManagedProperty(value = "#{searchService}")
	private SearchService searchService ;
	
	private List<JobEntity> jobList;
	
	private String query;
	
	//this is "outcome" result page name used by serachresulttemplate.xhtml, to direct use to 
	//differnt "view" page, for admin, it's "preview.xhtml", for public, it's "viewjob.xhtml"
	private String viewJobPage;
	
	@PostConstruct
	private void init()
	{
		query = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
				.getExternalContext(), "query");
		logger.debug("searchService init() called, query="+query);
		
		if(StringUtils.isEmpty(query))
		{
			return;
		}
		
		//check url, if it's outside (applicants), load active job only
		if(!isAdmin())
		{
			logger.debug("This is client request, search  active job only");
			jobList = searchService.searchJob(query, CommonConstants.ACTIVE);
			viewJobPage = CommonConstants.PUBLIC_VIEW_JOB_PAGE;
		}
		else if(isAdmin())
		{
			logger.debug("This is admin search all job");
			jobList = searchService.searchJob(query, null);
			viewJobPage = CommonConstants.ADMIN_VIEW_JOB_PAGE;
		}
		
	}
	
	/**
	 * This is to return list size, and used to display result number in UI
	 * @return
	 */
	public String getListSize()
	{
		if(jobList != null)
		{
			return String.valueOf(jobList.size());
		}
		
		return "";
	}
	
	/** 
	 * Return view job page based on if it's admin or public
	 * 
	 * @return
	 */
	public String getViewJobPage()
	{
		return viewJobPage;
	}
	
	/**
	 * This is to check if a request is from admin
	 * In reality, this should be checked by by user principal
	 * @return
	 */
	public boolean isAdmin()
	{
		String requestUri = this.getServlet().getRequestURI();
		logger.debug("search job requestUri="+requestUri);
		if(requestUri.contains(UriPathEnum.ADMIN.getDesc()))
		{
			return true;
		}
		
		return false;
	}
	
	
	/**
	 * This is for search job, redirect to corresponding page
	 * @return
	 */
	public String searchJob()
	{
		logger.debug("searchJob called, query="+query);
		if(StringUtils.isEmpty(query))
		{
			return null;
		}
		
		//jobList = searchService.searchJob(query, null);
		//logger.debug("searchJob got result, size="+jobList.size());
		//redirect, a=true mean it's for admin
		if(isAdmin())
		{
			return "adminsearchresult?query="+query +"&faces-redirect=true";
		}
		
		//return public site
		return "searchresult?query="+query +"&faces-redirect=true";
	}
	
	
	private HttpServletRequest getServlet()
	{
		return (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
	}
	
	
	
	/*  ========getter/setter  ======================*/
	
	public void setSearchService(SearchService searchService)
	{
		this.searchService = searchService;
	}

	public List<JobEntity> getJobList()
	{
		return jobList;
	}

	public void setJobList(List<JobEntity> jobList)
	{
		this.jobList = jobList;
	}

	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}
}
