//this is inner class for path comparing
package yoshikihigo.clonegear.gui.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import yoshikihigo.clonegear.gui.data.file.GUIFile;

public class PathCompare {

	private static Logger logger = Logger.getLogger("PathCompare");

	// this method return index which at path[] are first diferent
	public static int getDifferentIndex(String[] path) {

		logger.log(Level.FINEST, "begin");

		// get number of elements
		int elementNum = path.length;
		int index;

		for (index = 0; index < path[0].length(); index++) {

			char c = path[0].charAt(index);

			for (int i = 1; i < elementNum; i++) {
				if (path[i].length() <= index)
					return index;
				else if (c != path[i].charAt(index))
					return index;
			}
		}

		logger.log(Level.FINEST, "end");

		return index;
	}

	public static int countHierarchy(int index, String path) {

		logger.log(Level.FINEST, "begin");

		int pathLength = path.length();
		int count = 0;

		if (index == pathLength)
			return 0;
		else
			count++;

		for (int i = index; i < pathLength; i++) {
			if (path.charAt(i) == File.separatorChar)
				count++;
		}

		logger.log(Level.FINEST, "end");

		return count;
	}

	public static String getRootFilePath(final Collection<GUIFile> files) {

		final List<String> pathList = new ArrayList<String>();
		for (GUIFile file : files) {
			pathList.add(file.path);
		}

		final String[] paths = pathList.toArray(new String[0]);
		if (paths.length == 0) {
			return "";
		}

		final int differentIndex = getDifferentIndex(paths);
		final String commonString = paths[0].substring(0, differentIndex);
		final int lastSeparatorIndex = commonString
				.lastIndexOf(File.separatorChar);
		return commonString.substring(0, lastSeparatorIndex);
	}
}
