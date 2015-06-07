package com.lxs.web.servlets;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is to To instruct the browser to not cache the dynamic JSF pages
 * Also Avoiding ViewExpiredException
 * see detatil: http://stackoverflow.com/questions/3642919/javax-faces-application-viewexpiredexception-view-could-not-be-restored
 * 
 * @author lxs
 *
 */
@WebFilter(servletNames = { "Faces Servlet" })
// Must match <servlet-name> of your FacesServlet.
public class NoCacheFilter implements Filter
{
	private static final Logger logger = LogManager.getLogger(NoCacheFilter.class);
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if (!req.getRequestURI().startsWith(
				req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER))
		{ 
			// Skip JSF resources (CSS/JS/Images/etc)
			res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
			res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			res.setDateHeader("Expires", 0); // Proxies.
			
			//logger.debug("NoCacheFilter: set Cache-Control");
		}

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		
		logger.debug("NoCacheFilter init()");
	}

	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub

	}
}