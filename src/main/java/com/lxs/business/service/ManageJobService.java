package com.lxs.business.service;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.dao.ManageJobDao;
import com.lxs.business.dao.SearchJobSolrDao;
import com.lxs.business.ejb.entity.ApplicationEntity;
import com.lxs.business.ejb.entity.JobEntity;

@ManagedBean(name = "manageJobService", eager = true)
@ViewScoped
public class ManageJobService implements Serializable
{
	private static final Logger logger = LogManager.getLogger(ManageJobService.class);
	private static final long serialVersionUID = 1L;

	@ManagedProperty(value = "#{manageJobDaoImpl}")
	private ManageJobDao mjDao;

	
	@ManagedProperty(value = "#{searchService}")
	private SearchService searchService;
	
	public int addJob(JobEntity job)
	{
		logger.debug("Inerset job=" + job);
		int key = mjDao.addJob(job);
		
		//add to solr
		searchService.addUpdateJob(job);
		
		return key;
	}

	public List<JobEntity> getJobList()
	{
		logger.debug("Get Job list now...");

		return mjDao.getJobList();
	}

	public JobEntity getSingleJobEntity(int jobId)
	{
		logger.debug("Get single job now...");
		return mjDao.getJobById(jobId);
	}

	public JobEntity getJobByIdAndStatus(int jobId, boolean status)
	{
		logger.debug("Get single job by id and status now...");
		return mjDao.getJobByIdAndStatus(jobId, status);
	}

	public void updateJob(JobEntity job)
	{
		logger.debug("Updating job=" + job);

		mjDao.updateJob(job);
		//update solr
		searchService.addUpdateJob(job);
	}

	public void deleteSingleJob(JobEntity job)
	{
		logger.debug("deleting single job =" + job);
		mjDao.deleteJob(job);
		
		//delete from solr
		searchService.deleteJob(job.getJobId());
	}

	public void deleteBatchJobs(List<Integer> jobList)
	{
		logger.debug("deleting batch jobs =" + jobList);
		mjDao.deleteBatchJobs(jobList);
		
		//delete from solr
		searchService.deleteBatchJobs(jobList);
	}

	public void updateJobStatus(int jobId, boolean status, String updatedBy, Date updatedDate)
	{
		logger.debug("update ob status: jobId=" + jobId + "; status=" + status);
		mjDao.updateJobStatus(jobId, status, updatedBy, updatedDate);
		
		//update solr
		searchService.updateJobStatus(jobId, status);
	}

	public List<ApplicationEntity> getApplicantListByJobId(int jobId, int pageIndex, int pageSize,
			String orderBy, String orderDir, String status)
	{
		logger.debug("getApplicantListByJobId with jobId=" + jobId);
		return mjDao.getApplicantListByJobId(jobId, pageIndex, pageSize, orderBy, orderDir, status);
	}

	public int getAppilcantCountByJobId(int jobId, String status)
	{
		return mjDao.getAppilcantCountByJobId(jobId, status);
	}

	public ApplicationEntity getAppilcantProfileByJobId(int appId)
	{
		return mjDao.getAppilcantProfileByJobId(appId);
	}

	public void updateApplicationStatus(int appId, String status, String comments,
			String commentedBy, Date commentedDate)
	{
		mjDao.updateApplicationStatus(appId, status, comments, commentedBy, commentedDate);
		
		//update solr too
		searchService.updateApplicationStatus(appId, status);
	}

	
	
	/* ====== setter ==============*/
	public void setMjDao(ManageJobDao mjDao)
	{
		this.mjDao = mjDao;
	}
	
	
	public void setSearchService(SearchService searchService)
	{
		this.searchService = searchService;
	}
}
