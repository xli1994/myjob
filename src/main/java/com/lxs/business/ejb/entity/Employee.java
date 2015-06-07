package com.lxs.business.ejb.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * to let eclipse discover Annotated entity, project-->property-->JPA-->Select Eclipselink2.5.2
 * -->check  "Discover annotated classes automatically" 
 * @author lxs
 *
 */
@Entity
@Table(name = "Employee")
public class Employee implements Serializable
{
	private static final long serialVersionUID = -723583058586873479L; 
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)//change to IDENTITY, since AUTO doesn't work for EclipseLink
	private int id;

	@Column(name = "FIRST_NAME")
	private String firstName;

	@Column(name = "LAST_NAME")
	private String lastName;

	@Column(name = "SALARY")
	private double salary;

	@Column(name = "DEPT")
	private String dept;

	@Transient
	private boolean canEdit;
	
	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
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

	public double getSalary()
	{
		return salary;
	}

	public void setSalary(double salary)
	{
		this.salary = salary;
	}

	public String getDept()
	{
		return dept;
	}

	public void setDept(String dept)
	{
		this.dept = dept;
	}

	@Override
	public String toString()
	{
		return "EMPLOYEE{id: " + this.id + " first name: " + this.firstName + " last name: "
				+ this.lastName + " salary: " + this.salary + " dept: " + this.dept + "}";
	}

	public boolean isCanEdit()
	{
		return canEdit;
	}

	public void setCanEdit(boolean canEdit)
	{
		this.canEdit = canEdit;
	}
}
