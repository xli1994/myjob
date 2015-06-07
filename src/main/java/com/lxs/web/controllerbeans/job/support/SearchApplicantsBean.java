package com.lxs.web.controllerbeans.job.support;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.StreamedContent;

import com.lxs.business.service.SearchService;
import com.lxs.business.vo.SolrApplicantsVO;

import com.lxs.web.controllerbeans.job.utils.CommonUtils;
import com.lxs.web.controllerbeans.job.utils.DownloadFileUtil;

@ManagedBean(name = "searchApplicantsBean", eager = true)
@ViewScoped
public class SearchApplicantsBean implements Serializable
{
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SearchApplicantsBean.class);
	
	@ManagedProperty(value = "#{searchService}")
	private SearchService searchService ;
	
	private LazyDataModel<SolrApplicantsVO> appModel;
	
	private String query;
	
	@PostConstruct
	private void init()
	{
		logger.debug("=====SearchApplicantsBean init() called");

		String queryApp = CommonUtils.getRequestParameter(FacesContext.getCurrentInstance()
				.getExternalContext(), "queryApp");
		
		logger.debug("=====SearchApplicantsBean init() called, param queryApp="+queryApp);
		if (!StringUtils.isEmpty(queryApp))
		{
			this.query = queryApp;
			appModel = new LazySorlAppResultDataModel(query, searchService);
		}

	}
	
	
	/**
	 * This is for search job, redirect to corresponding page
	 * @return
	 */
	public String searchApplicants()
	{
		logger.debug("searchApplicants called, query="+query);
		if(StringUtils.isEmpty(query))
		{
			return null;
		}
		
		appModel = new LazySorlAppResultDataModel(query, searchService);
		
		return null;
	}
	
	public StreamedContent downloadResume(SolrApplicantsVO sAppVO)
	{
		return DownloadFileUtil.downloadResume(sAppVO.getResumeName());
	}
	
	public LazyDataModel<SolrApplicantsVO> getAppModel()
	{
		return appModel;
	}
	
	/*  ========getter/setter  ======================*/
	
	public void setSearchService(SearchService searchService)
	{
		this.searchService = searchService;
	}



	public String getQuery()
	{
		return query;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}
}
