package yoshikihigo.clonegear.gui.view.metric.source.path;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class FilePathView extends JTextField {

	public FilePathView(final String path) {

		this.setText(path);
		this.setBorder(new TitledBorder(new LineBorder(Color.black),
				"File Path"));
		this.setEditable(false);
	}
}
