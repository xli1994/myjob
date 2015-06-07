package com.lxs.business.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import com.lxs.web.controllerbeans.job.utils.ConfigUtil;

public class SolrClientFactory
{
	private static final Logger logger = LogManager.getLogger(SolrClientFactory.class);
	
	private static SolrClient solrClientJob; //solr job core
	private static SolrClient solrClientApplicants; //solr applicants core
	
	static 
	{
		solrClientJob = new HttpSolrClient(ConfigUtil.getSolrServerUrl()+ConfigUtil.getSolrCoreNameJob());
		solrClientApplicants = new HttpSolrClient(ConfigUtil.getSolrServerUrl()+ConfigUtil.getSolrCoreNameApplicants());
		
		//set password

	}
	
	public static SolrClient getJobSolrClient()
	{
		return solrClientJob;
	}
	
	public static SolrClient getApplicantsSolrClient()
	{
		return solrClientApplicants;
	}
	
	public static void shutdown()
	{
		solrClientJob.shutdown();
		solrClientApplicants.shutdown();
	}
}
