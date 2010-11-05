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
	private boolean sizeChecked;
	private String type;

	/**
	 * Initialize an Uploader with a given <code>uploadPath</code> to which the
	 * files will be saved, minSize and maxSize can be changed to restrict the
	 * lenght (in byte) of a uploaded file
	 * 
	 * @param uploadPath
	 */
	public Uploader(String uploadPath) {
		this.uploadPath = uploadPath;
		this.uploadedFiles = new ArrayList<File>();
		sizeChecked = false;
		minSize = maxSize = 0;
		assert invariant();
	}

	/**
	 * Uploads a file in the <code>uploadPath</code> with the given name
	 * <code>uploadPath</code> is not null and minSize <= maxSize
	 * 
	 * @param attachment
	 * @param name
	 * @return path on the server
	 */
	public String upload(File attachment, String name) {
		assert invariant();
		this.getTypeOf(attachment);
		String filePath = uploadPath + name + "." + type;
		copyFile(attachment, filePath);
		assert invariant();
		return filePath;
	}

	/**
	 * Uploads a file in the <code>uploadPath</code> with the original name
	 * <code>uploadPath</code> is not null and minSize <= maxSize
	 * 
	 * @param attachment
	 * @return path on the server
	 */
	public String upload(File attachment) {
		assert invariant();
		copyFile(attachment, uploadPath + attachment.getName());
		assert invariant();
		return uploadPath + attachment.getName();
	}

	public ArrayList<File> getUploadedFiles() {
		return uploadedFiles;
	}

	public int getMinSize() {
		return minSize;
	}

	public void setMinSize(int minSize) {
		assert minSize >= 0;
		this.minSize = minSize;
		sizeChecked = true;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		assert maxSize > minSize;
		this.maxSize = maxSize;
		sizeChecked = true;
	}

	private void checkSizeOf(File attachment) throws Exception {
		long lenght = attachment.length();
		if (minSize == 0 && maxSize == 0)
			throw new Exception("minSize and maxSize are still zero");
		else if (lenght < minSize)
			throw new Exception("File is too small");
		else if (lenght > maxSize)
			throw new Exception("File is too big");
	}

	private void getTypeOf(File attachment) {
		String filename = attachment.getName();
		this.type = filename.substring(filename.lastIndexOf('.') + 1);
	}

	private void copyFile(File attachment, String filePath) {
		try {
			if (sizeChecked)
				this.checkSizeOf(attachment);
			iStream = new FileInputStream(attachment);
			File outputFile = new File(filePath);
			IOUtils.copy(iStream, new FileOutputStream(outputFile));
			uploadedFiles.add(outputFile);
		} catch (FileNotFoundException e) {
			System.out.println("File not found!");
		} catch (IOException io) {
			System.out.println("Input & Output Exception");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private boolean invariant() {
		return maxSize >= minSize && uploadPath != null;
	}
}
