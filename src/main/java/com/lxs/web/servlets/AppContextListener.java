package com.lxs.web.servlets;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is to add some staffs upon web app start, and shut down when server is shut down
 * 
 * can't have two ServletContextListener, remove it to EMF
 * @author lxs
 *
 */
//@WebListener
public class AppContextListener implements ServletContextListener
{
	private static final Logger logger = LogManager.getLogger(AppContextListener.class);
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)
	{
		logger.debug("MyJob ====AppContextListener::: contextInitialized() called");
  
		//set system property so that null is displayed for integer/double in jsf page
		//this is a bug of Tomcat: not working for Tom8
		System.setProperty("org.apache.el.parser.COERCE_TO_ZERO", "false");
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		logger.debug("MyJob AppContextListener::: contextDestroyed() called");
	}

}
