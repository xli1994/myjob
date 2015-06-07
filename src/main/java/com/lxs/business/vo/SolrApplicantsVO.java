package com.lxs.business.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * This is a vo to store solr search applicants results
 * @author lxs
 *
 */
public class SolrApplicantsVO implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int appId;
	private int appliedJobId; 		
	private Date appliedDate;		
	private String firstName; 		
	private String lastName;  		
	private String email;     		
	private String phone;			
	private String cellPhone;		
	private String resumeName;	
	private String status;
	private String appliedJobTitle;		
	private String appliedJobNumber;
	
	
	public int getAppId()
	{
		return appId;
	}
	public void setAppId(int appId)
	{
		this.appId = appId;
	}
	public int getAppliedJobId()
	{
		return appliedJobId;
	}
	public void setAppliedJobId(int appliedJobId)
	{
		this.appliedJobId = appliedJobId;
	}
	public Date getAppliedDate()
	{
		return appliedDate;
	}
	public void setAppliedDate(Date appliedDate)
	{
		this.appliedDate = appliedDate;
	}
	public String getFirstName()
	{
		return firstName;
	}
	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
	}
	public String getLastName()
	{
		return lastName;
	}
	public void setLastName(String lastName)
	{
		this.lastName = lastName;
	}
	public String getEmail()
	{
		return email;
	}
	public void setEmail(String email)
	{
		this.email = email;
	}
	public String getPhone()
	{
		return phone;
	}
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	public String getCellPhone()
	{
		return cellPhone;
	}
	public void setCellPhone(String cellPhone)
	{
		this.cellPhone = cellPhone;
	}
	public String getResumeName()
	{
		return resumeName;
	}
	public void setResumeName(String resumeName)
	{
		this.resumeName = resumeName;
	}
	public String getAppliedJobTitle()
	{
		return appliedJobTitle;
	}
	public void setAppliedJobTitle(String appliedJobTitle)
	{
		this.appliedJobTitle = appliedJobTitle;
	}
	public String getAppliedJobNumber()
	{
		return appliedJobNumber;
	}
	public void setAppliedJobNumber(String appliedJobNumber)
	{
		this.appliedJobNumber = appliedJobNumber;
	}

	public String getStatus()
	{
		return status;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	@Override
	public String toString()
	{
		return "SolrApplicantsVO [appId=" + appId + ", appliedJobId=" + appliedJobId
				+ ", appliedDate=" + appliedDate + ", firstName=" + firstName + ", lastName="
				+ lastName + ", email=" + email + ", phone=" + phone + ", cellPhone=" + cellPhone
				+ ", resumeName=" + resumeName + ", status=" + status + ", appliedJobTitle="
				+ appliedJobTitle + ", appliedJobNumber=" + appliedJobNumber + "]";
	}			


}
