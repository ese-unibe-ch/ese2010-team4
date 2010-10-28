package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

public class Uploader {

	private String uploadPath;
	private ArrayList<File> uploadedFiles;
	private FileInputStream iStream;

	public Uploader(String uploadPath) {
		this.uploadPath = uploadPath;
	}

	// Uploads a file to the given Path with the format nameid.typ
	// DR a lot of refactoring needed!
	public String upload(File attachment, String name, Long id, String type)
			throws FileNotFoundException, IOException {
		String filePath = uploadPath + name + id + "." + type;
		copyFile(attachment, filePath);
		return filePath;
	}

	public String upload(File attachment) throws FileNotFoundException,
			IOException {
		copyFile(attachment, uploadPath);
		return uploadPath + attachment.getName();
	}

	public ArrayList<File> getUploadedFiles() {
		return uploadedFiles;
	}

	private void copyFile(File attachment, String filePath)
			throws FileNotFoundException, IOException {
		iStream = new FileInputStream(attachment);
		File outputFile = new File(filePath);
		IOUtils.copy(iStream, new FileOutputStream(outputFile));
		uploadedFiles.add(outputFile);
	}

}
