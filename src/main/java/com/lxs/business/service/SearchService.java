package com.lxs.business.service;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.dao.SearchApplicantsSolrDao;
import com.lxs.business.dao.SearchJobSolrDao;
import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.business.vo.SearchResultList;
import com.lxs.business.vo.SolrApplicantsVO;

@ManagedBean(name = "searchService", eager = true)
@ViewScoped
public class SearchService implements Serializable
{
	private static final Logger logger = LogManager.getLogger(SearchService.class);
	private static final long serialVersionUID = 1L;

	//search job solr dao
	@ManagedProperty(value = "#{searchJobSolrDao}")
	private SearchJobSolrDao solrJob;

	//search applicant solr dao
	@ManagedProperty(value = "#{searchApplicantsSolrDao}")
	private SearchApplicantsSolrDao solrApp;

	/* =========== Solr Job actions ===============*/
	public List<JobEntity> searchJob(String sQuery, String status)
	{
		logger.debug("SearchService Search job now...");
		return solrJob.searchJob(sQuery, status);
	}

	/**
	 * Add /update job to solr
	 * @param jobEntity
	 */
	public void addUpdateJob(JobEntity jobEntity)
	{
		solrJob.addUpdateJob(jobEntity);
	}
	
	public void deleteJob(int jobId)
	{
		solrJob.deleteJob(jobId);
	}
	
	public void deleteBatchJobs(List<Integer> jobList)
	{
		solrJob.deleteBatchJobs(jobList);
	}
	
	public void updateJobStatus(int jobId, boolean status)
	{
		solrJob.updateJobStatus(jobId, status);
	}
	
	
	
	/* ================= Solr Applicants actions ================*/
	

	/**
	 * Search applicants
	 * @param sQuery
	 * @param pageIndex
	 * @param pageSize
	 * @param orderBy
	 * @param orderDir
	 * @return
	 */
	public SearchResultList<SolrApplicantsVO> searchApplicants(String sQuery, int pageIndex,
			int pageSize, String orderBy, String orderDir)
	{
		logger.debug("search service searchApplicants...");
		return solrApp.searchApplicants(sQuery, pageIndex, pageSize, orderBy, orderDir)	;
		
	}

	/**
	 * Add applicant to Solr
	 * @param vo
	 */
	public void addUpdateApplicant(SolrApplicantsVO vo)
	{
		logger.debug("search service add applicant to solr...");
		solrApp.addUpdateApplicant(vo);
	}
	
	/**
	 * Update Application status
	 * 
	 * @param appId
	 * @param status
	 */
	public void updateApplicationStatus(int appId, String status)
	{
		logger.debug("search service updateApplicationStatus to solr...");
		solrApp.updateApplicationStatus(appId, status);
	}
	
	/*    ============= setter ==================*/
	public void setSolrJob(SearchJobSolrDao solrJob)
	{
		this.solrJob = solrJob;
	}

	public void setSolrApp(SearchApplicantsSolrDao solrApp)
	{
		this.solrApp = solrApp;
	}
}
