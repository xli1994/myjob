package com.lxs.web.servlets;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is lifecycle listener for jsf
 * JSF lifecycle contains 6 phases:
	Restore view
	Apply request values
	Process validations
	Update model values
	Invoke application
	Render response
	
	This need to be registered in faces-config.xml
	
 * @author lxs
 *
 */

public class LifeCycleListener implements PhaseListener
{
	private static final Logger logger = LogManager.getLogger(LifeCycleListener.class);
	@Override
	public void afterPhase(PhaseEvent event)
	{
		//logger.debug("LifeCycleListener::END PHASE:: " + event.getPhaseId());
		
	}

	@Override
	public void beforePhase(PhaseEvent event)
	{
		//logger.debug("LifeCycleListener::START PHASE:: " + event.getPhaseId());
		
	}

	@Override
	public PhaseId getPhaseId()
	{
		return PhaseId.ANY_PHASE;
	}

}
