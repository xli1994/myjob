package com.lxs.web.controllerbeans.job.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@FacesValidator("emailValidator")
public class EmailValidator implements Validator
{
	private static final Logger logger = LogManager.getLogger(EmailValidator.class);
	private static Pattern pattern;
	private Matcher matcher;

	//resource bundle name
	private static String bundleName= "validatorMsg";
	/**
	 * Description
		^					#start of the line
		[_A-Za-z0-9-\\+]+	#  must start with string in the bracket [ ] (allow "-" and "+", must contains one or more (+)
		(					#  start of group #1
		\\.[_A-Za-z0-9-]+	#  follow by a dot "." and string in the bracket [ ], must contains one or more (+)
		)*					#  end of group #1, this group is optional (*)
		@					#  must contains a "@" symbol
	 	[A-Za-z0-9-]+     	#  follow by string in the bracket [ ], must contains one or more (+)
	  	(					#  start of group #2 - first level TLD checking
	   		\\.[A-Za-z0-9]+ #  follow by a dot "." and string in the bracket [ ], must contains one or more (+)
	  	)*					#  end of group #2, this group is optional (*)
	  	(					#  start of group #3 - second level TLD checking
	   		\\.[A-Za-z]{2,} #  follow by a dot "." and string in the bracket [ ], with minimum length of 2
	  	)					#  end of group #3
		$					#end of the line

		The combination means, email address must start with "_A-Za-z0-9-\\+" , optional follow 
		by ".[_A-Za-z0-9-]", and end with a "@" symbol. The email’s domain name must start with
		 "A-Za-z0-9-", follow by first level Tld (.com, .net) ".[A-Za-z0-9]" and optional follow
		  by a second level Tld (.com.au, .com.my) "\\.[A-Za-z]{2,}", where second level Tld 
		  must start with a dot "." and length must equal or more than 2 characters.

		
	 */
	//original pattern
	//private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
	//		+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	//There could be a case with ' in names, such as Becky.L.O'Sullivan@aaa.com
	//modify above pattern to include it in (\\.[_A-Za-z0-9-']+) and add (['][_A-Za-z0-9-\\+]+)*
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(['][_A-Za-z0-9-\\+]+)*(\\.[_A-Za-z0-9-']+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	static {
		pattern = Pattern.compile(EMAIL_PATTERN);
	}
	
	public EmailValidator()
	{
		
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException
	{
		String email = (String) value;
		logger.debug("Email="+email);
		if (StringUtils.isEmpty(email) || email.length() < 4)
		{
			FacesMessage message = new FacesMessage(MessageProvider.getValue("missingEmail", bundleName));
			throw new ValidatorException(message);
		}
		
		if(email.length() > 100)
		{
			FacesMessage message = new FacesMessage(MessageProvider.getValue("maxEmailLength", bundleName));
			throw new ValidatorException(message);
		}
		
		matcher = pattern.matcher(email.trim());
		
		if( !matcher.matches())
		{
			logger.debug("Invalid email");
			FacesMessage message = new FacesMessage(MessageProvider.getValue("invalidEamil", bundleName));
			throw new ValidatorException(message);
		}
		
	}
	
	/**
	 * This is a util method to reuse above validate method to validate email
	 * if validation failed, it return a FacesMessage; otherwise return null
	 * @param email
	 * @return
	 */
	public FacesMessage validate(String email)
	{
		logger.debug("Custome vailidate email=" + email);
		try
		{
			validate(null, null, email);
		}
		catch (ValidatorException e)
		{
			String msg = e.getMessage(); 
			logger.debug("got exception errormsg="+msg);
			
			FacesMessage fmsg = e.getFacesMessage();
			fmsg.setSeverity(FacesMessage.SEVERITY_ERROR);
			return fmsg;
		}
		
		return null;
	}
}