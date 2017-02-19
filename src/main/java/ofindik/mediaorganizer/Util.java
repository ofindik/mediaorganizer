package ofindik.mediaorganizer;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class Util {

	public static void getFileList (String directoryName, ArrayList<File> files) {
		File directory = new File (directoryName);

		File[] fileList = directory.listFiles ();
		if (null != fileList) {
			for (File file : fileList) {
				if (file.isFile ()) {
					files.add (file);
				} else if (file.isDirectory ()) {
					getFileList (file.getAbsolutePath (), files);
				}
			}
		} else {
			System.out.println ("No source file found!");
		}
	}

	public static boolean checkPhotoExtension (File file) {
		String fileName = file.getName ();
		// System.out.println ("fileName:" + fileName);
		String[] fileNameArr = fileName.split ("\\.");
		if (fileNameArr.length == 2) {
			String extension = fileNameArr[1];
			if (extension.equalsIgnoreCase ("jpg")
				|| extension.equalsIgnoreCase ("jpeg") || extension.equalsIgnoreCase ("png")) {
				return true;
			}
		}
		System.out.println ("File ignored:" + fileName);
		return false;
	}

	public static boolean checkVideoExtension (File file) {
		String fileName = file.getName ();
		// System.out.println ("fileName:" + fileName);
		String[] fileNameArr = fileName.split ("\\.");
		if (fileNameArr.length == 2) {
			String extension = fileNameArr[1];
			if (extension.equalsIgnoreCase ("mp4") || extension.equalsIgnoreCase ("mov")) {
				return true;
			}
		}
		System.out.println ("File ignored:" + fileName);
		return false;
	}

	public static Date getOriginalDate (File file) throws Exception {
		//System.out.println (file.getAbsolutePath ());

		Date originalDate = null;
		try {
			Metadata metadata = ImageMetadataReader.readMetadata (new FileInputStream (file.getAbsolutePath ()));
			ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType (ExifSubIFDDirectory.class);
			if (null != directory) {
				originalDate = directory.getDate (ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			}
		} catch (Exception e) {
			System.out.println ("Get date failed:" + e.getMessage ());
		}
		if (null == originalDate) {
			long lastModified = file.lastModified ();
			originalDate = new Date (lastModified);
		}

		return originalDate;
	}

	public static void renameFile (File file, String destFolder, String testMode) throws Exception {
		Date fileDate = Util.getOriginalDate (file);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
		String targetPath = destFolder + "/" + simpleDateFormat.format (fileDate) + "/";
		// System.out.println ("targetPath:" + targetPath);
		File targetDir = new File (targetPath);
		System.out.println (file.getPath () + " will be renamed to " + targetPath + file.getName ());
		if (!testMode.equals ("true")) {
			if (!targetDir.exists ()) {
				if (!targetDir.mkdirs ()) {
					System.out.println ("mkdirs failed for:" + targetPath);
				}
			}
			if (!file.renameTo (new File (targetPath + file.getName ()))) {
				System.out.println ("renameTo failed for:" + targetPath + file.getName ());
			}
		}
	}
}
