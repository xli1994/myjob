package com.lxs.web.controllerbeans.job.client;

import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.ApplicationEntity;
import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.business.ejb.entity.StateCode;
import com.lxs.business.service.ApplicantService;
import com.lxs.business.service.ManageJobService;
import com.lxs.web.controllerbeans.job.utils.AllDropdowns;
import com.lxs.web.controllerbeans.job.utils.ApplicationStatusEnum;
import com.lxs.web.controllerbeans.job.utils.CommonConstants;
import com.lxs.web.controllerbeans.job.utils.CommonUtils;
import com.lxs.web.controllerbeans.job.utils.ConfigUtil;
import com.lxs.web.controllerbeans.job.utils.EmailUtil;
import com.lxs.web.controllerbeans.job.utils.GenderEnum;
import com.lxs.web.controllerbeans.job.utils.MessageProvider;
import com.lxs.web.controllerbeans.job.utils.RaceEnum;
import com.lxs.web.controllerbeans.job.utils.VeteranStatusEnum;
import com.lxs.web.controllerbeans.job.utils.WorkStatusEnum;

/**
 * This is the bean to process job submission
 * @author lxs
 *
 */
@ManagedBean(name = "applyFormBean", eager = true)
@ViewScoped
public class ApplyFormBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ApplyFormBean.class);

	@ManagedProperty(value = "#{applicantService}")
	private ApplicantService aps;

	@ManagedProperty(value = "#{manageJobService}")
	private ManageJobService mjs;

	private JobEntity jobEntity;

	private ApplicationEntity appEntity = new ApplicationEntity();

	/*
	//workstatus dropdown
	private WorkStatusEnum workStatusEnum;
	//gender dropdwon
	private GenderEnum genderEnum;
	//race drodpdown:
	private RaceEnum raceEnum;
	//vatern status dropdown
	private VeteranStatusEnum veteranStatusEnum;
	*/
	//resource bundle name
	private static String bundleName = "validatorMsg";

	//upload file resume
	private Part resume;

	private static String emailBundleName= "emailTemplate";
	
	//dateformat for save file name
	private static SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyyMMdd'_'hhmmss");

	public ApplyFormBean()
	{
		logger.debug("ApplyFormBean constructor called");
	}

	@PostConstruct
	private void init()
	{
		//get jobEntity from param
		String jobId = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
				.getExternalContext(), "jobId");
		//jobId from form hidden field, double check, because somwhow this bean is initialized again 
		//when submit with error
		//String jobIdHidden = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance().getExternalContext(), "applicationForm:jobId");

		logger.debug("ApplyFormBean init() called jobId=" + jobId);
		if (jobEntity != null)
		{
			logger.debug("jobEntity not null, jobId=" + jobEntity.getJobId());
		}

		boolean isBadRequest = false;
		if (StringUtils.isEmpty(jobId))
		{
			logger.warn("Bad url request for apply, jobId = null, redirect to error page!!");
			isBadRequest = true;
		}
		else
		{
			logger.debug("load active job only");
			jobEntity = mjs.getJobByIdAndStatus(Integer.valueOf(jobId), true);
		}

		if (isBadRequest || jobEntity == null)
		{
			logger.warn("jobEntity is null or isBadRequest=" + isBadRequest
					+ "; redirect to error page!!!");
			try
			{
				FacesContext.getCurrentInstance().getExternalContext()
						.redirect(CommonConstants.JOB_NOT_FOUND_PAGE);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
	}

	/**
	 * Submit application and upload file
	 * @return
	 */
	public String submit()
	{
		//get jobid from hidden field, not necessary
		//String jobId = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance().getExternalContext(), "applicationForm:jobId");
		logger.debug("Submit called, jobId =" + jobEntity.getJobId() + "; firstName="
				+ appEntity.getFirstName() + "; last name=" + appEntity.getLastName() + "; email="
				+ appEntity.getEmail());

		if (!isInputValid())
		{
			logger.warn("Input is not valid, but passed front end validation!!");
			return null;
		}

		//save resume to repo
		String savedFileName = saveResume();
		if(savedFileName == null)
		{
			logger.warn("Some thing wrong when saving file to desk, Redirect user to error page");
			final FacesContext facesContext = FacesContext.getCurrentInstance();
			final ExternalContext externalContext = facesContext.getExternalContext();
			final Map<String, Object> requestMap = externalContext.getRequestMap();
			requestMap.put("errorMsg", "An error happened when uploading file");
		
			try
			{
				externalContext.redirect(CommonConstants.ERROR_PAGE);
				return null;
			}
			catch (IOException e1)
			{
				throw new RuntimeException(e1);
			}
		}

		//save application to db:
		appEntity.setAppliedJobId(jobEntity.getJobId());
		appEntity.setResumeName(savedFileName);
		appEntity.setAppliedDate(new Date());
		appEntity.setStatus(ApplicationStatusEnum.NEW.getDesc());
		if(!StringUtils.isEmpty(appEntity.getCoverLetter()))
		{
			this.processCoverLetter();
		}
		
		//save to both db and solr
		aps.saveApplication(appEntity, jobEntity.getJobTitle(), jobEntity.getJobNumber());

		//send email to HR
		sendEamilToHR();
		
		//send confirmation email to applicant
		sendEmailToApplicant();

		logger.debug("Successfully saved application");
		return "submitsuccess?faces-redirect=true";
	}

	/**
	 * Save uploaded resume to repository
	 * @return
	 */
	private String saveResume()
	{
		String originalFileName = resume.getSubmittedFileName();
		int lastDotIndex = originalFileName.lastIndexOf(".");
		String extension = originalFileName.substring(lastDotIndex);
		String savedFileName = jobEntity.getJobId() + "_" + appEntity.getLastName().trim() + "_"
				+ DATEFORMAT.format(new Date()) + extension;
		logger.debug("**fileName: " + originalFileName + "; Saved file name=" + savedFileName);

		//write file:
		try
		{
			resume.write(ConfigUtil.getUploadFileDir() + savedFileName);
			return savedFileName;
		}
		catch (IOException e)
		{
			logger.error("Error when saving upload resume");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Send email and resume to HR
	 */
	private void sendEamilToHR()
	{
		if (!StringUtils.isEmpty(jobEntity.getEmailTo()))
		{
			logger.debug("sending email to HR....");
			String[] emailTo = jobEntity.getEmailTo().split(",");

			String senderEmail = "xiaosongli2@comcast.net";
			
			String subject = MessageProvider.getValue("emailSubject", emailBundleName);
			subject = MessageFormat.format(subject, jobEntity.getJobTitle(), jobEntity.getJobNumber());

			String content = MessageProvider.getValue("emailContent", emailBundleName);
			content = MessageFormat.format(content, appEntity.getFirstName(), appEntity.getLastName(), appEntity.getEmail());		
			
			EmailUtil.sendEmail(senderEmail, emailTo, null, subject, content,
					appEntity.getResumeName());
			
			logger.debug("sent email to HR");
		}
	}

	/**
	 * Send confirmation to applicant
	 */
	private void sendEmailToApplicant()
	{
		logger.debug("sending confirmation email to applicant....");
		String senderEamil = "xiaosongli2@comcast.net";
		
		String subject = MessageProvider.getValue("appSubject", emailBundleName);
		subject = MessageFormat.format(subject, jobEntity.getJobTitle());

		String content = MessageProvider.getValue("appContent", emailBundleName);
		content = MessageFormat.format(content, appEntity.getFirstName()+" "+ appEntity.getLastName(), appEntity.getEmail());		
		
		EmailUtil.sendEmail(senderEamil, appEntity.getEmail(), subject, content);
		logger.debug("sent confirmation email successful");
	}
	
	
	private boolean isInputValid()
	{
		boolean isValid = true;
		if (StringUtils.isEmpty(appEntity.getFirstName())
				|| appEntity.getFirstName().trim().length() < 2
				|| appEntity.getFirstName().trim().length() > 100)
		{
			String msg = MessageProvider.getValue("requiredMsg", bundleName);
			logger.debug("isInpuValid:: first name is not valid=" + appEntity.getFirstName().trim());
			//pass parameter {0} to message 
			msg = MessageFormat.format(msg, "First Name");
			FacesMessage fmsg = new FacesMessage(msg);
			fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);

			//add to message id field: "formname:fieldid".
			//Note#, this is input field id, not message field id
			FacesContext.getCurrentInstance().addMessage("applicationForm:firstName", fmsg);
			isValid = false;
		}

		if (StringUtils.isEmpty(appEntity.getLastName())
				|| appEntity.getLastName().trim().length() < 2
				|| appEntity.getLastName().trim().length() > 100)
		{
			String msg = MessageProvider.getValue("requiredMsg", bundleName);
			logger.debug("isInpuValid:: last name is not valid=" + appEntity.getFirstName().trim());
			//pass parameter {0} to message 
			msg = MessageFormat.format(msg, "Last Name");
			FacesMessage fmsg = new FacesMessage(msg);
			fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);

			//add to message id field: "formname:fieldid".
			//Note#, this is input field id, not message field id
			FacesContext.getCurrentInstance().addMessage("applicationForm:lastName", fmsg);
			isValid = false;
		}

		return isValid;
	}

	/**
	 * This is to add delimiter @@@ for linebreak \n in cover letter
	 */
	private void processCoverLetter()
	{
		String converted = CommonUtils.convertLineBreakFromHtmlCoverLetter(appEntity.getCoverLetter());
		appEntity.setCoverLetter(converted);
	}
	
	/**
	 * State dropdown
	 * @return
	 */
	public List<StateCode> getStateCodeList()
	{
		return AllDropdowns.getStateCodeList();
		//return stateCodeList;
	}

	/**
	 * Work status DD
	 * @return
	 */
	public WorkStatusEnum[] getWorkStatusEnums()
	{
		return WorkStatusEnum.values();
	}

	/**
	 * Gender status DD
	 * @return
	 */
	public GenderEnum[] getGenderEnums()
	{
		return GenderEnum.values();
	}

	/**
	 * Race DD
	 * @return
	 */
	public RaceEnum[] getRaceEnums()
	{
		return RaceEnum.values();
	}

	/**
	 * VeteranStatus DD
	 * @return
	 */
	public VeteranStatusEnum[] getVeteranStatusEnums()
	{
		return VeteranStatusEnum.values();
	}

	public void setAps(ApplicantService aps)
	{
		this.aps = aps;
	}

	public void setMjs(ManageJobService mjs)
	{
		this.mjs = mjs;
	}

	public JobEntity getJobEntity()
	{
		return jobEntity;
	}

	public ApplicationEntity getAppEntity()
	{
		return appEntity;
	}

	public void setAppEntity(ApplicationEntity appEntity)
	{
		this.appEntity = appEntity;
	}

	/*
	public WorkStatusEnum getWorkStatusEnum()
	{
		return workStatusEnum;
	}

	public void setWorkStatusEnum(WorkStatusEnum workStatusEnum)
	{
		this.workStatusEnum = workStatusEnum;
	}

	public GenderEnum getGenderEnum()
	{
		return genderEnum;
	}

	public void setGenderEnum(GenderEnum genderEnum)
	{
		this.genderEnum = genderEnum;
	}

	public RaceEnum getRaceEnum()
	{
		return raceEnum;
	}

	public void setRaceEnum(RaceEnum raceEnum)
	{
		this.raceEnum = raceEnum;
	}

	public VeteranStatusEnum getVeteranStatusEnum()
	{
		return veteranStatusEnum;
	}

	public void setVeteranStatusEnum(VeteranStatusEnum veteranStatusEnum)
	{
		this.veteranStatusEnum = veteranStatusEnum;
	}
	*/
	public Part getResume()
	{
		return resume;
	}

	public void setResume(Part resume)
	{
		this.resume = resume;
	}

}
