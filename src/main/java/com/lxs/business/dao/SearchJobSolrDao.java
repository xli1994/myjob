package com.lxs.business.dao;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.web.controllerbeans.job.utils.CommonConstants;

@ManagedBean(name = "searchJobSolrDao", eager = true)
@ViewScoped
public class SearchJobSolrDao implements Serializable
{
	private static final long serialVersionUID = -1;
	private static final Logger logger = LogManager.getLogger(SearchJobSolrDao.class);

	//solr returned date with UTC format: Thu Apr 16 04:48:34 PDT 2015, not used
	//private static SimpleDateFormat FORMAT_UTC = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");;

	//solr field names
	private static final String ID = "id";
	private static final String JOB_NUMBER = "jobNumber";
	private static final String JOB_TITLE = "jobTitle";
	private static final String JOB_TYPE = "jobType";
	private static final String LOCATION = "location";
	private static final String JOB_DESC = "jobDesc";
	private static final String RESPONSIBILITY = "responsibility";
	private static final String QUALIFICATION = "qualification";
	private static final String ACTIVESTATUS = "activeStatus";
	private static final String POSTDATE = "postDate";

	//this is fro debugging in this main() only, must be comment out after use
	//private static SolrClient solrClient;
	/*
	public static void main(String[] args)
	{
		//this is for debugging use only
		solrClient = new HttpSolrClient("http://localhost:8983/solr/myjob");
		SearchJobSolrDao ss = new SearchJobSolrDao();
		ss.serachJob("java", null);
		//ss.updateJobStatus(32, false);
	}
	*/
	
