package com.lxs.web.servlets;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.dao.SolrClientFactory;

/**
 * This is a util class to instantiate EntityManagerFactory and create EntityManager
 * 
 * @author lxs
 *
 */

@WebListener
public class EMF implements ServletContextListener
{
	private static final Logger logger = LogManager.getLogger(EMF.class);
	//JPA
	private static EntityManagerFactory emf;
	
	//unit name, this is from <persistence-unit name="my-jpa"> defined in "persistence.xml
	//change to hibernate
	private static final String unitName = "my-hibernate-jpa" ;
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)
	{
		//create EntityManagerFactory 
		logger.debug("MyJob ====EMF ContextListener::: contextInitialized() created EntityManagerFactory");
		emf = Persistence.createEntityManagerFactory(unitName);
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		logger.debug("Myjob === EMF ContextListener::: contextDestroyed() called, shutdown EMF and solr");
	
		emf.close();
		//shutdown solr
		SolrClientFactory.shutdown();
	}

    public static EntityManager createEntityManager() {
        if (emf == null) {
            throw new IllegalStateException("Context is not initialized yet.");
        }

        return emf.createEntityManager();
    }
}
