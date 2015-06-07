package com.lxs.web.controllerbeans.job.utils;

public enum JobTypeEnum
{
	FULLTIME("Full Time"), CONTRACT("Contract"), CONTRACT_HIRE("Contract-To-Hire"), 
	PARTTIME("Part Time"), INTERN("Intern");
	
	private String desc;
	
	private JobTypeEnum(String desc)
	{
		this.desc = desc;
	}
	
	public String getDesc()
	{
		return desc;
	}
}
