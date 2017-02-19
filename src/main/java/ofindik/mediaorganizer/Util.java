package ofindik.mediaorganizer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
	    System.out.println ("fileName:" + fileName);
		String[] fileNameArr = fileName.split ("\\.");
		if (fileNameArr.length == 2) {
			String extension = fileNameArr[1];
			if (extension.equalsIgnoreCase ("mp4")
				|| extension.equalsIgnoreCase ("mov") || extension.equalsIgnoreCase ("avi")
				|| extension.equalsIgnoreCase ("mpg") || extension.equalsIgnoreCase ("3gp")) {
				return true;
			}
		}
		System.out.println ("File ignored:" + fileName);
		return false;
	}

	public static Date getOriginalDate (File file, boolean useLastModifiedDate) throws Exception {
		//System.out.println (file.getAbsolutePath ());

		Date originalDate = null;
		try {
			Metadata metadata = ImageMetadataReader.readMetadata (new FileInputStream (file.getAbsolutePath ()));
			ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType (ExifSubIFDDirectory.class);
			if (null != directory) {
				originalDate = directory.getDate (ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
			}
		} catch (Exception e) {
			//System.out.println ("Get date failed:" + e.getMessage ());
			if (!useLastModifiedDate) {
				throw e;
			}
		}
		if (null == originalDate) {
			long lastModified = file.lastModified ();
			originalDate = new Date (lastModified);
		}

		return originalDate;
	}

	public static void renameFile (File file, String destFolder, boolean testMode, boolean useLastModifiedDate) {
		try {
			Date fileDate = Util.getOriginalDate (file, useLastModifiedDate);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
			String dateStr = simpleDateFormat.format (fileDate);
			if (dateStr.startsWith ("0")) {
				throw new Exception ("Invalid date string:" + dateStr);
			}
			String targetPath = destFolder + "/" + dateStr + "/";
			// System.out.println ("targetPath:" + targetPath);
			File targetDir = new File (targetPath);
			System.out.println (file.getPath () + " will be moved to " + targetPath + file.getName ());
			if (!testMode) {
				if (!targetDir.exists ()) {
					if (!targetDir.mkdirs ()) {
						System.out.println ("mkdirs failed for:" + targetPath);
					}
				}

				Path movefrom = FileSystems.getDefault ().getPath (file.getPath ());
				Path target = FileSystems.getDefault ().getPath (targetPath + file.getName ());
				Files.move (movefrom, target, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			System.err.println (e);
		}
	}
}
