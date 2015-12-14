package yoshikihigo.clonegear.gui.view.visual.filepath;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.SelectedEntities;

public class FilePathView extends JTextField implements Observer {

	public FilePathView() {
		this.setEditable(false);
	}

	public void update(Observable o, Object arg) {

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedFiles = (SelectedEntities) o;
			if (selectedFiles.isSet()) {
				final GUIFile file = (GUIFile) selectedFiles.get().get(0);
				this.setText(file.path);
			} else {
				this.setText("");
			}

			this.repaint();
		}
	}
}
