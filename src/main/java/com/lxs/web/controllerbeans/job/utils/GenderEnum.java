package com.lxs.web.controllerbeans.job.utils;

public enum GenderEnum
{
	MALE("Male"), FEMALE("Female"),DECLINE("Decline to self identify");
	
	private String desc;
	
	private GenderEnum(String desc)
	{
		this.desc=desc;
	}
	
	public String getDesc()
	{
		return desc;
	}
}
