package com.lxs.web.controllerbeans.job.client;


import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.business.service.ApplicantService;
import com.lxs.web.controllerbeans.job.utils.ApplicationEntitySorter;
import com.lxs.web.controllerbeans.job.utils.JobEntitySorter;

@ManagedBean(name = "applicantBean", eager = true)
@ViewScoped
public class ApplicantBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ApplicantBean.class);

	@ManagedProperty(value = "#{applicantService}")
	private ApplicantService aps;

	private JobEntity jobEntity = new JobEntity();

	private List<JobEntity> activeJobList;

	//for sort
	private boolean sortAscending = true;


	@PostConstruct
	private void init()
	{
		logger.debug("ApplicantBean init() called");
		//load activeJobList
		activeJobList = aps.getActiveJobList();

	}


	/**
	 * Sort by posting date (createDate)
	 * @return
	 */
	public String sortByPostDate()
	{
		logger.debug("sortByPostDate called: sortAscending="+sortAscending);
		Collections.sort(activeJobList, new JobEntitySorter("createdDate", sortAscending));
		this.sortAscending = !sortAscending;

		return null;
	}
	

	/**
	 * Sort by Location
	 * @return
	 */
	public String sortByLocation()
	{
		logger.debug("sortByLocation called: sortAscending="+sortAscending);
		Collections.sort(activeJobList, new JobEntitySorter("location", sortAscending));
		this.sortAscending = !sortAscending;
		return null;
	}

	/**
	 * Sort by jobType
	 * @return
	 */
	public String sortByJobType()
	{
		logger.debug("sortByJobType called: sortAscending="+sortAscending);
		Collections.sort(activeJobList, new JobEntitySorter("jobType", sortAscending));
		this.sortAscending = !sortAscending;

		return null;
	}

	/**
	 * Sort by jobTitle
	 * @return
	 */
	public String sortByJobTitle()
	{
		logger.debug("sortByJobTitle called: sortAscending="+sortAscending);
		Collections.sort(activeJobList, new JobEntitySorter("jobTitle", sortAscending));
		this.sortAscending = !sortAscending;

		return null;
	}

	
	public void setAps(ApplicantService aps)
	{
		this.aps = aps;
	}

	public JobEntity getJobEntity()
	{
		return jobEntity;
	}

	
	public List<JobEntity> getActiveJobList()
	{
		return activeJobList;
	}


}
