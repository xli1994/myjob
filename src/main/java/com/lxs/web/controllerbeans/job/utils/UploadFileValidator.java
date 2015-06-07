package com.lxs.web.controllerbeans.job.utils;


import javax.faces.application.FacesMessage;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//this works
@FacesValidator("uploadFileValidator")
public class UploadFileValidator implements Validator
{
	private static final Logger logger = LogManager.getLogger(UploadFileValidator.class);

	//resource bundle name
	private static String bundleName= "validatorMsg";
	//max allowed file size, 1024*2000=2MB
	private int MAXSIZE;
	
	public UploadFileValidator()
	{
		MAXSIZE = Integer.valueOf(MessageProvider.getValue("maxFileSize", bundleName));
	}
	
	@Override
	public void validate(FacesContext context, UIComponent uiComponent, Object value)
			throws ValidatorException
	{

		Part part = (Part) value;

		//test bundle: works 
		//logger.debug("Bundle msg test="+ MessageProvider.getValue("test", "validatorMsg"));
		
		// 1. validate file name length
		String fileName = part.getSubmittedFileName();
		logger.debug("----- validator fileName: " + fileName);
		
		if (StringUtils.isEmpty(fileName)|| fileName.length() < 5 || fileName.indexOf(".") == -1)
		{
			FacesMessage message = new FacesMessage(MessageProvider.getValue("missingUploadFile", bundleName));
			throw new ValidatorException(message);
		}

		/*IE will append file path, may very long
		if (fileName.length() > 100)
		{
			FacesMessage message = new FacesMessage("File name is too long !!");
			throw new ValidatorException(message);
		}
		*/
		int lastDotIndex = fileName.lastIndexOf(".");
		String extension = fileName.substring(lastDotIndex);
		logger.debug("lastDotIndex=" + lastDotIndex + "; file extension=" + extension);
		logger.debug("fileContentType=" + part.getContentType() + "; file size =" + part.getSize());

		// 2. validate file type (only text files allowed)
		String contentType = part.getContentType();

		boolean isFileTypeValid = false;
		for (FileTypeEnum ft : FileTypeEnum.values())
		{
			if (contentType.equalsIgnoreCase(ft.getFileType()) && 
					extension.equalsIgnoreCase(ft.getFileExtension()))
			{
				isFileTypeValid = true;
				logger.debug("File type is valid");
				break;
			}
		}

		if (!isFileTypeValid)
		{
			//IE and Chrome give MS new word file docx content-type "application/octet-stream"
			//check it
			if (contentType.equalsIgnoreCase("application/octet-stream")
					&& extension.equalsIgnoreCase(FileTypeEnum.WORD_NEW_FILE.getFileExtension()))
			{
				logger.debug("This is ms 2007 word file");
				isFileTypeValid = true;
			}
			else
			{
				FacesMessage message = new FacesMessage(
						MessageProvider.getValue("invalidFileType", bundleName));
				throw new ValidatorException(message);
			}
		}

		// 3. validate file size (should not be greater than 1mb )
		if (part.getSize() > MAXSIZE)
		{
			FacesMessage message = new FacesMessage(MessageProvider.getValue("sizeTooBig", bundleName));
			throw new ValidatorException(message);
		}
	}

}
