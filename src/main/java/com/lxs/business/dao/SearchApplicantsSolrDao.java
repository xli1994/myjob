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
import org.apache.solr.common.StringUtils;

import com.lxs.business.vo.SearchResultList;
import com.lxs.business.vo.SolrApplicantsVO;
import com.lxs.web.controllerbeans.job.utils.CommonConstants;

@ManagedBean(name = "searchApplicantsSolrDao", eager = true)
@ViewScoped
public class SearchApplicantsSolrDao implements Serializable
{
	private static final long serialVersionUID = -1;
	private static final Logger logger = LogManager.getLogger(SearchApplicantsSolrDao.class);

	//solr returned date with UTC format: Thu Apr 16 04:48:34 PDT 2015, not used
	//private static SimpleDateFormat FORMAT_UTC = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");;

	//solr field names
	private static final String ID = "id"; //appId
	private static final String APPLIED_JOBID = "appliedJobId";
	private static final String APPLIED_DATE = "appliedDate";
	private static final String FIRSTNAME = "firstName";
	private static final String LASTNAME = "lastName";
	private static final String EMAIL = "email";
	private static final String PHONE = "phone";
	private static final String CELLPHONE = "cellPhone";
	private static final String RESUME_NAME = "resumeName";
	private static final String STATUS = "status";
	private static final String APPLIED_JOBTITLE = "appliedJobTitle";
	private static final String APPLIED_JOBNUMBER = "appliedJobNumber";

	//this is fro debugging in this main() only, must be comment out after use
	/*
	private static SolrClient solrClient;
	
	public static void main(String[] args)
	{
		//this is for debugging use only
		solrClient = new HttpSolrClient("http://localhost:8983/solr/applicants");
		SearchApplicantsSolrDao ss = new SearchApplicantsSolrDao();
		ss.searchApplicants("mark", 0, 5, null, null);

	}
	*/

	public SearchResultList<SolrApplicantsVO> searchApplicants(String sQuery, int pageIndex,
			int pageSize, String orderBy, String orderDir)
	{
		logger.debug("==========query solr searchApplicants--- query=" + sQuery+"; pageIndex="+pageIndex+
				"; pageSize="+pageSize+"; orderBy="+orderBy+"; orderDir="+orderDir);
		
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

		//return fields
		query.setFields(ID, APPLIED_JOBID, APPLIED_DATE, FIRSTNAME, LASTNAME, EMAIL, RESUME_NAME,
				STATUS, APPLIED_JOBTITLE, APPLIED_JOBNUMBER);
		
		//solr default return only 10 result
		query.setStart(pageIndex*pageSize);
		query.setRows(pageSize);

		//add sort, default to desc
		if(!StringUtils.isEmpty(orderBy))
		{
			logger.debug("---order by set...");
			if("asc".equalsIgnoreCase(orderDir))
			{
				query.addSort(orderBy, SolrQuery.ORDER.asc);
			}
			else
			{
				query.addSort(orderBy, SolrQuery.ORDER.desc);
			}
		}
		

		SearchResultList<SolrApplicantsVO> list = new SearchResultList<SolrApplicantsVO>();

		QueryResponse response;
		try
		{
			response = SolrClientFactory.getApplicantsSolrClient().query(query);
			//response = solrClient.query(query);
			SolrDocumentList results = response.getResults();

			SolrDocument temp = null;

			for (int i = 0; i < results.size(); ++i)
			{
				temp = results.get(i);
				SolrApplicantsVO vo = new SolrApplicantsVO();

				vo.setAppId(Integer.valueOf((String) temp.getFieldValue(ID)));
				vo.setAppliedJobId(Integer.valueOf((String) temp.getFieldValue(APPLIED_JOBID)));
				vo.setAppliedDate((Date) temp.getFieldValue(APPLIED_DATE));
				vo.setFirstName((String) temp.getFieldValue(FIRSTNAME));
				vo.setLastName((String) temp.getFieldValue(LASTNAME));
				vo.setEmail((String) temp.getFieldValue(EMAIL));
				vo.setResumeName((String) temp.getFieldValue(RESUME_NAME));
				vo.setStatus((String) temp.getFieldValue(STATUS));
				vo.setAppliedJobTitle((String) temp.getFieldValue(APPLIED_JOBTITLE));
				vo.setAppliedJobNumber((String) temp.getFieldValue(APPLIED_JOBNUMBER));

				//logger.debug("applicants result vo=" + vo);

				list.add(vo);
			}

			//this is total number found, not just returned result size
			int totalNumberFound = (int) results.getNumFound();
			list.setTotalResult(totalNumberFound);
			logger.debug("=====done query--size=" + list.size() + "; totalNumberFound="
					+ totalNumberFound);

		}
		catch (SolrServerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * Add or update applicant to solr, same command for add/update
	 * @param 
	 */
	public void addUpdateApplicant(SolrApplicantsVO vo)
	{
		logger.debug("=======solr dao: add/update applicants..");
		SolrInputDocument sdoc = getSolrInputDocument(vo);
		try
		{
			SolrClientFactory.getApplicantsSolrClient().add(sdoc);
			SolrClientFactory.getApplicantsSolrClient().commit();
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

	private SolrInputDocument getSolrInputDocument(SolrApplicantsVO vo)
	{
		SolrInputDocument sdoc = new SolrInputDocument();
		sdoc.addField(ID, vo.getAppId());
		sdoc.addField(APPLIED_JOBID, vo.getAppliedJobId());
		sdoc.addField(APPLIED_DATE, vo.getAppliedDate());
		sdoc.addField(FIRSTNAME, vo.getFirstName());
		sdoc.addField(LASTNAME, vo.getLastName());
		sdoc.addField(EMAIL, vo.getEmail());
		sdoc.addField(PHONE, vo.getPhone());				
		sdoc.addField(CELLPHONE, vo.getCellPhone());		
		sdoc.addField(RESUME_NAME, vo.getResumeName());
		sdoc.addField(STATUS, vo.getStatus());
		sdoc.addField(APPLIED_JOBTITLE, vo.getAppliedJobTitle());
		sdoc.addField(APPLIED_JOBNUMBER, vo.getAppliedJobNumber());

		return sdoc;
	}

	public void updateApplicationStatus(int appId, String status)
	{
		logger.debug("==========Solr dao: updateApplicationStatus, appId="+appId+"; status="+status);
		
		
		SolrInputDocument sdoc = new SolrInputDocument();
        sdoc.setField(ID, appId); 

        //for updated field, put value into this map, and use "set"!!!! otherwise it wiped out all other fields!!!
        HashMap<String, Object> oper = new HashMap<>();
        oper.put("set", status);
        //then add map to field
        sdoc.addField(STATUS, oper);

        try
		{
        	SolrClientFactory.getApplicantsSolrClient().add(sdoc);
        	SolrClientFactory.getApplicantsSolrClient().commit();
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
}
