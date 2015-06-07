package com.lxs.business.vo;

import java.util.ArrayList;

public class SearchResultList <T> extends ArrayList<T>
{
	//total result number = results.getNumFound(), not just returned result number
	private int totalResult;

	
	
	public int getTotalResult()
	{
		return totalResult;
	}

	public void setTotalResult(int totalResult)
	{
		this.totalResult = totalResult;
	}
}
