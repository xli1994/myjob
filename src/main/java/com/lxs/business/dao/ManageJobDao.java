package com.lxs.business.dao;

import java.util.Date;
import java.util.List;

import com.lxs.business.ejb.entity.ApplicationEntity;
import com.lxs.business.ejb.entity.JobEntity;

public interface ManageJobDao
{
	public int addJob(JobEntity job);

	public List<JobEntity> getJobList();

	public JobEntity getJobById(int jobId);
	
	public JobEntity getJobByIdAndStatus(int jobId, boolean status);
	
	public void updateJob(JobEntity job);

	public void deleteJob(JobEntity job);
	
	public void deleteBatchJobs(List<Integer> jobList);
	
	public void updateJobStatus(int jobId, boolean status, String updatedBy, Date updatedDate);
	
	//with pagination
	public List<ApplicationEntity> getApplicantListByJobId(int jobId, int pageIndex, int pageSize, String orderBy, String orderDir, String status);
	
	public int getAppilcantCountByJobId(int jobId, String status);
	
	public ApplicationEntity getAppilcantProfileByJobId(int appId);
	
	public void updateApplicationStatus(int appId, String status, String comments, String commentedBy, Date commentedDate);
}
