package com.lxs.web.controllerbeans.job.exceptions;

/**
 * it can't be thrown from method annotated with @poscontruct (ignored)
 * @author lxs
 *
 */
public class JobNotFoundException extends Exception
{
	public JobNotFoundException()
	{	
	}
	
	public JobNotFoundException (String message) {
        super (message);
    }

    public JobNotFoundException (Throwable cause) {
        super (cause);
    }

    public JobNotFoundException (String message, Throwable cause) {
        super (message, cause);
    }
}
