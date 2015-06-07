package com.lxs.web.controllerbeans.job.support;

import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.business.service.ManageJobService;
import com.lxs.web.controllerbeans.job.exceptions.JobNotFoundException;

import com.lxs.web.controllerbeans.job.utils.CommonConstants;
import com.lxs.web.controllerbeans.job.utils.CommonUtils;
import com.lxs.web.controllerbeans.job.utils.EmailValidator;
import com.lxs.web.controllerbeans.job.utils.JobTypeEnum;
import com.lxs.web.controllerbeans.job.utils.MessageProvider;

@ManagedBean(name = "addEditJobBean", eager = true)
@ViewScoped
public class AddEditJobBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AddEditJobBean.class);

	@ManagedProperty(value = "#{manageJobService}")
	private ManageJobService mjs;

	private JobEntity jobEntity = new JobEntity();

	//resource bundle name
	private static String bundleName = "validatorMsg";
	
	//jobType drodown
	private JobTypeEnum jobTypeEnum;

	public AddEditJobBean()
	{

	}

	@PostConstruct
	private void init()
	{
		logger.debug("init() called");

		//set edit mode if jobId is not empty
		String jobId = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
				.getExternalContext(), "jobId");
		if (!StringUtils.isEmpty(jobId))
		{
			//this is only accessible from admin url
			logger.debug("This is admin request");
			//this job is already in db, need to load it from db
			jobEntity = mjs.getSingleJobEntity(Integer.valueOf(jobId));

			if (jobEntity == null || jobEntity.getJobId() < 1)
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
					FacesContext.getCurrentInstance().getExternalContext()
							.redirect(CommonConstants.JOB_NOT_FOUND_PAGE);
					return;
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//this is a eidt or replicate, we need to revert linebreak from "||" to "\n" 
			//for display properly in edit page
			jobEntity = CommonUtils.revertLineBreakToHtmlJobEntity(jobEntity);

			//is this replicate request?
			String replicate = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
					.getExternalContext(), "rep");
			if (replicate != null && replicate.equalsIgnoreCase("true"))
			{
				//this is a replicate request, reset job id, and some fields
				jobEntity.setJobId(0);
				jobEntity.setJobNumber("");
				jobEntity.setCreatedBy(null);
				jobEntity.setCreatedDate(null);
				jobEntity.setUpdatedBy(null);
				jobEntity.setUpdatedDate(null);
			}

		}

		if ( StringUtils.isEmpty(jobEntity.getJobType()) )
		{
			//logger.debug("Jobtype is empty, set to full time");
			//set default jobType= Full type	
			jobEntity.setJobType(JobTypeEnum.FULLTIME.getDesc());
		}

	}

	/**
	 * Add a new job or update one
	 * @return
	 * @throws JobNotFoundException 
	 */
	public String addUpdateJob(String publish)
	{
		logger.debug("addUpdate called, test some value: jobId=" + jobEntity.getJobId()
				+ "; title=" + jobEntity.getJobTitle() + "; type=" + jobEntity.getJobType()
				+ "; publish=" + publish);
		//validate 
		if (!isInputValid())
		{
			logger.debug("Validate failed!!!");
			return null;
		}

		//check length of jobtitle
		//logger.debug("Job tile ="+jobEntity.getJobTitle()+"; length="+jobEntity.getJobTitle().length());

		if (publish != null && publish.equalsIgnoreCase("true"))
		{
			//publish it
			jobEntity.setActiveStatus(true);
		}
		else
		{
			//save only, publish later
			jobEntity.setActiveStatus(false);
		}

		//this works, try use System.getProperty("line.separator")
		//jobEntity.setAboutCompany(jobEntity.getAboutCompany().replace("\n", "<br />\n"));

		//process some text fields to convert linebreak "\n" to "||" for display
		jobEntity = CommonUtils.convertLineBreakFromHtmlJobEntity(jobEntity);

		//if jobId exist, update it, otherwise insert it
		int jobId = -1;
		boolean update = false;
		if (jobEntity.getJobId() > 0)
		{
			//update
			update = true;
			jobId = jobEntity.getJobId();
			jobEntity.setUpdatedBy("lxs");
			jobEntity.setUpdatedDate(new Date());
			logger.debug("Updating job...job=" + jobEntity);
			mjs.updateJob(jobEntity);
		}
		else
		{
			//insert to db:
			jobEntity.setCreatedBy("lxs");
			jobEntity.setCreatedDate(new Date());
			logger.debug("Inserting new job....job=" + jobEntity);
			jobId = mjs.addJob(jobEntity);
		}

		return "addeditsuccess?title=" + jobEntity.getJobTitle() + "&jobId=" + jobId + "&update="
				+ update + "&faces-redirect=true";
		//return null;
	}

	/**
	 * Validate input 
	 * @return
	 */
	private boolean isInputValid()
	{
		boolean isValid = true;

		EmailValidator validator = new EmailValidator();
		if (!StringUtils.isEmpty(jobEntity.getEmailTo()))
		{
			String[] recipientEmails = jobEntity.getEmailTo().split(",");
			for (String email : recipientEmails)
			{
				logger.debug("start vailidate email=" + email);
				FacesMessage fmsg = validator.validate(email);

				if (fmsg != null)
				{
					logger.debug("validate failed: errormsg=" + fmsg.getDetail());
					FacesContext.getCurrentInstance().addMessage("jobForm:emailTo", fmsg);
					isValid = false;
				}
			}
		}

		if (StringUtils.isEmpty(jobEntity.getJobTitle()) || jobEntity.getJobTitle().trim().length() < 2)
		{
			String msg = MessageProvider.getValue("requiredMsg", bundleName);
			logger.debug("isInpuValid:: subject is not valid=" + jobEntity.getJobTitle());
			//pass parameter {0} to message 
			msg = MessageFormat.format(msg, "Job Title");
			FacesMessage fmsg = new FacesMessage(msg);
			fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("jobForm:jobTitle", fmsg);
			isValid = false;
		}

		if (StringUtils.isEmpty(jobEntity.getLocation()) || jobEntity.getLocation().trim().length() < 2)
		{
			String msg = MessageProvider.getValue("requiredMsg", bundleName);
			logger.debug("isInpuValid:: content is not valid=" + jobEntity.getLocation());
			//pass parameter {0} to message 
			msg = MessageFormat.format(msg, "Location");
			FacesMessage fmsg = new FacesMessage(msg);
			fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("jobForm:location", fmsg);
			isValid = false;
		}

		if (StringUtils.isEmpty(jobEntity.getJobDesc()) || jobEntity.getJobDesc().trim().length() < 2)
		{
			String msg = MessageProvider.getValue("requiredMsg", bundleName);
			logger.debug("isInpuValid:: content is not valid=" + jobEntity.getJobDesc());
			//pass parameter {0} to message 
			msg = MessageFormat.format(msg, "Description");
			FacesMessage fmsg = new FacesMessage(msg);
			fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("jobForm:jobDesc", fmsg);
			isValid = false;
		}
		
		if (StringUtils.isEmpty(jobEntity.getQualification()) || jobEntity.getQualification().trim().length() < 2)
		{
			String msg = MessageProvider.getValue("requiredMsg", bundleName);
			logger.debug("isInpuValid:: content is not valid=" + jobEntity.getQualification());
			//pass parameter {0} to message 
			msg = MessageFormat.format(msg, "Qualifications");
			FacesMessage fmsg = new FacesMessage(msg);
			fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("jobForm:qualification", fmsg);
			isValid = false;
		}
		
		return isValid;
	}

	/**
	 * This is to preview job
	 */
	public String preview()
	{
		logger.debug("Preview is called, job Title=" + jobEntity.getJobTitle());

		//make a copy of original jobEntity, then convert "\n" to "||" for preview, keep original intact
		JobEntity copy = (JobEntity) jobEntity.clone();
		//replace \n:
		copy = CommonUtils.convertLineBreakFromHtmlJobEntity(copy);
		//save current jobEntity to request and pass to preview page
		setJobEntityToRequest(copy);

		return "preview";
	}

	/**
	 * This is to set jonEntity to request attribute for use in jsf view page
	 */
	private void setJobEntityToRequest(JobEntity jobEntity)
	{
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		request.setAttribute("jobEntity", jobEntity);
	}

	private HttpServletRequest getServletRequest()
	{
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
	}

	/**
	 * This works, change to use enum directly , see below
	 * @return
	 */
	/*
	public Map getJobTypeMap()
	{
		
		return AllDropdowns.getJobTypeMap();
	}
	*/

	/**
	 * Use enum directly to popolate jobtype dropdown
	 * 
	 * @return
	 */
	public JobTypeEnum[] getJobTypeEnums()
	{
		return JobTypeEnum.values();
	}

	/**
	 * handle Jobtype change event and dynamically display ContractDuration field
	 * @param e
	 */
	/* This is not needed any more, change to use javascript code
	public void jobTypeChanged(ValueChangeEvent e)
	{
		String jobType = e.getNewValue().toString();
		logger.debug("jobTypeChanged, new value=" + jobType);
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		request.setAttribute("jobType", jobType);
	}
	*/

	public void setMjs(ManageJobService mjs)
	{
		this.mjs = mjs;
	}

	public JobEntity getJobEntity()
	{
		return jobEntity;
	}

	public void setJobEntity(JobEntity jobEntity)
	{
		this.jobEntity = jobEntity;
	}

	public JobTypeEnum getJobTypeEnum()
	{
		return jobTypeEnum;
	}

	public void setJobTypeEnum(JobTypeEnum jobTypeEnum)
	{
		this.jobTypeEnum = jobTypeEnum;
	}

}
