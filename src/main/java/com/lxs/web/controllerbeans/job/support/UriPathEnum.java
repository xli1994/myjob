package com.lxs.web.controllerbeans.job.support;

/**
 * This is enum with request uri for identifying if a request is from client or from internal admin
 * @author lxs
 *
 */
public enum UriPathEnum
{
	ADMIN("/jobadmin/"), CLIENT("/joblist/");
	
	private String desc;
	
	private UriPathEnum(String desc)
	{
		this.desc = desc;
	}
	
	public String getDesc()
	{
		return desc;
	}
}
