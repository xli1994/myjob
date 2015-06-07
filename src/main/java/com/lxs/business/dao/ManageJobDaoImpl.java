package com.lxs.business.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.ApplicationEntity;
import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.web.servlets.EMF;

@ManagedBean(name = "manageJobDaoImpl", eager = true)
@ViewScoped
public class ManageJobDaoImpl implements ManageJobDao, Serializable
{
	private static final long serialVersionUID = -1;

	private static final Logger logger = LogManager.getLogger(ManageJobDaoImpl.class);

	@Override
	public int addJob(JobEntity job)
	{
		logger.debug("Dao add job...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		em.persist(job);

		em.getTransaction().commit();
		em.close();
		return job.getJobId();
	}

	@Override
	public List<JobEntity> getJobList()
	{
		logger.debug("Dao Get Job list now...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		//use native query
		String sQuery = "SELECT j.jobId as jobId, j.jobTitle as jobTitle, j.jobNumber as jobNumber, "
				+ " j.activeStatus as activeStatus, j.createdDate as createdDate,  count(a.appid) as applicantNumber "
				+ " from lxstest.joblist j left join  jobapplications a on j.jobId = a.appliedJobId "
				+ " group by j.jobId order by j.createdDate desc ";
		//Query query = em.createQuery("SELECT j FROM JobEntity j order by j.createdDate desc ");

		Query query = em.createNativeQuery(sQuery, "jobEntityMapping");

		List<JobEntity> list = query.getResultList();

		em.getTransaction().commit();
		em.close();
		logger.debug("Dao got Job list size=" + list.size());

		return list;
	}

	@Override
	public JobEntity getJobById(int jobId)
	{
		logger.debug("Dao Get single job now...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		JobEntity jobEntity = em.find(JobEntity.class, jobId);

		em.getTransaction().commit();
		em.close();
		return jobEntity;
	}

	@Override
	public void updateJob(JobEntity job)
	{
		logger.debug("Dao update job...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		em.merge(job);

		em.getTransaction().commit();
		em.close();
	}

	@Override
	public void deleteJob(JobEntity job)
	{
		logger.debug("Dao delete single job...:" + job);
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		job = em.getReference(JobEntity.class, job.getJobId());
		em.remove(job);
		em.getTransaction().commit();
	}

	@Override
	public void deleteBatchJobs(List<Integer> jobList)
	{
		logger.debug("Deleteing batch jobs: list=" + jobList);
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		Query query = em.createQuery("DELETE FROM JobEntity j where j.jobId in :idList");
		query.setParameter("idList", jobList);
		int row = query.executeUpdate();
		em.getTransaction().commit();
		logger.debug("deleted jobs row=" + row);
		em.close();

	}

	@Override
	public void updateJobStatus(int jobId, boolean status, String updatedBy, Date updatedDate)
	{
		logger.debug("Dao update job status now...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		String sQurey = "UPDATE JobEntity j SET j.activeStatus = :status, j.updatedBy = :updatedBy, "
				+ "j.updatedDate = :updatedDate WHERE j.jobId = :jobId ";
		Query query = em.createQuery(sQurey);
		query.setParameter("status", status);
		query.setParameter("updatedBy", updatedBy);
		query.setParameter("updatedDate", updatedDate);
		query.setParameter("jobId", jobId);
		query.executeUpdate();
		em.getTransaction().commit();
		em.close();

	}

	@Override
	public JobEntity getJobByIdAndStatus(int jobId, boolean status)
	{
		logger.debug("Dao Get Job list now...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		Query query = em
				.createQuery("SELECT j FROM JobEntity j WHERE j.jobId = :jobId AND j.activeStatus = :status");
		query.setParameter("jobId", jobId);
		query.setParameter("status", status);
		List<JobEntity> list = query.getResultList();
		if (list == null || list.size() == 0)
		{
			logger.warn("Job is not found with jobId=" + jobId + "; someone made bad request!!");
			em.getTransaction().commit();
			em.close();
			return null;
		}

		JobEntity jobEntity = list.get(0);
		em.getTransaction().commit();
		em.close();
		return jobEntity;
	}

	/**
	 * get applicant list for a specific job id, with pagination
	 * see http://www.javacodegeeks.com/2013/04/jpa-2-0-criteria-query-with-hibernate.html
	 * for query
	 */
	@Override
	public List<ApplicationEntity> getApplicantListByJobId(int jobId, int pageIndex, int pageSize,
			String orderBy, String orderDir, String status)
	{
		logger.debug("Dao Get getApplicantListByJobId for jobId=" + jobId + "; pageIndex="
				+ pageIndex + "; pageSize=" + pageSize + "; orderBy=" + orderBy + ";orderDir="
				+ orderDir + "; status=" + status);
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();

		final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<ApplicationEntity> criteriaQuery = criteriaBuilder
				.createQuery(ApplicationEntity.class);
		Root<ApplicationEntity> root = criteriaQuery.from(ApplicationEntity.class);
		//query.select(root);
		//This list contains all Predicates (where clauses), in this case, we have only one, but you may add more
		List criteriaList = new ArrayList();

		Predicate predicate1 = criteriaBuilder.equal(root.get("appliedJobId"), jobId);
		criteriaList.add(predicate1);

		if (status != null && !status.equalsIgnoreCase("All"))
		{
			Predicate predicate2 = criteriaBuilder.equal(root.get("status"), status);
			criteriaList.add(predicate2);
		}
		// Pass the criteria list to the where method of criteria query
		criteriaQuery.where(criteriaBuilder.and((Predicate[]) criteriaList
				.toArray(new Predicate[0])));

		// Order by clause
		if (StringUtils.isEmpty(orderBy))
		{
			criteriaQuery.orderBy(criteriaBuilder.asc(root.get("appliedDate")));
		}
		else
		{
			if ("asc".equalsIgnoreCase(orderDir))
			{
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get(orderBy)));
			}
			else
			{
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get(orderBy)));
			}
		}

		// Here entity manager will create actual SQL query out of criteria query
		final TypedQuery<ApplicationEntity> query = em.createQuery(criteriaQuery);

		query.setFirstResult(pageIndex * pageSize);
		query.setMaxResults(pageSize);

		List<ApplicationEntity> list = query.getResultList();
		em.getTransaction().commit();
		em.close();
		logger.debug("Dao got getApplicant List size=" + list.size());

		return list;

	}

	/**
	 * Get appilcatin number for a specific job id
	 */
	@Override
	public int getAppilcantCountByJobId(int jobId, String status)
	{
		logger.debug("Dao Get applicant count by jobid= " + jobId + "; status=" + status);
		boolean queryAll = false;
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		String sQuery = null;
		if (status == null || status.equalsIgnoreCase("All"))
		{
			queryAll = true;
		}

		if (queryAll)
		{
			sQuery = "SELECT count(*) FROM jobApplications a WHERE a.appliedJobId = :jobId";
		}
		else
		{
			sQuery = "SELECT count(*) FROM jobApplications a WHERE a.appliedJobId = :jobId and a.status = :status";
		}
		Query query = em.createNativeQuery(sQuery);
		query.setParameter("jobId", jobId);
		if (!queryAll)
		{
			query.setParameter("status", status);
		}

		int count = ((Number) query.getSingleResult()).intValue();
		em.getTransaction().commit();
		em.close();
		return count;
	}

	@Override
	public ApplicationEntity getAppilcantProfileByJobId(int appId)
	{
		logger.debug("Dao Get single ApplicationEntity now...");
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		ApplicationEntity appEntity = em.find(ApplicationEntity.class, appId);

		em.getTransaction().commit();
		em.close();

		return appEntity;
	}

	@Override
	public void updateApplicationStatus(int appId, String status, String comments,
			String commentedBy, Date commentedDate)
	{

		logger.debug("Dao update application status-- appId=" + appId + "; status=" + status + "; comments=" + comments
				+ "; commentedBy=" + commentedBy);
		
		EntityManager em = EMF.createEntityManager();
		em.getTransaction().begin();
		String sQurey = "UPDATE ApplicationEntity a SET a.status = :status,  a.comments = :comments,"
				+ "a.commentedBy = :commentedBy, "
				+ "a.commentedDate = :commentedDate WHERE a.appId = :appId ";
		Query query = em.createQuery(sQurey);
		query.setParameter("status", status);
		query.setParameter("comments", comments);
		query.setParameter("commentedBy", commentedBy);
		query.setParameter("commentedDate", commentedDate);
		query.setParameter("appId", appId);
		query.executeUpdate();
		em.getTransaction().commit();
		em.close();
		logger.debug("update application status and comments ---done!");
	}

}
