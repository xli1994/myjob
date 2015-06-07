package com.lxs.business.ejb.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import javax.persistence.SqlResultSetMapping;
import javax.persistence.ConstructorResult;
import javax.persistence.ColumnResult;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;

/* this not works
@SqlResultSetMapping(name = "jobEntityMapping", 
	entities = 
		{@EntityResult(entityClass = JobEntity.class, 
			fields = {@FieldResult(name = "jobId", column = "jobId"),
			      	  @FieldResult(name = "jobTitle", column = "jobTitle"),
			      	  @FieldResult(name = "jobNumber", column = "jobNumber"),
			      	@FieldResult(name = "activeStatus", column = "activeStatus"),
			      	@FieldResult(name = "createdDate", column = "createdDate"),
			      	@FieldResult(name = "applicantNumber", column = "applicantNumber"),
					})
		})
*/
/* this works */
@SqlResultSetMapping(name = "jobEntityMapping", classes = { @ConstructorResult(targetClass = com.lxs.business.ejb.entity.JobEntity.class, columns = {
		@ColumnResult(name = "jobId", type = Integer.class),
		@ColumnResult(name = "jobTitle", type = String.class),
		@ColumnResult(name = "jobNumber", type = String.class),
		@ColumnResult(name = "activeStatus", type = Boolean.class),
		@ColumnResult(name = "createdDate", type = java.util.Date.class),
		@ColumnResult(name = "applicantNumber", type = Integer.class) }) })
@Entity
@Table(name = "joblist")
public class JobEntity implements Serializable, Cloneable
{
	private static final long serialVersionUID = 1L;

	private int jobId;
	private String jobNumber; //length 30
	private String jobTitle; //length 70 NN
	private String emailTo; //Length: 400, 
	private String jobCategory; //length 70
	private String jobType; //length 45,NN, full time, contract, contract-to-hire
	private String contractDuration; //length 80, unit month,or long trem...
	private String location; //length 100, NN
	private String aboutCompany; //length 800
	private String jobDesc; //length 1500, NN
	private String responsibility; //length 1500
	private String qualification; //length 1500 NN
	private String education; //length 150
	private String preferredSkill; //length 1000
	private String benefit; //length 800
	private String note; //length 500
	private boolean activeStatus; //NN, active or disabled

	private Date createdDate; //NN, 
	private String createdBy; //length 45, NN
	private Date updatedDate; //,  
	private String updatedBy; //length 45

	//this is applicant number from joined table jobapplications, transit, used in mapping
	private int applicantNumber;

	/*===========================constructor===================================*/
	public JobEntity()
	{
	}

	public JobEntity(int jobId, String jobTitle, String jobNumber, boolean activeStatus,
			Date createdDate, int applicantNumber)
	{
		this.jobId = jobId;
		this.jobTitle = jobTitle;
		this.jobNumber = jobNumber;
		this.activeStatus = activeStatus;
		this.createdDate = createdDate;
		this.applicantNumber = applicantNumber;
	}

	/*--------------------mapping ----------------------*/
	@Id
	@Column(name = "jobId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@Column(name = "jobTitle", nullable = false)
	public String getJobTitle()
	{
		return jobTitle;
	}

	public void setJobTitle(String jobTitle)
	{
		this.jobTitle = jobTitle;
	}

	public String getJobCategory()
	{
		return jobCategory;
	}

	public void setJobCategory(String jobCategory)
	{
		this.jobCategory = jobCategory;
	}

	public String getJobType()
	{
		return jobType;
	}

	public void setJobType(String jobType)
	{
		this.jobType = jobType;
	}

	public String getContractDuration()
	{
		return contractDuration;
	}

	public void setContractDuration(String contractDuration)
	{
		this.contractDuration = contractDuration;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getAboutCompany()
	{
		return aboutCompany;
	}

	public void setAboutCompany(String aboutCompany)
	{
		this.aboutCompany = aboutCompany;
	}

	public String getJobDesc()
	{
		return jobDesc;
	}

	public void setJobDesc(String jobDesc)
	{
		this.jobDesc = jobDesc;
	}

	public String getResponsibility()
	{
		return responsibility;
	}

	public void setResponsibility(String responsibility)
	{
		this.responsibility = responsibility;
	}

	public String getQualification()
	{
		return qualification;
	}

	public void setQualification(String qualification)
	{
		this.qualification = qualification;
	}

	public String getEducation()
	{
		return education;
	}

	public void setEducation(String education)
	{
		this.education = education;
	}

	public String getPreferredSkill()
	{
		return preferredSkill;
	}

	public void setPreferredSkill(String preferredSkill)
	{
		this.preferredSkill = preferredSkill;
	}

	public String getBenefit()
	{
		return benefit;
	}

	public void setBenefit(String benefit)
	{
		this.benefit = benefit;
	}

	public String getNote()
	{
		return note;
	}

	public void setNote(String note)
	{
		this.note = note;
	}

	public boolean getActiveStatus()
	{
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus)
	{
		this.activeStatus = activeStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createdDate", nullable = false, updatable = false)
	public Date getCreatedDate()
	{
		return createdDate;
	}

	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

	@Column(name = "createdBy", nullable = false, updatable = false)
	public String getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updatedDate")
	public Date getUpdatedDate()
	{
		return updatedDate;
	}

	public void setUpdatedDate(Date updatedDate)
	{
		this.updatedDate = updatedDate;
	}

	public String getUpdatedBy()
	{
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}

	@Override
	public String toString()
	{
		return "JobEntity [jobId=" + jobId + ", jobNumber=" + jobNumber + ", jobTitle=" + jobTitle
				+ ", jobCategory=" + jobCategory + ", jobType=" + jobType + ", contractDuration="
				+ contractDuration + ", location=" + location + ", aboutCompany=" + aboutCompany
				+ ", jobDesc=" + jobDesc + ", responsibility=" + responsibility
				+ ", qualification=" + qualification + ", education=" + education
				+ ", preferredSkill=" + preferredSkill + ", benefit=" + benefit + ", note=" + note
				+ ", activeStatus=" + activeStatus + ", createdDate=" + createdDate
				+ ", createdBy=" + createdBy + ", updatedDate=" + updatedDate + ", updatedBy="
				+ updatedBy + "]";
	}

	public String getEmailTo()
	{
		return emailTo;
	}

	public void setEmailTo(String emailTo)
	{
		this.emailTo = emailTo;
	}

	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Transient
	public int getApplicantNumber()
	{
		return applicantNumber;
	}

	public void setApplicantNumber(int applicantNumber)
	{
		this.applicantNumber = applicantNumber;
	}
}
