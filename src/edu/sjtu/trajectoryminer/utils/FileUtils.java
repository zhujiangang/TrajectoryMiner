package edu.sjtu.trajectoryminer.utils;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * 文件操作工具类
 * 
 * @author Zhu
 * 
 */
public class FileUtils {

	public static String generateFileName(File file, String suffix,
			String extension) {
		String oldName = file.getName();
		return generateFileName(oldName, suffix, extension);
	}

	public static String generateFileName(String fileName, String suffix,
			String extension) {
		// String oldName = file.getName();
		String newName = "";
		if (FileUtils.getExt(fileName).length() == 0)
			newName = fileName.substring(0, fileName.length()
					- FileUtils.getExt(fileName).length());
		else
			newName = fileName.substring(0, fileName.length()
					- FileUtils.getExt(fileName).length() - 1);
		if (!StringUtils.isBlank(suffix))
			newName += "_" + suffix;
		if (!StringUtils.isBlank(extension))
			newName += "." + extension;
		return newName;
	}

	public static void appendFile(File f, String content) throws Exception {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(f, true));
			writer.write(content);
		} catch (Exception e) {
			throw e;
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void appendLine(File f, int lineNum, String str) {

	}

	public static void modifyLine(File f, int lineNum, String str) {
		BufferedReader reader = null;
		int i = 0;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(f), "utf-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				i++;
				if (i == lineNum) {

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static BufferedWriter getBufferedWriter(File file) {
		BufferedWriter writer = null;
		try {
			createFile(file, true);
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "utf-8"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return writer;
	}

	public static void closeWriter(BufferedWriter writer) {
		if (writer != null) {
			try {
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void writeFile(File f, Collection<String> list){
		BufferedWriter writer = getBufferedWriter(f);
		if (list == null)
			return;
		// String content = "";
		try{
			for (String str : list) {
				// content += str + "\n";
				writer.write(str + "\n");
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeWriter(writer);
		}
	}

	public static void writeFile(File f, String content) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f), "utf-8"));
			writer.write(content);
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String readFile(InputStream in) {
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String line = null;
			while ((line = reader.readLine()) != null)
				sb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	public static String readFile(File f) {
		try {
			return readFile(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static List<String> readLine(InputStream in) {
		List<String> result = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String line = null;
			while ((line = reader.readLine()) != null)
				result.add(line);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public static List<String> readLine(File f) {
		try {
			return readLine(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static String getExt(File f) {
		if (!f.getName().contains("."))
			return "";
		return f.getName().substring(f.getName().lastIndexOf(".") + 1);
	}

	public static String getExt(String name) {
		if (!name.contains("."))
			return "";
		return name.substring(name.lastIndexOf(".") + 1);
	}

	public static boolean deleteFolder(File folder) {
		return deleteFolderContents(folder) && folder.delete();
	}

	/**
	 * Delete folder's children files
	 * 
	 * @param folder
	 * @return
	 */
	public static boolean deleteFolderContents(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					if (!file.delete()) {
						return false;
					}
				} else {
					if (!deleteFolder(file)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public static BufferedImage getBufferedImage(String imagePath,
			boolean isRemote) throws Exception {
		return getBufferedImage(imagePath, isRemote, 0, 1 * 1000);
	}

	public static BufferedImage getBufferedImage(String imagePath)
			throws Exception {
		return getBufferedImage(imagePath, false, 0, 1 * 1000);
	}

	public static BufferedImage getBufferedImage(String imagePath,
			int retryTimes, long sleep) throws Exception {
		return getBufferedImage(imagePath, false, retryTimes, sleep);
	}

	/**
	 * 
	 * @param imagePath
	 *            给定的图片Path
	 * @param retryTimes
	 *            如果发生异常重试次数
	 * @param sleep
	 * @return
	 */
	public static BufferedImage getBufferedImage(String imagePath,
			boolean isRemote, int retryTimes, long sleep) throws Exception {
		if (imagePath == null || imagePath.trim().length() == 0)
			throw new Exception("image url can not be empty");

		int count = 0;
		while (true) {
			try {
				if (isRemote)
					return getRemote(imagePath);

				try {
					return getLocal(imagePath);
				} catch (Throwable e) {
					return getRemote(imagePath);
				}
			} catch (Throwable e) {
				if (count >= retryTimes) {
					throw new Exception(e);
				}
				Thread.sleep(sleep);
			}
			count++;
		}
	}

	private static BufferedImage getLocal(String imagePath) throws IOException {
		try {
			return toBufferedImage(Toolkit.getDefaultToolkit().getImage(
					imagePath));
		} catch (Throwable e) {
			return ImageIO.read(new File(imagePath));
		}
	}

	private static BufferedImage getRemote(String imagePath)
			throws MalformedURLException, IOException {
		URL url = new URL(imagePath.replace(" ", "%20"));
		try {
			return toBufferedImage(Toolkit.getDefaultToolkit().getImage(url));
		} catch (Throwable e1) {
			return ImageIO.read(url);
		}
	}

	public static boolean isFileExist(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	/**
	 * 将一个InputStream里的数据写入
	 * 
	 * @param path
	 *            目录
	 * @param fileName
	 *            文件名
	 * @param input
	 * @return
	 */
	public static File writeFromInput(String fileName, InputStream input) {
		File file = null;
		OutputStream output = null;
		try {
			file = new File(fileName);
			FileUtils.createFile(fileName, true);
			output = new FileOutputStream(file);// 根据file创建输出流
			byte buffer[] = new byte[4 * 1024];// 以4K为单位，每4K写一次
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();// 清空缓存
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see e661 Determining If an Image Has Transparent
		// Pixels
		// boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			/*
			 * if (hasAlpha) { transparency = Transparency.BITMASK; }
			 */

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null),
					image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			// int type = BufferedImage.TYPE_3BYTE_BGR;//by wang
			/*
			 * if (hasAlpha) { type = BufferedImage.TYPE_INT_ARGB; }
			 */
			bimage = new BufferedImage(image.getWidth(null),
					image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	public static boolean exists(String filePath) {
		File dir = new File(CommonUtils.uriDecoding(filePath));
		return dir.exists();
	}

	/**
	 * 返回某目录下所有文件对象
	 * 
	 * @param str
	 * @return
	 */
	public static File[] getFiles(String str) {
		File dir = new File(CommonUtils.uriDecoding(str));
		File[] result = null;
		if (dir.isDirectory()) {
			result = dir.listFiles();
		}
		return result;
	}

	public static List<File> getAllFiles(String str) {
		File dir = new File(CommonUtils.uriDecoding(str));
		List<File> result = new ArrayList<File>();
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					result.addAll(getAllFiles(file.getPath()));
				} else {
					result.add(file);
				}
			}
		}
		return result;
	}

	public static List<String> getFileNames(String str) {
		File[] files = getFiles(str);
		List<String> list = new ArrayList<String>();
		if (files != null) {
			for (File file : files)
				list.add(file.getName());
		}
		return list;
	}

	public static List<String> getFileNamesWithoutExt(String str) {
		File[] files = getFiles(str);
		List<String> list = new ArrayList<String>();
		if (files != null) {
			for (File file : files)
				list.add(getFileNameWithoutExt(file));
		}
		return list;
	}

	public static String getFileNameWithoutExt(File file) {
		if (file.getName().lastIndexOf(".") == -1)
			return file.getName();
		return file.getName().substring(0, file.getName().lastIndexOf("."));
	}

	/**
	 * 返回某个类所在包最顶层文件夹
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getTopClassPath(Class<?> clazz) {
		String path = CommonUtils.uriDecoding(clazz.getResource("/").getPath());

		return path;
	}

	public static void main(String[] args) throws Exception {
		// String c = FileUtils.readFile(new File("d:/seasky.lrc"));
		// System.out.println(c);
		// File file = new File("C:\\zhujiangang");
		// System.out.println(file.getName().lastIndexOf("."));
		String srcDir = "D:\\Research\\CodeGen\\template\\style";
		String dstDir = "D:\\TestProject\\fasdf\\fasdfa\\fasdfa";
		createDir(dstDir);
	}

	/**
	 * get the jars path
	 * 
	 * @return
	 */
	public static String getLib() {
		return CommonUtils.uriDecoding(FileUtils.getParent(
				FileUtils.getTopClassPath(FileUtils.class), 1)
				+ "lib");
	}

	public static String[] getChildrenPath(File parent) {
		File[] files = parent.listFiles();
		String[] result = new String[files.length];
		for (int i = 0; i < files.length; i++)
			result[i] = CommonUtils.uriDecoding(files[i].getAbsolutePath());

		return result;
	}

	public static Collection<String> getJars() {
		Collection<String> jars = new HashSet<String>();
		Enumeration<URL> urls;
		try {
			urls = FileUtils.class.getClassLoader().getResources(
					"META-INF/MANIFEST.MF");

			while (urls.hasMoreElements()) {
				URL url = (URL) urls.nextElement();
				String path = url.getFile().replace("file:/", "")
						.replace("!/META-INF/MANIFEST.MF", "");
				jars.add(CommonUtils.uriDecoding(path));
			}
			File jarDir = new File(getLib());
			if (jarDir.isDirectory() && jarDir.exists()) {
				for (File jar : jarDir.listFiles()) {
					jars.add(CommonUtils.uriDecoding(jar.getAbsolutePath()));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return jars;
	}

	public static String getClassPath(String folderName) {
		return getParent(getTopClassPath(FileUtils.class), 1) + folderName;
	}

	/**
	 * 获得类所在文件路径
	 * 
	 * @param clazz
	 * @return
	 */
	public static String getCurrPath(Class<?> clazz) {
		return CommonUtils.uriDecoding(clazz.getResource("/").getPath()
				+ clazz.getName().replace(".", File.separator));
	}

	/**
	 * 创建一个文件夹
	 * 
	 * @param path
	 * @return
	 */
	public static boolean createDir(String path) {
		boolean flag = false;
		File file = new File(CommonUtils.uriDecoding(path));
		String fatherDir = file.getAbsolutePath().substring(0,
				file.getAbsolutePath().lastIndexOf(File.separator));
		File father = new File(fatherDir);
		if (!father.exists())
			createDir(fatherDir);
		if (!file.isDirectory()) {
			flag = file.mkdir();
		}

		return flag;
	}

	public static boolean createDir(String path, boolean isDelete) {
		boolean flag = false;
		File file = new File(CommonUtils.uriDecoding(path));
		if (file.exists() && file.isDirectory()) {
			if (isDelete)
				deleteFolder(file);
		}
		String fatherDir = file.getAbsolutePath().substring(0,
				file.getAbsolutePath().lastIndexOf(File.separator));
		File father = new File(fatherDir);
		if (!father.exists())
			createDir(fatherDir);
		if (!file.isDirectory()) {
			flag = file.mkdir();
		}

		return flag;
	}

	/**
	 * 创建一个文件
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static boolean createFile(String path) throws IOException {
		return createFile(path, false);
	}

	/**
	 * 
	 * @param file
	 * @param isDelete
	 * @return
	 * @throws IOException
	 */
	public static boolean createFile(File file, boolean isDelete) {
		boolean flag = true;
		try {
			if (file.exists()) {
				if (isDelete) {
					file.delete();
					file.createNewFile();
				} else {
					flag = false;
				}
			} else {
				String dir = file.getAbsolutePath().substring(0,
						file.getAbsolutePath().lastIndexOf(File.separator));
				createDir(dir);
				file.createNewFile();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 
	 * @param path
	 * @param isDelete
	 * @return
	 * @throws IOException
	 */
	public static boolean createFile(String path, boolean isDelete)
			throws IOException {
		File file = new File(CommonUtils.uriDecoding(path));

		return createFile(file, isDelete);
	}

	/**
	 * 将oldFile移动到指定目录
	 * 
	 * @param oldFile
	 * @param newDir
	 * @return
	 */
	public static boolean moveFileTo(File oldFile, String newDir) {
		StringBuilder sb = new StringBuilder(newDir);
		sb.append(File.separator).append(oldFile.getName());
		File toDir = new File(CommonUtils.uriDecoding(sb.toString()));
		boolean flag = false;
		if (!toDir.exists()) {
			flag = oldFile.renameTo(toDir);
		}
		return flag;
	}

	/**
	 * 返回当前文件的上层文件夹路径（第几层由参数floor决定）
	 * 
	 * @param f
	 * @param floor
	 * @return
	 */
	public static String getParent(File f, int floor) {
		String result = "";
		if (f != null && f.exists()) {
			for (int i = 0; i < floor; ++i) {
				f = f.getParentFile();
			}

			if (f != null && f.exists()) {
				result = f.getPath();
			}
		}

		return CommonUtils.uriDecoding(result) + File.separator;
	}

	public static String getParent(String path, int floor) {
		return getParent(new File(path), floor);
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteFile(File file) {
		boolean flag = false;
		if (file != null && file.exists()) {
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					deleteFile(f);
				}
			}
			flag = file.delete();
		}

		return flag;
	}

	/**
	 * 检查文件名是否合法
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isValidFileName(String fileName) {
		if (fileName == null || fileName.length() > 255)
			return false;
		else {
			return fileName
					.matches("[^\\s\\\\/:\\*\\?\\\"<>\\|](\\x20|[^\\s\\\\/:\\*\\?\\\"<>\\|])*[^\\s\\\\/:\\*\\?\\\"<>\\|\\.]$");
		}
	}

	public static void copy(String srcDir, String dstDir) {
		srcDir = normalizeDir(srcDir);
		dstDir = normalizeDir(dstDir);
		List<File> files = getAllFiles(srcDir);
		for (File file : files) {
			File dstFile = new File(file.getAbsolutePath().replace(srcDir,
					dstDir));
			copy(file, dstFile);
		}
	}

	public static String normalizeDir(String dir) {
		dir = dir.replace("\\", File.separator).replace("/", File.separator);
		if (dir.endsWith(File.separator))
			dir = dir.substring(0, dir.length() - 1);
		return dir;

	}

	/**
	 * 复制文件
	 * 
	 * @param src
	 * @param dst
	 */
	public static void copy(File src, File dst) {
		if (!src.exists())
			return;
		InputStream in = null;
		OutputStream out = null;
		try {
			createFile(dst, true);
			in = new FileInputStream(src);
			out = new FileOutputStream(dst);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len = -1;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		return;
	}

}
