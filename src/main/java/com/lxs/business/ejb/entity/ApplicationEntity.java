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

@Entity
@Table(name = "jobApplications")
public class ApplicationEntity implements Serializable
{
	private static final long serialVersionUID = 1L;

	private int appId;
	private int appliedJobId; 		//NN, foreign key to jobId in joblist table
	private Date appliedDate;		//NN
	private String firstName; 		//len=30, NN
	private String lastName;  		//len=30, NN
	private String email;     		//len=100, NN
	private String address;   		//len=100, 
	private String city;			//len=50, 
	private String state;			//len=5, 
	private String country;			//len=50, 
	private String zipcode;			//len=10, 
	private String phone;			//len=20, 
	private String cellPhone;			//len=20,
	private String workStatus;       //len=30
	private String website;       //len=150
	private String resumeName;		//len=100, NN
	private String coverLetter;		//len=500
	private String gender;			//len =30
	private String race;			//len=60
	private String veteranStatus;	//len=30	
	private String status;          //len=20, not sure how to use it
	private String comments;         //len=300	
	private String commentedBy;       //len=30
	private Date commentedDate;       
	

	@Id
	@Column(name = "appId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public String getAddress()
	{
		return address;
	}

	public void setAddress(String address)
	{
		this.address = address;
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String city)
	{
		this.city = city;
	}

	public String getState()
	{
		return state;
	}

	public void setState(String state)
	{
		this.state = state;
	}

	public String getZipcode()
	{
		return zipcode;
	}

	public void setZipcode(String zipcode)
	{
		this.zipcode = zipcode;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public String getResumeName()
	{
		return resumeName;
	}

	public void setResumeName(String resumeName)
	{
		this.resumeName = resumeName;
	}



	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public String getRace()
	{
		return race;
	}

	public void setRace(String race)
	{
		this.race = race;
	}

	public String getVeteranStatus()
	{
		return veteranStatus;
	}

	public void setVeteranStatus(String veteranStatus)
	{
		this.veteranStatus = veteranStatus;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "appliedDate", nullable = false, updatable = false)
	public Date getAppliedDate()
	{
		return appliedDate;
	}

	public void setAppliedDate(Date appliedDate)
	{
		this.appliedDate = appliedDate;
	}

	public String getCountry()
	{
		return country;
	}

	public void setCountry(String country)
	{
		this.country = country;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}


	public String getComments()
	{
		return comments;
	}

	public void setComments(String comments)
	{
		this.comments = comments;
	}

	public String getCommentedBy()
	{
		return commentedBy;
	}

	public void setCommentedBy(String commentedBy)
	{
		this.commentedBy = commentedBy;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getCommentedDate()
	{
		return commentedDate;
	}

	public void setCommentedDate(Date commentedDate)
	{
		this.commentedDate = commentedDate;
	}

	public String getCellPhone()
	{
		return cellPhone;
	}

	public void setCellPhone(String cellPhone)
	{
		this.cellPhone = cellPhone;
	}

	public String getWorkStatus()
	{
		return workStatus;
	}

	public void setWorkStatus(String workStatus)
	{
		this.workStatus = workStatus;
	}

	public String getCoverLetter()
	{
		return coverLetter;
	}

	public void setCoverLetter(String coverLetter)
	{
		this.coverLetter = coverLetter;
	}

	public String getWebsite()
	{
		return website;
	}

	public void setWebsite(String website)
	{
		this.website = website;
	}



}
