package yoshikihigo.clonegear.gui.util;

import java.net.URL;

import javax.swing.ImageIcon;

import yoshikihigo.clonegear.gui.MessagePrinter;

public class ImageUtility {

	public static ImageIcon createImageIcon(final String path,
			final String description) {

		final URL imgURL = ImageUtility.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		}

		MessagePrinter.ERR.println("Couldn't find file: " + path);
		return null;
	}
}
