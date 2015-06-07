package com.lxs.web.controllerbeans.job.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import com.lxs.business.service.ManageJobService;
import com.lxs.web.controllerbeans.job.utils.JobEntitySorter;

@ManagedBean(name = "manageJobListBean", eager = true)
@ViewScoped
public class ManageJobListBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ManageJobListBean.class);

	@ManagedProperty(value = "#{manageJobService}")
	private ManageJobService mjs;

	private JobEntity jobEntity = new JobEntity();

	private List<JobEntity> jobList;
	//this is used for deleting batch jobs by job id  
	private Map<Integer, Boolean> checked = new HashMap<Integer, Boolean>();
	
	//for sort
	private boolean sortAscending = true;
	
	//a jobId passed in from jobList page for update active status
	private String targetJobId;
	
	public ManageJobListBean()
	{

	}

	@PostConstruct
	private void init()
	{
		logger.debug("ManageJobBean init() called");
		
		//test isPostback method
		if(FacesContext.getCurrentInstance().isPostback())
		{
			logger.debug("ManageJobBean init() : isPostback is true!!!!!!");
		}
		else
		{
			logger.debug("ManageJobBean init() : isPostback is false=====!");
		}
		//load joblist
		jobList = mjs.getJobList();
		if(jobList == null || jobList.size()==0)
		{
			jobList = null;
			return;
		}
		//set checked to false for deletion
		for (JobEntity jobEntity: jobList)
		{
			checked.put(jobEntity.getJobId(), false);
		}
	}

	/**
	 * update job active status: true (publish) or false
	 * @return
	 */
	public String updateJobStatus(String publish)
	{
		logger.debug("updateJobStatus called, test some value: jobId=" + targetJobId
				+ "; publish=" + publish);

		String updatedBy = "lxs2";
		boolean status = false;
		if (publish != null && publish.equalsIgnoreCase("true"))
		{
			//publish it
			status = true;
		}

		Date now = new Date();
		mjs.updateJobStatus(Integer.valueOf(targetJobId), status, updatedBy, now);

		//not to reload, just update existing jobList
		for(JobEntity jobEntity : jobList)
		{
			if(jobEntity.getJobId() == Integer.valueOf(targetJobId))
			{
				jobEntity.setActiveStatus(status);
				jobEntity.setUpdatedBy(updatedBy);
				jobEntity.setUpdatedDate(now);
				
				break;
			}
		}
		
		return null;
	}

	/**
	 * delete single job
	 * 
	 * @param jobEntity
	 * @return
	 */
	public String deleteJob(JobEntity jobEntity)
	{
		mjs.deleteSingleJob(jobEntity);
		
		//not reset to call db, just remove it from jobList
		jobList.remove(jobEntity);
		
		return null;
	}
	
	/**
	 * delete all selected job
	 * @param employee
	 * @return
	 */
	public String deleteBatchJobs()
	{
		logger.debug("delete batch jobs....");
		List <Integer> delete = new ArrayList<Integer>();
		Iterator<JobEntity> it = jobList.iterator();
		
		while (it.hasNext())
		{
			JobEntity jobEntity = it.next();
			if(checked.get(jobEntity.getJobId()))
			{
				delete.add(jobEntity.getJobId());
				//delete it from jobList now
				it.remove();
			}
		}
		
		if(delete.size() > 0)
		{
			//delete from db
			mjs.deleteBatchJobs(delete);
		}

	    checked.clear();
	
		//no need to reset, already delete all from jobList in above iterate
	    //for checked Map, just leave it as is,
	    
		logger.debug("Done delete jobs");
		return null;
	}
	
	/**
	 * Sort by posting date (createDate)
	 * @return
	 */
	public String sortByPostDate()
	{
		logger.debug("sortByPostDate called: sortAscending="+sortAscending);
		
		Collections.sort(jobList, new JobEntitySorter("createdDate", sortAscending));
		this.sortAscending = !sortAscending;
		
		/* change to use sorter
		if (sortAscending)
		{			
			Collections.sort(jobList, new Comparator<JobEntity>()
			{
				@Override
				public int compare(JobEntity o1, JobEntity o2)
				{
					return o1.getCreatedDate().compareTo(o2.getCreatedDate());
				}
			});			
			sortAscending = false;
		}
		else
		{
			//descending order
			Collections.sort(jobList, new Comparator<JobEntity>()
			{
				@Override
				public int compare(JobEntity o1, JobEntity o2)
				{
					return o2.getCreatedDate().compareTo(o1.getCreatedDate());
				}
			});
			sortAscending = true;
		}
		*/
		return null;
	}

	/**
	 * Sort by Job Number (Job Code)
	 * @return
	 */
	public String sortByJobNumber()
	{
		logger.debug("sortByJobNUmber called: sortAscending="+sortAscending);
		Collections.sort(jobList, new JobEntitySorter("jobNumber", sortAscending));
		this.sortAscending = !sortAscending;
		
		return null;
	}


	/**
	 * Sort by Job Title =
	 * @return
	 */
	public String sortByJobTitle()
	{
		logger.debug("sortByJobTitle called: sortAscending="+sortAscending);
		Collections.sort(jobList, new JobEntitySorter("jobTitle", sortAscending));
		this.sortAscending = !sortAscending;
		
		return null;
	}
	
	/**
	 * Sort by Job status =
	 * @return
	 */
	public String sortByJobStatus()
	{
		logger.debug("sortByJobStatus called: sortAscending="+sortAscending);
		Collections.sort(jobList, new JobEntitySorter("activeStatus", sortAscending));
		this.sortAscending = !sortAscending;
		return null;
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
	
	public List<JobEntity> getJobList()
	{
		return jobList;
	}

	public Map<Integer, Boolean> getChecked()
	{
		return checked;
	}

	public void setTargetJobId(String targetJobId)
	{
		logger.debug("setTargetJobId() called targetJobId="+targetJobId);
		this.targetJobId = targetJobId;
	}
}
