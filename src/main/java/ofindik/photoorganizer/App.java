package ofindik.photoorganizer;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class App {
	public static void main (String[] args) throws Exception {
		String sourceFolder = args[0];
		String destFolder = args[1];

		App app = new App ();
		app.process (sourceFolder, destFolder);
		System.out.println ("Process finished.");
	}

	private void process (String sourceFolder, String destFolder) throws Exception {
		ArrayList<File> fileList = new ArrayList<File> ();
		getFileList (sourceFolder, fileList);
		for (File file : fileList) {
			if (!checkExtension (file)) {
				continue;
			}
			Date fileDate = getOriginalDate (file);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd");
			String targetPath = destFolder + "/" + simpleDateFormat.format (fileDate) + "/";
			System.out.println ("targetPath:" + targetPath);
			File targetDir = new File (targetPath);
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

	private void getFileList (String directoryName, ArrayList<File> files) {
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

	private boolean checkExtension (File file) {
		String fileName = file.getName ();
		System.out.println ("fileName:" + fileName);
		String[] fileNameArr = fileName.split ("\\.");
		if (fileNameArr.length == 2) {
			String extension = fileNameArr[1];
			if (extension.equalsIgnoreCase ("jpg")
				|| extension.equalsIgnoreCase ("jpeg") || extension.equalsIgnoreCase ("png")
				|| extension.equalsIgnoreCase ("mp4") || extension.equalsIgnoreCase ("mov")) {
				return true;
			}
		}
		System.out.println ("File ignored:" + fileName);
		return false;
	}

	private Date getOriginalDate (File file) throws Exception {
		System.out.println (file.getAbsolutePath ());

		Date originalDate = null;
		try {
			Metadata metadata =
				ImageMetadataReader.readMetadata (new FileInputStream (file.getAbsolutePath ()));
			ExifSubIFDDirectory directory =
				metadata.getFirstDirectoryOfType (ExifSubIFDDirectory.class);
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
}
