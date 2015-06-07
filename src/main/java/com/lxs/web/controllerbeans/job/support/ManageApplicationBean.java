package com.lxs.web.controllerbeans.job.support;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;

import com.lxs.business.ejb.entity.ApplicationEntity;
import com.lxs.business.service.ManageJobService;
import com.lxs.web.controllerbeans.job.utils.ApplicationStatusEnum;
import com.lxs.web.controllerbeans.job.utils.CommonConstants;
import com.lxs.web.controllerbeans.job.utils.CommonUtils;
import com.lxs.web.controllerbeans.job.utils.DownloadFileUtil;


@ManagedBean(name = "manageApplicationBean", eager = true)
@ViewScoped
public class ManageApplicationBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ManageApplicationBean.class);

	@ManagedProperty(value = "#{manageJobService}")
	private ManageJobService mjs;

	//private ApplicationEntity appEntity = new ApplicationEntity();
	//change to appModel
	//private List<ApplicationEntity> appList;
	private LazyDataModel<ApplicationEntity> appModel;
	
	
	//parameter values passed from joblist 
	//use param to inject: this doens't work if beans's scope is 'ViewScoped'.
	//it only works when bean's scope is @RequestScoped, and must have setter method
	//@ManagedProperty(value = "#{param.jobTitle}")
	private String jobTitle;
	//@ManagedProperty(value = "#{param.jobId}") //this need convert from string to int
	private int jobId;
	//@ManagedProperty(value = "#{param.jobNumber}")
	private String jobNumber;

	//application status, used for get applications beased on selected status in app list page
	private String status ;
	
	//sorting flag: move to LazyModel
	/*
	private boolean sortLastNameAsc = true;
	private boolean sortFirstNameAsc = true;
	private boolean sortAppiledDateAsc = true;
	*/
	
	@PostConstruct
	private void init()
	{
		logger.debug("manage application init() called");

		//get jobId from passed in param
		String sjobId = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
				.getExternalContext(), "jobId");
		if (sjobId != null && sjobId.length() > 0)
		{
			jobId = Integer.valueOf(sjobId);

			jobTitle = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
					.getExternalContext(), "jobTitle");
			jobNumber = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
					.getExternalContext(), "jobNumber");
					
			if(jobNumber == null)
			{
				jobNumber = "";
			}
				
			//use lazymodel
			appModel = new LazyApplicationDataModel(jobId,  mjs);

			if (appModel == null)
			{
				//this is a bad request
				//this throw is ignored, dont' work coz of @PostConstruct
				//throw new JobNotFoundException("Job not found with id="+jobId);
				logger.warn("appModel is empty, something wrong with request!!");
			}
			
			logger.debug("====jobId=" + jobId + "; jobTitle="
					+ jobTitle + "jobNumber="+jobNumber+"; appModel size==" + appModel.getPageSize());			
		}

	}

	public StreamedContent downloadResume(ApplicationEntity appEntity)
	{
		return DownloadFileUtil.downloadResume(appEntity.getResumeName());
	}
	
	/**
	 * A status dropdown used in datatable header to filter application
	 * This is required by Primefaces datatable
	 * @return
	 */
	public SelectItem[] getStatusOptions() {  
		SelectItem[] options = new SelectItem[ApplicationStatusEnum.values().length+1];  
		options[0] = new SelectItem("All", "All");  
        for(int i = 0; i < ApplicationStatusEnum.values().length; i++) {  
            options[i+1] = new SelectItem(ApplicationStatusEnum.values()[i].getDesc(), 
            		ApplicationStatusEnum.values()[i].getDesc());  
        }  
       
        return options;  
    }
	
	/* moved to lazyModel
	public String sortByFirstName()
	{
		logger.debug("sortByFirstName called");
		String sortDir=null;
		if (this.sortFirstNameAsc)
		{
			sortDir = "asc";
			this.sortFirstNameAsc = false;
		}
		else
		{
			sortDir = "desc";
			this.sortFirstNameAsc = true;
		}
		
		appList = mjs.getApplicantListByJobId(jobId, 0, pageSize, "firstName", sortDir);
		return null;
	}

	public String sortByLastName()
	{
		logger.debug("sortByLastName called");
		
		String sortDir=null;
		if (this.sortLastNameAsc)
		{
			sortDir = "asc";
			this.sortLastNameAsc = false;
		}
		else
		{
			sortDir = "desc";
			this.sortLastNameAsc = true;
		}
		appList = mjs.getApplicantListByJobId(jobId, 0, pageSize, "lastName", sortDir);
		return null;
	}

	public String sortByAppliedDate()
	{
		logger.debug("sortByAppliedDate called");
		String sortDir=null;
		if (this.sortAppiledDateAsc)
		{
			sortDir = "asc";
			this.sortAppiledDateAsc = false;
		}
		else
		{
			sortDir = "desc";
			this.sortAppiledDateAsc = true;
		}
		appList = mjs.getApplicantListByJobId(jobId, 0, pageSize, "appliedDate", sortDir);
		return null;
	}
	*/
	/****        getter/setter           ====================**/

	public void setMjs(ManageJobService mjs)
	{
		this.mjs = mjs;
	}

	public String getJobTitle()
	{
		return jobTitle;
	}

	public String getJobNumber()
	{
		return jobNumber;
	}

	public int getJobId()
	{
		return jobId;
	}
	
	public LazyDataModel<ApplicationEntity> getAppModel()
	{
		return appModel;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public void setJobTitle(String jobTitle)
	{
		this.jobTitle = jobTitle;
	}

	public void setJobNumber(String jobNumber)
	{
		this.jobNumber = jobNumber;
	}

}
