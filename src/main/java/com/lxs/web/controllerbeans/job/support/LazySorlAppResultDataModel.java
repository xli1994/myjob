package com.lxs.web.controllerbeans.job.support;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.lxs.business.service.SearchService;
import com.lxs.business.vo.SearchResultList;
import com.lxs.business.vo.SolrApplicantsVO;

/**
 * This is a primefaces datamodel for display search applicant result 
 * @author lxs
 *
 */
public class LazySorlAppResultDataModel extends LazyDataModel<SolrApplicantsVO> implements
		Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LazySorlAppResultDataModel.class);

	private SearchService searchService;

	SearchResultList<SolrApplicantsVO> list;
	private int rowCount;
	private String query;

	public LazySorlAppResultDataModel(String query,  SearchService searchService)
	{
		logger.debug("new LazyApplicationDataModel is initialized: query=" + query );
		this.query = query;

		this.searchService = searchService;
		//this list is used once only! pageSize doesn't matter, it's overridden by "pageSize" in load method
		/*
		list = this.searchService.searchApplicants(query, 0, pageSize, null, null);
		this.rowCount = list.getTotalResult();
		this.setRowCount(rowCount);
		*/
		logger.debug("new LazyApplicationDataModel: total count=" + rowCount);

	}

	@Override
	public SolrApplicantsVO getRowData(String rowKey)
	{
		for (SolrApplicantsVO app : list)
		{
			if (String.valueOf(app.getAppId()).equals(rowKey))
			{
				return app;
			}
		}

		return null;
	}

	@Override
	public Object getRowKey(SolrApplicantsVO app)
	{
		return app.getAppId();
	}

	@Override
	public SearchResultList<SolrApplicantsVO> load(int first, int pageSize, String sortField,
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

		String sortDir = "asc";
		if(sortOrder != null)
		{
			if(sortOrder.name().equalsIgnoreCase("DESCENDING"))
			{
				sortDir = "desc";
			}
		}
		list = searchService.searchApplicants(query, first/pageSize, pageSize, sortField, sortDir);
		this.rowCount = list.getTotalResult();
		logger.debug(" ====total rowCount====="+this.rowCount);
			//rowCount
		this.setRowCount(this.rowCount);

		return list;
	}


}
