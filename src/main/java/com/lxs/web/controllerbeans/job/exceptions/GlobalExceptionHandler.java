package com.lxs.web.controllerbeans.job.exceptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.web.controllerbeans.job.utils.CommonConstants;

/**
 * This is global exception handler used by GlobalExceptionHandlerFactory
 * @author lxs
 *
 */
public class GlobalExceptionHandler extends ExceptionHandlerWrapper
{
	private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

	private final javax.faces.context.ExceptionHandler wrapped;
	
	public GlobalExceptionHandler(final javax.faces.context.ExceptionHandler wrapped)
	{
		this.wrapped = wrapped;
	}

	@Override
	public javax.faces.context.ExceptionHandler getWrapped()
	{
		return this.wrapped;
	}

	@Override
	public void handle() throws FacesException
	{
		final Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator();
		for (; it.hasNext();)
		{
			Throwable t = it.next().getContext().getException();
			while ((t instanceof FacesException) && t.getCause() != null)
			{
				t = t.getCause();
			}
			if (t instanceof JobNotFoundException || t instanceof FileNotFoundException 
				     /*|| t instanceof ViewExpiredException */)
			{
				final FacesContext facesContext = FacesContext.getCurrentInstance();
				final ExternalContext externalContext = facesContext.getExternalContext();
				final Map<String, Object> requestMap = externalContext.getRequestMap();
				try
				{
					logger.info("Error msg="+t.getMessage());
					String message;
					if (t instanceof ViewExpiredException)
					{
						final String viewId = ((ViewExpiredException) t).getViewId();
						message = "View is expired with viewId: " +viewId;
					}
					else
					{
						message = t.getMessage(); // beware, don't leak internal info!
					}
					requestMap.put("errorMsg", message);
					try
					{
						logger.warn("ReDirect user to error page");
						//change to redirect
						//externalContext.dispatch(errorPage);
						externalContext.redirect(CommonConstants.JOB_NOT_FOUND_PAGE);
					}
					catch (final IOException e)
					{
						e.printStackTrace();
					}
					facesContext.responseComplete();
				}
				finally
				{
					it.remove();
				}
			}
		}
		
		getWrapped().handle();
	}
}
