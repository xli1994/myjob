package com.lxs.business.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.dao.ApplicantDaoImpl;
import com.lxs.business.ejb.entity.ApplicationEntity;
import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.business.ejb.entity.StateCode;
import com.lxs.business.vo.SolrApplicantsVO;


@ManagedBean(name = "applicantService", eager = true)
@ViewScoped
public class ApplicantService implements Serializable
{
	private static final Logger logger = LogManager.getLogger(ApplicantService.class);
	private static final long serialVersionUID = 1L;
	
	@ManagedProperty(value="#{applicantDaoImpl}")
	private ApplicantDaoImpl appDao;
	
	@ManagedProperty(value = "#{searchService}")
	private SearchService searchService;
	
	public List<JobEntity> getActiveJobList()
	{
		logger.debug("Get Job list now...");
		
		return appDao.getActiveJobList();
	}
	
	public List<StateCode> getStateCodeList()
	{
		//when calling from static method (non-managed bean, appDao can't be injected,
		//manually initialize it:
		if(appDao == null)
		{
			appDao = new ApplicantDaoImpl();
		}
		
		return appDao.getStateCodeList();
	}
	
	public void saveApplication(ApplicationEntity appEntity, String jobTitle, String jobNumber)
	{
		logger.debug("Save application...");
		appDao.saveApplication(appEntity);
		
		//now save to solr:
		SolrApplicantsVO vo = new SolrApplicantsVO();
		vo.setAppId(appEntity.getAppId());
		vo.setAppliedDate(appEntity.getAppliedDate());
		vo.setAppliedJobId(appEntity.getAppliedJobId());
		vo.setAppliedJobNumber(jobNumber);
		vo.setAppliedJobTitle(jobTitle);
		vo.setCellPhone(appEntity.getCellPhone());
		vo.setPhone(appEntity.getPhone());
		vo.setEmail(appEntity.getEmail());
		vo.setFirstName(appEntity.getFirstName());
		vo.setLastName(appEntity.getLastName());
		vo.setResumeName(appEntity.getResumeName());
		vo.setStatus(appEntity.getStatus());
		
		searchService.addUpdateApplicant(vo);
	}
	
	
	/* ===================setter =================*/
	public void setAppDao(ApplicantDaoImpl appDao)
	{
		this.appDao = appDao;
	}
	
	public void setSearchService(SearchService searchService)
	{
		this.searchService = searchService;
	}
}
