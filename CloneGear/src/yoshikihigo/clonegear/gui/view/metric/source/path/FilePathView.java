package yoshikihigo.clonegear.gui.view.metric.source.path;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class FilePathView extends JTextField {

	public FilePathView(final String path) {

		this.setText(path);

		this.setBorder(new TitledBorder(new LineBorder(java.awt.Color.black),
				"File Path"));
		this.setEditable(false);
	}
}
