package yoshikihigo.clonegear.gui.view.visual.filelist;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import yoshikihigo.clonegear.gui.MessagePrinter;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.util.RNRValue;
import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.ViewColors;
import yoshikihigo.clonegear.gui.view.ViewScale;
import yoshikihigo.clonegear.gui.view.visual.DIRECTION;
import yoshikihigo.clonegear.gui.view.visual.VisualViewInterface;

class FileListViewRenderer extends JLabel implements TableCellRenderer,
		ViewColors, VisualViewInterface {

	final private DIRECTION direction;

	public FileListViewRenderer(final DIRECTION direction) {
		this.direction = direction;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		this.setOpaque(true);

		if (isSelected) {

			switch (this.direction) {
			case HORIZONTAL: {
				this.setBackground(VISUAL_HORIZONTAL_COLOR);
				this.setForeground(VISUAL_FILELISTVIEW_SELECTED_FOREGROUND_COLOR);
				break;
			}
			case VERTICAL: {
				this.setBackground(VISUAL_VERTICAL_COLOR);
				this.setForeground(VISUAL_FILELISTVIEW_SELECTED_FOREGROUND_COLOR);
				break;
			}
			}

		} else {
			this.setBackground(VISUAL_FILELISTVIEW_UNSELECTED_BACKGROUND_COLOR);
			this.setForeground(VISUAL_FILELISTVIEW_UNSELECTED_FOREGROUND_COLOR);
		}

		final int modelRow = table.convertRowIndexToModel(row);
		final int modelColumn = table.convertColumnIndexToModel(column);
		final FileListViewModel model = (FileListViewModel) table.getModel();
		final GUIFile file = model.getFile(modelRow);
		final boolean display = UninterestingClonesDisplay.getInstance(
				UNINTERESTING).isDisplay();
		final int threshold = RNRValue.getInstance(RNR).get();
		switch (modelColumn) {
		case FileListViewModel.COL_NAME: {
			this.setHorizontalAlignment(JLabel.LEFT);
			this.setText(file.getFileName());
			break;
		}
		case FileListViewModel.COL_LOC: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			this.setText(Integer.toString(file.loc));
			break;
		}
		case FileListViewModel.COL_NOC: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			if (display) {
				final StringBuilder sb = new StringBuilder();
				sb.append(file.getNOC(threshold));
				sb.append("(");
				sb.append(file.getNOC());
				sb.append(")");
				this.setText(sb.toString());
			} else {
				this.setText(Integer.toString(file.getNOC(threshold)));
			}
			break;
		}
		case FileListViewModel.COL_ROC: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			if (display) {
				final StringBuilder sb = new StringBuilder();
				sb.append(ViewScale.ROC_FORMAT.format(file.getROC(threshold)));
				sb.append("(");
				sb.append(ViewScale.ROC_FORMAT.format(file.getROC()));
				sb.append(")");
				this.setText(sb.toString());
			} else {
				this.setText(ViewScale.ROC_FORMAT.format(file.getROC(threshold)));
			}
			break;
		}
		case FileListViewModel.COL_NOF: {
			this.setHorizontalAlignment(JLabel.RIGHT);
			if (display) {
				final StringBuilder sb = new StringBuilder();
				sb.append(file.getNOF(threshold));
				sb.append("(");
				sb.append(file.getNOF());
				sb.append(")");
				this.setText(sb.toString());
			} else {
				this.setText(Integer.toString(file.getNOF(threshold)));
			}
			break;
		}
		default:
			MessagePrinter.ERR.println("Here shouldn't be reached!");
		}

		return this;
	}
}
