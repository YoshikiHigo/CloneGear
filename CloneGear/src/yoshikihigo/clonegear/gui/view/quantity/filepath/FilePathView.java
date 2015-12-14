package yoshikihigo.clonegear.gui.view.quantity.filepath;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class FilePathView extends JTextField implements Observer,
		QuantitativeViewInterface {

	public FilePathView() {
		this.setBorder(new TitledBorder(new LineBorder(Color.black),
				"File Path"));
		this.setEditable(false);
	}

	@Override
	public void update(final Observable o, final Object arg) {

		if (o instanceof SelectedEntities) {

			SelectedEntities selectedFiles = (SelectedEntities) o;

			if (selectedFiles.getLabel().equals(SELECTED_FILE)) {

				if (selectedFiles.isSet()) {
					final GUIFile file = (GUIFile) selectedFiles.get().get(0);
					this.setText(file.path);
				} else {
					this.setText("");
				}

				this.repaint();

			} else if (selectedFiles.getLabel().equals(RELATED_FILE)) {

			} else if (selectedFiles.getLabel().equals(GROUP)) {
				this.setText("");
				this.repaint();
			}

		} else if (o instanceof RNRValue) {

			final RNRValue rnrValue = (RNRValue) o;
			if (rnrValue.getLabel().equals(RNR)) {
				this.repaint();
			}
		}
	}
}
