package com.lxs.web.controllerbeans.job.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

public class DownloadFileUtil
{
	private static final Logger logger = LogManager.getLogger(DownloadFileUtil.class);
	
	private static final int DEFAULT_BUFFER_SIZE=1024; //1k
	
	public static StreamedContent downloadResume(String fileName)
	{
		StreamedContent sfile = null;

		FileInputStream inputstream = null;
		
		try
		{
			inputstream = new FileInputStream(ConfigUtil.getUploadFileDir() + fileName);
			String contentType = getFileContentType(fileName);
			logger.debug("downloadResume fileName="+fileName+"; contentType="+contentType);
			sfile = new DefaultStreamedContent(inputstream, contentType, fileName);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}

		return sfile;

	}
	
	/**
	 * This only works for txt and pdf file, for MS wrod file, it just download, can't open in browser
	 */
	public static void openFileToBrowser(FacesContext faceContext, String fileName)
	{
		HttpServletResponse response = (HttpServletResponse) faceContext
				.getExternalContext().getResponse();
		BufferedInputStream input = null;
        BufferedOutputStream output = null;
		try
		{
			File file = new File(ConfigUtil.getUploadFileDir() + fileName);
			input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

			String contentType = getFileContentType(fileName);
			logger.debug("openFileToBrowser called filename="+fileName+"; contentType="+contentType);
			
			response.reset();
			response.setContentType(contentType);  
		    response.setHeader("Content-Length", String.valueOf(file.length()));  	    
		    response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Finalize task.
            output.flush();
            
            input.close();
			output.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		faceContext.responseComplete();
	}
	
	
	private static String getFileContentType(String fileName)
	{
		int lastDotIndex = fileName.lastIndexOf(".");
		String extension = fileName.substring(lastDotIndex);
		String contentType = null;
		for (FileTypeEnum ft : FileTypeEnum.values())
		{
			if (extension.equalsIgnoreCase(ft.getFileExtension()))
			{
				contentType = ft.getFileType();
				logger.debug("MimeTye="+contentType+"; fileName="+fileName);
				break;
			}
		}
		return contentType;
	}
}
