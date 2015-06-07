package com.lxs.web.controllerbeans.job.exceptions;

import javax.faces.context.ExceptionHandlerFactory;

/**
 * This is global ExceptionHandlerFactory to handle exception
 * need to config it in faces-config.xml
 * 
 * @author lxs
 *
 */
public class ExceptionHandlerFactoryImpl extends ExceptionHandlerFactory
{

	private final ExceptionHandlerFactory parent;

	public ExceptionHandlerFactoryImpl(final ExceptionHandlerFactory parent)
	{
		this.parent = parent;
	}

	@Override
	public GlobalExceptionHandler getExceptionHandler()
	{
		return new GlobalExceptionHandler(this.parent.getExceptionHandler());
	}
}