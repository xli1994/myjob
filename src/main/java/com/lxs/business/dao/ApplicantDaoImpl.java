package com.lxs.business.dao;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.ApplicationEntity;
import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.business.ejb.entity.StateCode;
import com.lxs.web.servlets.EMF;

@ManagedBean(name = "applicantDaoImpl", eager = true)
@ViewScoped
public class ApplicantDaoImpl implements ApplicantDao, Serializable
{
	private static final long serialVersionUID = -1;
	private static final Logger logger = LogManager.getLogger(ApplicantDaoImpl.class);
	
	@Override
	public List<JobEntity> getActiveJobList()
	{
		logger.debug("ApplicantDaoImpl Get active Job list now...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();	
		Query query = em.createQuery("SELECT j FROM JobEntity j where j.activeStatus = :status order by j.createdDate desc");
		query.setParameter("status", true);
	    
		List<JobEntity> list = query.getResultList();
		em.getTransaction().commit();
		em.close();
		return list;
	}

	@Override
	public List<StateCode> getStateCodeList()
	{
		logger.debug("ApplicantDaoImpl Get state list now...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();	
		//use named query defined in StateCode.class
		TypedQuery<StateCode> query = em.createNamedQuery("queryState", StateCode.class);

		List<StateCode> list = query.getResultList();
		em.getTransaction().commit();
		em.close();
		return list;
	}

	@Override
	public void saveApplication(ApplicationEntity appEntity)
	{
		logger.debug("ApplicantDaoImpl save application now...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();	
		em.persist(appEntity);
		em.getTransaction().commit();
		em.close();
		
	}
	
}
