package com.lxs.web.controllerbeans.job.support;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.lxs.business.ejb.entity.ApplicationEntity;
import com.lxs.business.service.ManageJobService;


/**
 * This is a primefaces datamodel for display application with pagination 
 * @author lxs
 *
 */
public class LazyApplicationDataModel extends LazyDataModel<ApplicationEntity>
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LazyApplicationDataModel.class);

	private ManageJobService mjs;

	List<ApplicationEntity> list ;
	private int rowCount;
	private int jobId;
	//application status:
	private String status;

	public LazyApplicationDataModel(int jobId,  ManageJobService mjs)
	{
		logger.debug("new LazyApplicationDataModel is initialized: jobId="+jobId);
		this.jobId = jobId;
		
		this.mjs= mjs;
		//this list is used once only! pageSize doesn't matter, it's overridden by "pageSize" in load method
		/*
		list = mjs.getApplicantListByJobId(jobId, 0, pageSize, null, null, null);
		this.rowCount = mjs.getAppilcantCountByJobId(jobId, null);
		this.setRowCount(rowCount);
		*/
		logger.debug("new LazyApplicationDataModel: total count="+rowCount);
		
	}

	@Override
	public ApplicationEntity getRowData(String rowKey)
	{
		for (ApplicationEntity app : list)
		{
			if (String.valueOf(app.getAppId()).equals(rowKey))
			{
				return app;
			}
		}

		return null;
	}

	@Override
	public Object getRowKey(ApplicationEntity app)
	{
		return app.getAppId();
	}

	@Override
	public List<ApplicationEntity> load(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters)
	{
		logger.debug("load called===first="+first+"; pageSize="+pageSize+
				"; sortField= "+sortField+"; sortOrder="+sortOrder);
		
		/*
		Set<Map.Entry<String, Object>> set = filters.entrySet();
		for(Map.Entry<String, Object> entry : set)
		{
			logger.debug("Filter----: key="+entry.getKey()+"; value="+entry.getValue());
		}
		*/
		//filter by application status
		String sStatus = (String)filters.get("status");
		logger.debug("load() sStatus===="+sStatus);
		String sortDir = "asc";
		if(sortOrder != null)
		{
			if(sortOrder.name().equalsIgnoreCase("DESCENDING"))
			{
				sortDir = "desc";
			}
		}
		list = mjs
				.getApplicantListByJobId(jobId, first/pageSize, pageSize, sortField, sortDir, sStatus);

		//if(sStatus != null && !sStatus.equalsIgnoreCase(this.status))
		{
			this.status = sStatus;
			this.rowCount = mjs.getAppilcantCountByJobId(jobId, status);
			logger.debug("sStatus changed ====total rowCount====="+this.rowCount);
			//rowCount
			this.setRowCount(this.rowCount);
		}

		return list;
	}

	public void setMjs(ManageJobService mjs)
	{
		this.mjs = mjs;
	}
}
