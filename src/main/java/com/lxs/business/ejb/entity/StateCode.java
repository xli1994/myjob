package com.lxs.business.ejb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

@NamedNativeQuery(
		 name="queryState", 
		 query = "SELECT * FROM state_lk s order by "
				+ "if(s.stateCode in ('AS', 'MP', 'PR', 'VI', 'GU'),1,0), s.stateCode", 
		 resultClass = StateCode.class)

@Entity
@Table(name = "state_lk")
public class StateCode implements Serializable
{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "stateCode", insertable=false, updatable = false)
	private String stateCode;
	private String stateName;
	
	public String getStateCode()
	{
		return stateCode;
	}
	public void setStateCode(String stateCode)
	{
		this.stateCode = stateCode;
	}
	public String getStateName()
	{
		return stateName;
	}
	public void setStateName(String stateName)
	{
		this.stateName = stateName;
	}
}
