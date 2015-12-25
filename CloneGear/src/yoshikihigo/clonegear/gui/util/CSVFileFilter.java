package yoshikihigo.clonegear.gui.util;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CSVFileFilter extends FileFilter {

	@Override
	public boolean accept(File pathname) {

		if (pathname.isDirectory())
			return true;
		else if (pathname.isFile()) {
			String fileName = pathname.getName();
			return fileName.toLowerCase().endsWith(".csv");
		} else {
			return false;
		}
	}

	@Override
	public String getDescription() {
		return "*.csv";
	}
}
