package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class Uploader {

	private String uploadPath;
	private FileInputStream iStream;

	public Uploader(String uploadPath) {
		this.uploadPath = uploadPath;
	}

	// Uploads a file to the given Path with the format nameid.typ
	// DR a lot of refactoring needed!
	public String upload(File attachment, String name, String id, String type)
			throws FileNotFoundException, IOException {
		iStream = new FileInputStream(attachment);
		File outputFile = new File("/qa" + uploadPath + name + id + "." + type);
		IOUtils.copy(iStream, new FileOutputStream(outputFile));
		return uploadPath + name + id + "." + type;
	}

}
