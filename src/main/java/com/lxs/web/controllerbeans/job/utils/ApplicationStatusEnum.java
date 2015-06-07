package com.lxs.web.controllerbeans.job.utils;

public enum ApplicationStatusEnum
{
	NEW("New"), REVIEWED("Reviewed"), 
	REPLIED("Replied"), ACCEPTED("Accepted"), REJECTED("Not Selected");
	
	private String desc;
	
	private ApplicationStatusEnum(String desc)
	{
		this.desc = desc;
	}
	
	public String getDesc()
	{
		return desc;
	}
	
}
