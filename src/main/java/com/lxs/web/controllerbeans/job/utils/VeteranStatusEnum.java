package com.lxs.web.controllerbeans.job.utils;

public enum VeteranStatusEnum
{
	SPECIAL("Special Disabled Veteran"),
	VIETNAM("Vietnam Era Veteran"),	
	SEPARATED("Newly Separated Veteran"),
	OTHER("Other Protected Veteran"),
	NOT("Not a Veteran"),
	DECLINE("Decline to Self Identify");
	
	private String desc;
	
	private VeteranStatusEnum(String desc)
	{
		this.desc=desc;
	}
	
	public String getDesc()
	{
		return desc;
	}
}
