package com.lxs.business.dao;

import java.util.List;

import com.lxs.business.ejb.entity.ApplicationEntity;
import com.lxs.business.ejb.entity.JobEntity;
import com.lxs.business.ejb.entity.StateCode;

public interface ApplicantDao
{
	public List<JobEntity> getActiveJobList();

	public List<StateCode> getStateCodeList();
	
	public void saveApplication(ApplicationEntity appEntity);
	
}