	public List<JobEntity> searchJob(String sQuery, String status)
	{
		logger.debug("==========query solr--- query=" + sQuery + "; status=" + status);
		//if user entered more key words (suppose they are separated by whitespace or ","
		//we narrow down the search, use 1st keyword as query q,set others as filter (AND condition)
		//1st split with whitespace
		String[] queryArray = sQuery.split("\\s");

		SolrQuery query = new SolrQuery();
		logger.debug("main query q=" + queryArray[0]);
		query.setQuery("text:" + queryArray[0]);
		if (queryArray.length > 1)
		{
			StringBuilder fq = new StringBuilder();
			//add filter for 2nd to end 
			for (int i = 1; i < queryArray.length; i++)
			{
				fq.append("text:");
				fq.append(queryArray[i]);
				if (i < (queryArray.length - 1))
				{
					fq.append(" AND ");
				}
			}
			logger.debug("final fq=" + fq.toString());
			query.setFilterQueries(fq.toString());
		}

		//add condition to search active or inactive or all job 
		if (CommonConstants.ACTIVE.equalsIgnoreCase(status))
		{
			query.addFilterQuery("activeStatus:" + true);
		}
		else if (CommonConstants.INACTIVE.equalsIgnoreCase(status))
		{
			query.addFilterQuery("activeStatus:" + false);
		}

		//return fields
		query.setFields(ID, JOB_TITLE, JOB_NUMBER, JOB_TYPE, LOCATION, POSTDATE, ACTIVESTATUS);
		//solr default return only 10 result
		query.setStart(0);
		query.setRows(100);

		//add sort, default to desc
		query.addSort("postDate", SolrQuery.ORDER.desc);

		//query.setStart(0);
		//query.set("defType", "edismax");
		List<JobEntity> list = new ArrayList<JobEntity>();
		QueryResponse response;
		try
		{
			//this works with old soleServer
			//response =server.query(query);

			response = SolrClientFactory.getJobSolrClient().query(query);
			
			SolrDocumentList results = response.getResults();
			//convert to json
			//Gson gson = new Gson();
			SolrDocument temp = null;
			//String json = null;
			for (int i = 0; i < results.size(); ++i)
			{
				temp = results.get(i);
				JobEntity jobEntity = new JobEntity();
				jobEntity.setJobId(Integer.valueOf((String) temp.getFieldValue(ID)));
				jobEntity.setJobTitle((String) temp.getFieldValue(JOB_TITLE));
				jobEntity.setJobType((String) temp.getFieldValue(JOB_TYPE));
				jobEntity.setLocation((String) temp.getFieldValue(LOCATION));
				//no need to convert solr date, use it directly
				jobEntity.setCreatedDate((Date) temp.getFieldValue(POSTDATE));
				jobEntity.setActiveStatus((Boolean) temp.getFieldValue(ACTIVESTATUS));
				//logger.debug("result id=" + jobEntity.getJobId() + "; title=" + jobEntity.getJobTitle()
				//		+ "; type=" + jobEntity.getJobType() + "; location="
				//		+ jobEntity.getLocation() + "; status=" + jobEntity.getActiveStatus());

				list.add(jobEntity);
			}
			
			//this is total number found, not just returned result size
			int totalNumberFound = (int)results.getNumFound();
			logger.debug("=====done query--size=" + list.size()+ "; totalNumberFound="+totalNumberFound);

		}
		catch (SolrServerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}


	/**
	 * Add or update job to solr, same command
	 * @param jobEntity
	 */
	public void addUpdateJob(JobEntity jobEntity)
	{
		logger.debug("=======solr dao: add/update jobentity:"+jobEntity);
        SolrInputDocument sdoc = getSolrInputDocument(jobEntity);
        try
		{
			SolrClientFactory.getJobSolrClient().add(sdoc);
			SolrClientFactory.getJobSolrClient().commit();
		}
		catch (SolrServerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        catch (IOException e)
        {
        	e.printStackTrace();
        }
               
	}

	private SolrInputDocument getSolrInputDocument(JobEntity jobEntity)
	{
		SolrInputDocument sdoc = new SolrInputDocument();
        sdoc.addField(ID, jobEntity.getJobId());
        sdoc.addField(JOB_NUMBER, jobEntity.getJobNumber());
        sdoc.addField(JOB_TITLE, jobEntity.getJobTitle());
        sdoc.addField(JOB_TYPE, jobEntity.getJobType());
        sdoc.addField(LOCATION, jobEntity.getLocation());
        sdoc.addField(JOB_DESC, jobEntity.getJobDesc());
        sdoc.addField(RESPONSIBILITY, jobEntity.getResponsibility());
        sdoc.addField(QUALIFICATION, jobEntity.getQualification());
        sdoc.addField(ACTIVESTATUS, jobEntity.getActiveStatus());
        sdoc.addField(POSTDATE, jobEntity.getCreatedDate());       
        
        return sdoc;
	}
	
	/**
	 * Delete single 
	 * @param jobId
	 */
	public void deleteJob(int jobId)
	{
		logger.debug("=========delete single job id="+jobId);
		List<Integer> list = new ArrayList<Integer>();
		list.add(jobId);
		deleteBatchJobs(list);
	}

	/**
	 * delete batch jobs
	 * @param jobList
	 */
	public void deleteBatchJobs(List<Integer> jobList)
	{
		//id must be string
		List<String> ids = new ArrayList<String>();
		for(Integer id: jobList)
		{
			ids.add(String.valueOf(id));
		}
		
		logger.debug("===========Solr dao: delete batch jobs: ="+jobList);
		try
		{
			SolrClientFactory.getJobSolrClient().deleteById(ids);
			SolrClientFactory.getJobSolrClient().commit();
		}
		catch (SolrServerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This is to update status only: pay attention to it, it's tricky!
	 * see some example http://svn.apache.org/viewvc/lucene/dev/trunk/solr/solrj/src/test/org/apache/solr/client/solrj/SolrExampleTests.java?view=markup
	 * 
	 * @param jobId
	 * @param status
	 */
	public void updateJobStatus(int jobId, boolean status)
	{
		logger.debug("==========Solr dao: update status, jobId="+jobId+"; status="+status);
		
		
		SolrInputDocument sdoc = new SolrInputDocument();
        sdoc.setField(ID, jobId); 
        //sdoc.addField("jobTitle", "lxslxs");;
        
        //for updated field, put value into this map, and use "set"!!!! otherwise it wiped out all other fields!!!
        HashMap<String, Object> oper = new HashMap<>();
        oper.put("set", status);
        //then add map to field
        sdoc.addField(ACTIVESTATUS, oper);
        //sdoc.setField(ACTIVESTATUS, status);
        
        //UpdateRequest req = new UpdateRequest();
        //req.setParam("overwrite", "false");
        //req.add(sdoc);
        try
		{
			//SolrClientFactory.getSolrClient().request(req);
        	SolrClientFactory.getJobSolrClient().add(sdoc);
			SolrClientFactory.getJobSolrClient().commit();
		}
		catch (SolrServerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	/**
	 * Convert solr date with UTC format: Thu Apr 16 04:48:34 PDT 2015 to Date: no need.
	 * Just leave it here
	 * @param solrDate
	 * @return
	 */
	/*
	private Date covertDateFromSolr(String solrDate)
	{
		Date date = null;
		try
		{
			date = FORMAT_UTC.parse(solrDate);
			logger.debug("original date="+solrDate+";   After convert: date="+ date);
			
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
	*/
}
