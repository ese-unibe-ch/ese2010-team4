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
		this.uploadedFiles = new ArrayList<File>();
	}

	// Uploads a file to the given Path with the format nameid.typ
	// DR a lot of refactoring needed!
	public String upload(File attachment, String name, String type) {
		String filePath = uploadPath + name + "." + type;
		copyFile(attachment, filePath);
		return filePath;
	}

	public String upload(File attachment) {
		copyFile(attachment, uploadPath);
		return uploadPath + attachment.getName();
	}

	public ArrayList<File> getUploadedFiles() {
		return uploadedFiles;
	}

	private void copyFile(File attachment, String filePath) {
		try {
			iStream = new FileInputStream(attachment);
			File outputFile = new File(filePath);
			IOUtils.copy(iStream, new FileOutputStream(outputFile));
			uploadedFiles.add(outputFile);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException io) {
			System.out.println("Input & Output Exception");
		}
	}

}
