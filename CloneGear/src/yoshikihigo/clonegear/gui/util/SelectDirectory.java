package yoshikihigo.clonegear.gui.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SelectDirectory {

	static final private String CONFIGFILE = ".last_directory.txt";

	public static String getLastDirectory() {

		File configFile = new File(CONFIGFILE);

		if (configFile.isFile()) {

			try {

				BufferedReader bf = new BufferedReader(new FileReader(
						configFile));

				String lastDirectory = bf.readLine();
				bf.close();

				return lastDirectory;

			} catch (IOException e) {

				System.out.println(e.getMessage());
				String home = (new File(System.getProperty("user.home")))
						.getAbsolutePath();
				return home;
			}

		} else {
			String home = (new File(System.getProperty("user.home")))
					.getAbsolutePath();
			return home;
		}
	}

	static public void setLastDirectory(String lastDirectory) {

		try {

			BufferedWriter bf = new BufferedWriter(new FileWriter(CONFIGFILE));
			bf.write(lastDirectory);

			bf.close();

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
