package com.lxs.web.controllerbeans.job.support;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.business.service.ManageJobService;
import com.lxs.web.controllerbeans.job.utils.CommonConstants;
import com.lxs.web.controllerbeans.job.utils.CommonUtils;

/**
 * This bean is for view job only nothing else.
 * 
 * @author lxs
 *
 */
@ManagedBean(name = "viewJobBean", eager = true)
@ViewScoped
public class ViewJobBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ViewJobBean.class);

	@ManagedProperty(value = "#{manageJobService}")
	private ManageJobService mjs;

	private JobEntity jobEntity = new JobEntity();
	
	//flag to allow "edit button" in view page for admin
	private boolean allowEdit = false;
	
	@PostConstruct
	private void init() 
	{
		logger.debug("init() called");

		//set edit mode if jobId is not empty
		String jobId = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance().getExternalContext(), "jobId");
		if (jobId != null && jobId.length() > 0)
		{

			logger.debug("This job exists in db with jobId=" + jobId);
			if(!isAdmin())
			{
				logger.debug("This is client request, load active job only");
				jobEntity = mjs.getJobByIdAndStatus(Integer.valueOf(jobId), true);
			}
			else if(isAdmin())
			{
				logger.debug("This is admin request");
				//this job is already in db, need to load it from db
				jobEntity = mjs.getSingleJobEntity(Integer.valueOf(jobId));
				allowEdit = true;
			}
			else
			{
				String msg = "Invalid request from unknown resource!!!";
				logger.error(msg);
				throw new RuntimeException(msg);
			}

			if(jobEntity == null || jobEntity.getJobId() < 1)
			{
				//this is a bad request
				//this throw is ignored, dont' work coz of @PostConstruct
				//throw new JobNotFoundException("Job not found with id="+jobId);
				logger.warn("jobEntity is null, redirect to error page!!");
				//forward to error page
			    try
				{
			    	//this is forward (works), change to redirect
					//FacesContext.getCurrentInstance().getExternalContext().dispatch(errorPage);
			    	
			    	//redirect must include context path, 
			    	FacesContext.getCurrentInstance().getExternalContext().redirect(CommonConstants.JOB_NOT_FOUND_PAGE);
					return;
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
	
	/**
	 * This is to check if a request is from admin
	 * In reality, this should be checked by by user principal
	 * @return
	 */
	private boolean isAdmin()
	{
		String requestUri = this.getServlet().getRequestURI();
		logger.debug("search job requestUri="+requestUri);
		if(requestUri.contains(UriPathEnum.ADMIN.getDesc()))
		{
			return true;
		}
		
		return false;
	}
	
	private HttpServletRequest getServlet()
	{
		return (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
	}
	
	public JobEntity getJobEntity()
	{
		return jobEntity;
	}
	
	/**
	 * flag to allow "edit button" in view page for admin
	 * @return
	 */
	public boolean isAllowEdit()
	{
		return allowEdit;
	}
	
	public void setMjs(ManageJobService mjs)
	{
		this.mjs = mjs;
	}
}
