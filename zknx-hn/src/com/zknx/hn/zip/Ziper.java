package com.zknx.hn.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import com.zknx.hn.common.Debug;

/*
 * @作者：张华 @日期：2006-5-14 @说明：
 */
public class Ziper {
	public void zip(String zipFileName, String inputFile) throws Exception {
		zip(zipFileName, new File(inputFile));
	}

	public void zip(String zipFileName, File inputFile) throws Exception {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				new String(zipFileName.getBytes("gb2312"))));
		System.out.println("zip start");
		zip(out, inputFile, "");
		System.out.println("zip done");
		out.close();
	}

	public void zip(ZipOutputStream out, File f, String base) throws Exception {
		System.out.println("Zipping  " + f.getName());
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			// out.putNextEntry(new ZipEntry(base + "/"));
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < fl.length; i++) {
				zip(out, fl[i], base + fl[i].getName());
				System.out.println(fl[i].getName());
				// System.out.println(new
				// String(fl[i].getName().getBytes("gb2312")));
			}
		} else {
			// out.putNextEntry(new ZipEntry(base));
			out.putNextEntry(new ZipEntry(base));
			System.out.println(base);
			FileInputStream in = new FileInputStream(f);
			int b;
			while ((b = in.read()) != -1)
				out.write(b);
			in.close();
		}
	}

	private static void createDirectory(String directory, String subDirectory) {
		String dir[];
		File fl = new File(directory);
		try {
			if (subDirectory == "" && fl.exists() != true)
				fl.mkdir();
			else if (subDirectory != "") {
				dir = subDirectory.replace('\\', '/').split("/");
				for (int i = 0; i < dir.length; i++) {
					File subFile = new File(directory + File.separator + dir[i]);
					if (subFile.exists() == false)
						subFile.mkdir();
					directory += File.separator + dir[i];
				}
			}
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	public static boolean UnZip(String zipFileName, String outputDirectory) {
		try {
			// ZipFile zipFile = new ZipFile(zipFileName, "GBK");
			ZipFile zipFile = new ZipFile(zipFileName);

			@SuppressWarnings("rawtypes")
			Enumeration e = zipFile.entries();
			ZipEntry zipEntry = null;
			createDirectory(outputDirectory, "");
			while (e.hasMoreElements()) {
				zipEntry = (ZipEntry) e.nextElement();
				//String entryName = ;
				Debug.Log("正在解压: " + zipEntry.getName());
				
				String name = null;
				if (zipEntry.isDirectory()) {
					name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					f.mkdir();
					Debug.Log("创建目录：" + outputDirectory + File.separator + name);
				} else {
					String fileName = zipEntry.getName();
					fileName = fileName.replace('\\', '/');
					// System.out.println("测试文件1：" +fileName);
					if (fileName.indexOf("/") != -1) {
						createDirectory(outputDirectory, fileName.substring(0,
								fileName.lastIndexOf("/")));
						fileName = fileName.substring(
								fileName.lastIndexOf("/") + 1,
								fileName.length());
					}
					File f = new File(outputDirectory + File.separator
							+ zipEntry.getName());
					Debug.Log("解压" + f.getName());
					f.createNewFile();
					InputStream in = zipFile.getInputStream(zipEntry);
					FileOutputStream out = new FileOutputStream(f);
					byte[] by = new byte[1024];
					int c;
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					out.close();
					in.close();
				}

			}

			return true;

			// 删除文件不能在这里删，因为文件正在使用，应在上传那处删
			// 解压后，删除压缩文件
			// File zipFileToDel = new File(zipFileName);
			// zipFileToDel.delete();
			// System.out.println("正在删除文件："+ zipFileToDel.getCanonicalPath());

			// //删除解压后的那一层目录
			// delALayerDir(zipFileName, outputDirectory);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		return false;
	}

	/**
	 * 删掉一层目录
	 * 
	 * @param zipFileName
	 * @param outputDirectory
	 */
	public void delALayerDir(String zipFileName, String outputDirectory) {
		String[] dir = zipFileName.replace('\\', '/').split("/");
		String fileFullName = dir[dir.length - 1]; // 得到aa.zip
		int pos = -1;
		pos = fileFullName.indexOf(".");
		String fileName = fileFullName.substring(0, pos); // 得到aa
		String sourceDir = outputDirectory + File.separator + fileName;
		try {
			copyFile(new File(outputDirectory), new File(sourceDir), new File(
					sourceDir));
			deleteSourceBaseDir(new File(sourceDir));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将sourceDir目录的文件全部copy到destDir中去
	 */
	public void copyFile(File destDir, File sourceBaseDir, File sourceDir)
			throws Exception {
		File[] lists = sourceDir.listFiles();
		if (lists == null)
			return;
		for (int i = 0; i < lists.length; i++) {
			File f = lists[i];
			if (f.isFile()) {
				FileInputStream fis = new FileInputStream(f);
				String content = "";
				String sourceBasePath = sourceBaseDir.getCanonicalPath();
				String fPath = f.getCanonicalPath();
				String drPath = destDir
						+ fPath.substring(fPath.indexOf(sourceBasePath)
								+ sourceBasePath.length());
				FileOutputStream fos = new FileOutputStream(drPath);
				byte[] b = new byte[2048];
				while (fis.read(b) != -1) {
					if (content != null)
						content += new String(b);
					else
						content = new String(b);
					b = new byte[2048];
				}
				content = content.trim();
				fis.close();
				fos.write(content.getBytes());
				fos.flush();
				fos.close();
			} else {
				// 先新建目录
				new File(destDir + File.separator + f.getName()).mkdir();
				copyFile(destDir, sourceBaseDir, f); // 递归调用
			}
		}
	}

	/**
	 * 将sourceDir目录的文件全部copy到destDir中去
	 */
	public void deleteSourceBaseDir(File curFile) throws Exception {
		File[] lists = curFile.listFiles();
		File parentFile = null;
		for (int i = 0; i < lists.length; i++) {
			File f = lists[i];
			if (f.isFile()) {
				f.delete();
				// 若它的父目录没有文件了，说明已经删完，应该删除父目录
				parentFile = f.getParentFile();
				if (parentFile.list().length == 0)
					parentFile.delete();
			} else {
				deleteSourceBaseDir(f); // 递归调用
			}
		}
	}

}

