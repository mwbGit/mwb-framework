package com.mwb.framework.cxf;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.ContentDisposition;

import java.io.IOException;
import java.io.InputStream;

public class AttachmentFile {
	private String fileName;
	private InputStream file;
	
	public static AttachmentFile getInstance(Attachment attachment) throws IOException {
		ContentDisposition cd = attachment.getContentDisposition();
		String fileName = cd.getParameter("filename");
		
		if (StringUtils.isBlank(fileName)) {
			return null;
		}
		
		InputStream file = attachment.getDataHandler().getInputStream();
		
		
		return new AttachmentFile(fileName, file);
	}
	
	private AttachmentFile(String fileName, InputStream file) {
		this.fileName = fileName;
		this.file = file;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public InputStream getFile() {
		return file;
	}
}
