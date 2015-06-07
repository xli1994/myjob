package com.lxs.web.controllerbeans.job.utils;

import java.lang.reflect.Field;
import java.util.Comparator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.lxs.business.ejb.entity.JobEntity;

/**
 * A util general comparator for JobEntity to sort
 * @author lxs
 *
 */
public class JobEntitySorter implements Comparator<JobEntity>
{
	private static final Logger logger = LogManager.getLogger(JobEntitySorter.class);
	
	private String sortField;

	private boolean sortAscending;

	public JobEntitySorter(String sortField, boolean sortOrder)
	{
		this.sortField = sortField;
		this.sortAscending = sortOrder;
		logger.debug("JobEntitySorter sortField="+sortField+"; sortOrder="+sortOrder);
		
	}

	@Override
	public int compare(JobEntity job1, JobEntity job2)
	{
		try
		{
			//since all fields are private, use getDeclaredField
			Field field1 = JobEntity.class.getDeclaredField(this.sortField);
			field1.setAccessible(true);
			
			Object value1 = field1.get(job1);
			Object value2 = field1.get(job2);
			//logger.debug("value1="+value1+"; value2="+value2);
			int value = ((Comparable) value1).compareTo(value2);

			return sortAscending ? value : -1 * value;
		}
		catch (Exception e)
		{
			throw new RuntimeException();
		}
	}
}