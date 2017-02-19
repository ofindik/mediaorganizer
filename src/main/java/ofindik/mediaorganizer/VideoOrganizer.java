package ofindik.mediaorganizer;

import java.io.File;
import java.util.ArrayList;

public class VideoOrganizer {

	private String sourceFolder;

	private String destFolder;

	private String testMode;

	public static void main (String[] args) throws Exception {

		VideoOrganizer photoOrganizer = new VideoOrganizer (args);
		photoOrganizer.process ();
		System.out.println ("Process finished.");
	}

	public VideoOrganizer (String[] args) {
		this.sourceFolder = args[0];
		this.destFolder = args[1];
		this.testMode = args[2];
	}

	private void process () throws Exception {
		ArrayList<File> fileList = new ArrayList<File> ();
		Util.getFileList (sourceFolder, fileList);
		for (File file : fileList) {
			if (!Util.checkVideoExtension (file)) {
				continue;
			}
			Util.renameFile (file, destFolder, testMode);
		}
	}

}
