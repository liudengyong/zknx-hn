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
 * @���ߣ��Ż� @���ڣ�2006-5-14 @˵����
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
				Debug.Log("���ڽ�ѹ: " + zipEntry.getName());
				
				String name = null;
				if (zipEntry.isDirectory()) {
					name = zipEntry.getName();
					name = name.substring(0, name.length() - 1);
					File f = new File(outputDirectory + File.separator + name);
					f.mkdir();
					Debug.Log("����Ŀ¼��" + outputDirectory + File.separator + name);
				} else {
					String fileName = zipEntry.getName();
					fileName = fileName.replace('\\', '/');
					// System.out.println("�����ļ�1��" +fileName);
					if (fileName.indexOf("/") != -1) {
						createDirectory(outputDirectory, fileName.substring(0,
								fileName.lastIndexOf("/")));
						fileName = fileName.substring(
								fileName.lastIndexOf("/") + 1,
								fileName.length());
					}
					File f = new File(outputDirectory + File.separator
							+ zipEntry.getName());
					Debug.Log("��ѹ" + f.getName());
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

			// ɾ���ļ�����������ɾ����Ϊ�ļ�����ʹ�ã�Ӧ���ϴ��Ǵ�ɾ
			// ��ѹ��ɾ��ѹ���ļ�
			// File zipFileToDel = new File(zipFileName);
			// zipFileToDel.delete();
			// System.out.println("����ɾ���ļ���"+ zipFileToDel.getCanonicalPath());

			// //ɾ����ѹ�����һ��Ŀ¼
			// delALayerDir(zipFileName, outputDirectory);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		return false;
	}

	/**
	 * ɾ��һ��Ŀ¼
	 * 
	 * @param zipFileName
	 * @param outputDirectory
	 */
	public void delALayerDir(String zipFileName, String outputDirectory) {
		String[] dir = zipFileName.replace('\\', '/').split("/");
		String fileFullName = dir[dir.length - 1]; // �õ�aa.zip
		int pos = -1;
		pos = fileFullName.indexOf(".");
		String fileName = fileFullName.substring(0, pos); // �õ�aa
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
	 * ��sourceDirĿ¼���ļ�ȫ��copy��destDir��ȥ
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
				// ���½�Ŀ¼
				new File(destDir + File.separator + f.getName()).mkdir();
				copyFile(destDir, sourceBaseDir, f); // �ݹ����
			}
		}
	}

	/**
	 * ��sourceDirĿ¼���ļ�ȫ��copy��destDir��ȥ
	 */
	public void deleteSourceBaseDir(File curFile) throws Exception {
		File[] lists = curFile.listFiles();
		File parentFile = null;
		for (int i = 0; i < lists.length; i++) {
			File f = lists[i];
			if (f.isFile()) {
				f.delete();
				// �����ĸ�Ŀ¼û���ļ��ˣ�˵���Ѿ�ɾ�꣬Ӧ��ɾ����Ŀ¼
				parentFile = f.getParentFile();
				if (parentFile.list().length == 0)
					parentFile.delete();
			} else {
				deleteSourceBaseDir(f); // �ݹ����
			}
		}
	}

}

