package com.lxs.web.controllerbeans.job.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.StateCode;
import com.lxs.business.service.ApplicantService;

public class AllDropdowns
{
	private static final Logger logger = LogManager.getLogger(AllDropdowns.class);
	//job type dropdowns
	private static Map<String, String> jobTypeMap = new LinkedHashMap<String, String>();
	
	//private static ApplicantService aps = new ApplicantService();
	
	//state dropdown:
	private static List<StateCode> stateCodelist;
	
	static
	{
		logger.debug("Satrt loading dropdwons....");
		jobTypeMap.put(JobTypeEnum.FULLTIME.getDesc(), JobTypeEnum.FULLTIME.getDesc()); //label, value
		jobTypeMap.put(JobTypeEnum.CONTRACT.getDesc(), JobTypeEnum.CONTRACT.getDesc());
		jobTypeMap.put(JobTypeEnum.CONTRACT_HIRE.getDesc(), JobTypeEnum.CONTRACT_HIRE.getDesc());
		jobTypeMap.put(JobTypeEnum.PARTTIME.getDesc(), JobTypeEnum.PARTTIME.getDesc());
		jobTypeMap.put(JobTypeEnum.INTERN.getDesc(), JobTypeEnum.INTERN.getDesc());
	}
	
	//no instance needed
	private AllDropdowns()
	{
		
	}
	
	public static Map getJobTypeMap()
	{
		return jobTypeMap;
	}
	
	public static List<StateCode> getStateCodeList()
	{
		if( stateCodelist == null)
		{
			//ExternalContext exctxt = FacesContext.getCurrentInstance().getExternalContext();
			//ApplicantService aps = (ApplicantService) exctxt.getApplicationMap().get("applicantService");
			ApplicantService aps = new ApplicantService ();
			stateCodelist = aps.getStateCodeList();
		}
		return  stateCodelist;
	}
	
}
