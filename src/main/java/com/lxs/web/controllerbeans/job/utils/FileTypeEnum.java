package com.lxs.web.controllerbeans.job.utils;

public enum FileTypeEnum
{
	TEXT_FILE("text/plain", ".txt"), 
	WORD_OLD_FILE("application/msword", ".doc"),
	 //this doesn't work, got "application/octet-stream"?? only Firfox recongnize it
	WORD_NEW_FILE("application/vnd.openxmlformats-officedocument.wordprocessingml.document", ".docx"),
	PDF_FILE("application/pdf", ".pdf");
	
	private String fileType;
	private String fileExtension;
	private FileTypeEnum(String fileType, String fileExtension)
	{
		this.fileType = fileType;
		this.fileExtension = fileExtension;
	}
	
	public String getFileType()
	{
		return fileType;
	}
	
	public String getFileExtension()
	{
		return fileExtension;
	}
}
