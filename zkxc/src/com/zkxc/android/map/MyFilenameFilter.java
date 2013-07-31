package com.zkxc.android.map;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;

public class MyFilenameFilter implements FilenameFilter,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4496375868262182384L;
	private String filename;
	
	public MyFilenameFilter(String filename) {
		this.filename = filename;
	}
	
	public boolean accept(File dir, String filename) {
		int lastDot = filename.lastIndexOf('.');
//		String ext = filename.substring(lastDot+1);
		if(lastDot>0){
			filename = filename.substring(0,lastDot);
		}
		if(filename.equals(this.filename)){
			return true;
		}
		else{
			return false;
		}
	}

}
