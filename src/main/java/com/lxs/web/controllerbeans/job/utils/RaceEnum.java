package com.lxs.web.controllerbeans.job.utils;

public enum RaceEnum
{
	AMERICAN_INDIAN("American Indian or Alaskan Native"),
	ASIAN("Asian"),
	BLACK("Black or African American"),
	HISPANIC("Hispanic or Latino"),
	HAWAIIAN("Native Hawaiian or other Pacific Islander"),
	WHITE("White or Caucasian"),
	TWO("Two or more races"),
	DECLINE("Decline to self identify");
	
	private String desc;
	
	private RaceEnum(String desc)
	{
		this.desc=desc;
	}
	
	public String getDesc()
	{
		return desc;
	}
}
