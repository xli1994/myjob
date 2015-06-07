package com.lxs.web.controllerbeans.job.utils;

import java.util.Map;

import javax.faces.context.ExternalContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.JobEntity;

public class CommonUtils
{
	private static final Logger logger = LogManager.getLogger(CommonUtils.class);

	//line break, use "\n", or "\\r\\n|\\r|\\n", System.getProperty("line.separator") doesn't work
	private static String lineBreak = "\n";
	private static String replacement ="%%%";

	//private static String systemSeparator = System.getProperty("line.separator");
	
	//singleton
	private CommonUtils()
	{
	}

	/**
	 * get Http request parameter
	 * 
	 * @param context
	 * @param paramName
	 * @return
	 */
	public static String getRequestParameter(ExternalContext context, String paramName)
	{
		Map<String, String> params = context.getRequestParameterMap();
		String paramValue = params.get(paramName);
		return paramValue;
	}

	/**
	 * This is to replace all line breaks from html with "||" so that it can be handled in jsf to 
	 * display paragraph or <li> properly
	 * 
	 * see http://javarevisited.blogspot.com/2014/04/how-to-replace-line-breaks-new-lines-windows-mac-linux.html
	 * 
	 * @param jobEntity
	 * @return
	 */
	public static JobEntity convertLineBreakFromHtmlJobEntity(JobEntity jobEntity)
	{
		if (!StringUtils.isEmpty(jobEntity.getAboutCompany()))
		{
			logger.debug("aboutCom From HTML: before convert="+jobEntity.getAboutCompany());
			jobEntity.setAboutCompany(jobEntity.getAboutCompany().replaceAll(lineBreak, replacement));
			logger.debug("aboutCom From HTML: after convert="+jobEntity.getAboutCompany());
		}
		
		if (!StringUtils.isEmpty(jobEntity.getJobDesc()))
		{
			jobEntity.setJobDesc(jobEntity.getJobDesc().replaceAll(lineBreak, replacement));
		}

		if (!StringUtils.isEmpty(jobEntity.getResponsibility()))
		{
			jobEntity.setResponsibility(jobEntity.getResponsibility().replaceAll(lineBreak, replacement));
		}
		
		if (!StringUtils.isEmpty(jobEntity.getQualification()))
		{
			jobEntity.setQualification(jobEntity.getQualification().replaceAll(lineBreak, replacement));
		}
		
		if (!StringUtils.isEmpty(jobEntity.getPreferredSkill()))
		{
			jobEntity.setPreferredSkill(jobEntity.getPreferredSkill().replaceAll(lineBreak, replacement));
		}
		
		if (!StringUtils.isEmpty(jobEntity.getBenefit()))
		{
			jobEntity.setBenefit(jobEntity.getBenefit().replaceAll(lineBreak, replacement));
		}
		
		if (!StringUtils.isEmpty(jobEntity.getNote()))
		{
			jobEntity.setNote(jobEntity.getNote().replaceAll(lineBreak, replacement));
		}
		
		return jobEntity;
	}
	
	/**
	 * This is to revert linebreak from || to '\n', so that it will be displayed for Edit only
	 * @param jobEntity
	 * @return
	 */
	public static JobEntity revertLineBreakToHtmlJobEntity(JobEntity jobEntity)
	{
		if (!StringUtils.isEmpty(jobEntity.getAboutCompany()))
		{
			logger.debug("aboutCom: before convert="+jobEntity.getAboutCompany());
			jobEntity.setAboutCompany(jobEntity.getAboutCompany().replaceAll(replacement, lineBreak));
			logger.debug("aboutCom: after convert="+jobEntity.getAboutCompany());
		}
		
		if (!StringUtils.isEmpty(jobEntity.getJobDesc()))
		{
			jobEntity.setJobDesc(jobEntity.getJobDesc().replaceAll(replacement, lineBreak ));
		}

		if (!StringUtils.isEmpty(jobEntity.getResponsibility()))
		{
			jobEntity.setResponsibility(jobEntity.getResponsibility().replaceAll(replacement, lineBreak));
		}
		
		if (!StringUtils.isEmpty(jobEntity.getQualification()))
		{
			jobEntity.setQualification(jobEntity.getQualification().replaceAll(replacement, lineBreak));
		}
		
		if (!StringUtils.isEmpty(jobEntity.getPreferredSkill()))
		{
			jobEntity.setPreferredSkill(jobEntity.getPreferredSkill().replaceAll(replacement, lineBreak));
		}
		
		if (!StringUtils.isEmpty(jobEntity.getBenefit()))
		{
			jobEntity.setBenefit(jobEntity.getBenefit().replaceAll(replacement, lineBreak));
		}
		
		if (!StringUtils.isEmpty(jobEntity.getNote()))
		{
			jobEntity.setNote(jobEntity.getNote().replaceAll(replacement, lineBreak));
		}
		
		return jobEntity;
	}
	
	
	/**
	 * This is to replace all line breaks from html with "||" so that it can be handled in jsf to 
	 * display paragraph or <li> properly
	 * 
	 * @param String
	 * @return
	 */
	public static String convertLineBreakFromHtmlCoverLetter(String coverLetter)
	{
		if (!StringUtils.isEmpty(coverLetter))
		{
			coverLetter= coverLetter.replaceAll(lineBreak, replacement);
		}

		return coverLetter;
	}
	
}
