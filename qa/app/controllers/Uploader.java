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
	private int minSize;
	private int maxSize;
	private String type;

	public Uploader(String uploadPath) {
		this.uploadPath = uploadPath;
		this.uploadedFiles = new ArrayList<File>();
		minSize = maxSize = 0;
		assert invariant();
	}

	// Uploads a file to the given Path with the format nameid.typ
	// DR a lot of refactoring needed!
	public String upload(File attachment, String name) {
		assert invariant();
		this.type(attachment);
		String filePath = uploadPath + name + "." + type;
		copyFile(attachment, filePath);
		assert invariant();
		return filePath;
	}

	private void type(File attachment) {
		String filename = attachment.getName();
		this.type = filename.substring(filename.lastIndexOf('.') + 1, filename
				.length());
	}

	public String upload(File attachment) {
		assert invariant();
		copyFile(attachment, uploadPath);
		assert invariant();
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

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		this.minSize = minSize;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}

	private boolean invariant() {
		return maxSize >= minSize && uploadPath != null;
	}
}
