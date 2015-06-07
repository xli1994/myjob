package com.lxs.web.controllerbeans.job.support;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.StreamedContent;

import com.lxs.business.ejb.entity.ApplicationEntity;
import com.lxs.business.service.ManageJobService;
import com.lxs.web.controllerbeans.job.utils.ApplicationStatusEnum;
import com.lxs.web.controllerbeans.job.utils.CommonUtils;
import com.lxs.web.controllerbeans.job.utils.DownloadFileUtil;
import com.lxs.web.controllerbeans.job.utils.EmailUtil;
import com.lxs.web.controllerbeans.job.utils.EmailValidator;
import com.lxs.web.controllerbeans.job.utils.MessageProvider;

@ManagedBean(name = "viewProfileBean", eager = true)
@ViewScoped
public class ViewProfileBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(ViewProfileBean.class);

	@ManagedProperty(value = "#{manageJobService}")
	private ManageJobService mjs;

	private ApplicationEntity appEntity;

	//parameter values passed from applicationlist 
	private String jobTitle;
	private int jobId;
	private String jobNumber;
	private int appId;

	//parameter passed from searchapplicants.xhtml
	private String queryApp;
	
	//fields for Forward form:
	private String recipients;
	private String cc;
	private String subject;
	private String content;
	private String senderEmail;
	private boolean attachResume = true; //preset to true

	//resource bundle name
	private static String bundleName = "validatorMsg";
	private static String emailBundleName = "emailTemplate";

	//current login user, used for commentedBy when updating status and comments, hardcode for now
	private String currentViewer;
	
	@PostConstruct
	private void init()
	{
		logger.debug("View profile  init() called");

		//get applicant appId 
		String sAppId = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
				.getExternalContext(), "appId");
		if (!StringUtils.isEmpty(sAppId))
		{
			//this is for view applicant profile
			appId = Integer.valueOf(sAppId);

			jobId = Integer.valueOf(CommonUtils.getRequestParameter(FacesContext
					.getCurrentInstance().getExternalContext(), "jobId"));

			jobTitle = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
					.getExternalContext(), "jobTitle");
			jobNumber = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
					.getExternalContext(), "jobNumber");
			if (jobNumber == null)
			{
				jobNumber = "";
			}

			queryApp = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
					.getExternalContext(), "queryApp");
			
			appEntity = mjs.getAppilcantProfileByJobId(appId);

			logger.debug("init() jobId=" + jobId + "; appId=" + appId + "; jobTitle=" + jobTitle
					+ "jobNumber=" + jobNumber+"; queryApp="+queryApp);

			//set currentViewer should get it from login info, for now hardcode it
			currentViewer = "HR someone";
			
			//this is for forward only, preset some content for forward email:
			subject = MessageProvider.getValue("forwardSubject", emailBundleName);
			subject = MessageFormat.format(subject, this.jobTitle, this.getJobNumber());

			//here we can build a full profile url to add to forward content
			//it works, comment out for now
			/*
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().
					getExternalContext().getRequest();
			String requesturi = request.getRequestURI(); // this is /myjob/jobadmin/forward.jsf
			String queryString = request.getQueryString();
			logger.debug("requesturi="+requesturi+"; queryString="+queryString);
			int lastDashIndex = requesturi.lastIndexOf("/");
			int port =request.getLocalPort(); //8080
			StringBuilder sb = new StringBuilder();
			sb.append(request.getScheme());
			sb.append("://");
			sb.append(request.getServerName());
			if(port == 8080)
			{
				sb.append(":");
				sb.append(port);
			}
			sb.append(requesturi.substring(0, lastDashIndex+1));
			sb.append("applicantprofile.jsf?");
			sb.append(queryString);
			logger.debug("full profile path="+sb.toString());
			//String fullUrl = request.getScheme()+"://"+request.getServerName()+":"+port+requesturi.substring(0, lastDashIndex+1);
			*/
			content = MessageProvider.getValue("forwardContent", emailBundleName);
			content = MessageFormat.format(content, appEntity.getFirstName(),
					appEntity.getLastName(), appEntity.getEmail());

		}
	}
	
	public StreamedContent downloadResume()
	{
		return DownloadFileUtil.downloadResume(appEntity.getResumeName());
	}

	/**
	 * This only works for txt and pdf file, for MS wrod file, it just download, can't open in browser
	 */
	public void openFileToBrowser()
	{
		FacesContext faceContext = FacesContext.getCurrentInstance();
		DownloadFileUtil.openFileToBrowser(faceContext, appEntity.getResumeName());
	}

	/**
	 * This si to forward an applicant's profile to a colleague 
	 * @return
	 */
	public String forward()
	{
		logger.debug("forward called receipents=" + this.recipients + "; cc=" + this.cc
				+ "; senderEamil=" + senderEmail + "; subject=" + this.subject
				+ "; attachedresume=" + this.attachResume + "; content=" + this.content);
		if (!isInputValid())
		{
			logger.warn("Forward Input is not valid, send back!");
			return null;
		}

		String[] recipientEmails = recipients.split(",");
		String[] ccEmails = null;
		if (!StringUtils.isEmpty(cc))
		{
			ccEmails = cc.split(",");
		}

		String attachmentFileName = null;
		if (this.attachResume)
		{
			attachmentFileName = appEntity.getResumeName();
		}

		EmailUtil.sendEmail(senderEmail, recipientEmails, ccEmails, subject, content,
				attachmentFileName);

		logger.debug("forwarded applicant done");

		return "forwardcomplete?jobId=" + this.jobId + "&jobTitle=" + this.jobTitle + "&jobNumber="
				+ this.jobNumber + "&faces-redirect=true";
	}

	/**
	 * Validate forward input 
	 * @return
	 */
	private boolean isInputValid()
	{
		boolean isValid = true;

		EmailValidator validator = new EmailValidator();
		String[] recipientEmails = recipients.split(",");
		for (String email : recipientEmails)
		{
			logger.debug("start vailidate email=" + email);
			FacesMessage fmsg = validator.validate(email);

			if (fmsg != null)
			{
				logger.debug("validate failed: errormsg=" + fmsg.getDetail());
				FacesContext.getCurrentInstance().addMessage("forwardForm:recipients", fmsg);
				isValid = false;
			}
		}

		if (!StringUtils.isEmpty(cc))
		{
			String[] ccEmail = cc.split(",");
			for (String email : ccEmail)
			{
				logger.debug("start vailidate cc email=" + email);
				FacesMessage fmsg = validator.validate(email);

				if (fmsg != null)
				{
					logger.debug("validate cc failed: errormsg=" + fmsg.getDetail());
					FacesContext.getCurrentInstance().addMessage("forwardForm:cc", fmsg);
					isValid = false;
				}
			}
		}

		if (StringUtils.isEmpty(subject) || subject.trim().length() < 2)
		{
			String msg = MessageProvider.getValue("requiredMsg", bundleName);
			logger.debug("isInpuValid:: subject is not valid=" + subject);
			//pass parameter {0} to message 
			msg = MessageFormat.format(msg, "Subject");
			FacesMessage fmsg = new FacesMessage(msg);
			fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("forwardForm:subject", fmsg);
			isValid = false;
		}

		if (StringUtils.isEmpty(content) || content.trim().length() < 2)
		{
			String msg = MessageProvider.getValue("requiredMsg", bundleName);
			logger.debug("isInpuValid:: content is not valid=" + content);
			//pass parameter {0} to message 
			msg = MessageFormat.format(msg, "Content");
			FacesMessage fmsg = new FacesMessage(msg);
			fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage("forwardForm:content", fmsg);
			isValid = false;
		}

		return isValid;
	}

	/**
	 * Application status dropdown: not used, used below getStatusOptions
	 * @return
	 */
	public ApplicationStatusEnum[] getApplicationStatusEnums()
	{
		//logger.debug("getApplicationStatusEnums called"); 
		return ApplicationStatusEnum.values();
	}

	/**
	 * use ajax to save application status and comments by viewer
	 * @return
	 */
	public String updateStatusAndComments()
	{
		logger.debug("updateStatusAndComments called: status=" + appEntity.getStatus()
				+ "; comments=" + appEntity.getComments());
		//save to db:
		appEntity.setCommentedBy(currentViewer);
		appEntity.setCommentedDate(new Date());
		mjs.updateApplicationStatus(appEntity.getAppId(), appEntity.getStatus(), 
				appEntity.getComments(), appEntity.getCommentedBy(), appEntity.getCommentedDate());
		
		logger.debug("upadted status and comments to db!!!");
		return appEntity.getStatus()+"|"+appEntity.getComments()+"|";
	}

	/**            =========getter/setter ================ */
	public void setMjs(ManageJobService mjs)
	{
		this.mjs = mjs;
	}

	public ApplicationEntity getAppEntity()
	{
		return appEntity;
	}

	public void setAppEntity(ApplicationEntity appEntity)
	{
		this.appEntity = appEntity;
	}

	public String getJobTitle()
	{
		return jobTitle;
	}

	public void setJobTitle(String jobTitle)
	{
		this.jobTitle = jobTitle;
	}

	public int getJobId()
	{
		return jobId;
	}

	public void setJobId(int jobId)
	{
		this.jobId = jobId;
	}

	public String getJobNumber()
	{
		return jobNumber;
	}

	public void setJobNumber(String jobNumber)
	{
		this.jobNumber = jobNumber;
	}

	public String getRecipients()
	{
		return recipients;
	}

	public void setRecipients(String recipients)
	{
		this.recipients = recipients;
	}

	public String getCc()
	{
		return cc;
	}

	public void setCc(String cc)
	{
		this.cc = cc;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}

	public String getSenderEmail()
	{
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail)
	{
		this.senderEmail = senderEmail;
	}

	public boolean getAttachResume()
	{
		return attachResume;
	}

	public void setAttachResume(boolean attachResume)
	{
		this.attachResume = attachResume;
	}

	public String getCurrentViewer()
	{
		return currentViewer;
	}

	public void setCurrentViewer(String currentViewer)
	{
		this.currentViewer = currentViewer;
	}

	public String getQueryApp()
	{
		return queryApp;
	}

}