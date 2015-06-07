package com.lxs.web.controllerbeans.job.utils;

public enum WorkStatusEnum
{
	NOT_SPECIFIED("Not Specified"),
	USCITIZEN ("US Citizen"),
	GC("Permanent Resident"),
	H1_VISA("H1 Visa"),
	TN_VISA("TN Visa"),
	F1_VISA("F1 Visa"),
	DECLINE("Decline to self identify");
	
	private String desc;
	
	private WorkStatusEnum(String desc)
	{
		this.desc=desc;
	}
	
	public String getDesc()
	{
		return desc;
	}
}
