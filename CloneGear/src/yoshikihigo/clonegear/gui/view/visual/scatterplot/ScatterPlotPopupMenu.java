package yoshikihigo.clonegear.gui.view.visual.scatterplot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileLabelManager;
import yoshikihigo.clonegear.gui.data.file.IDIndexMap;

public class ScatterPlotPopupMenu extends JPopupMenu {

	public ScatterPlotPopupMenu(final ScatterPlotPanel parentContainer,
			final List<Area> historyList) {

		this.overViewMenuItem = new JMenuItem("Reset To Overview");
		final JMenu zoomMenu = new JMenu("Zoom");
		this.zoomManuallyMenuItem = new JMenuItem("Manually");
		this.zoomThisFileMenuItem = new JMenuItem("This File(not implemented)");
		this.zoomThisGroupMenuItem = new JMenuItem(
				"This Group(not implemented)");
		final JMenu exportMenu = new JMenu("Export");
		this.imageMenuItem = new JMenuItem("Scatter Plot Image");
		final JMenu displayMenu = new JMenu("Display");
		this.fileSeparetorMenuItem = new JCheckBoxMenuItem("File Separator",
				true);
		this.groupSeparetorMenuItem = new JCheckBoxMenuItem("Group Separator",
				true);
		this.diagonalLineMenuItem = new JCheckBoxMenuItem("Diagonal Line", true);
		this.numericalInformationMenuItem = new JCheckBoxMenuItem(
				"Numerical Information", true);

		this.historyMenu = new JMenu("History");

		final JMenu granularityMenu = new JMenu("Drawing Granularity");
		this.autoGranularityMenuItem = new JRadioButtonMenuItem("auto", true);
		this.fineGranularityMenuItem = new JRadioButtonMenuItem("fine", false);
		this.roughGranularityMenuItem = new JRadioButtonMenuItem("rough", false);
		final ButtonGroup granularityGroup = new ButtonGroup();
		granularityGroup.add(this.autoGranularityMenuItem);
		granularityGroup.add(this.fineGranularityMenuItem);
		granularityGroup.add(this.roughGranularityMenuItem);

		final JMenu scaleMenu = new JMenu("Scale Factor");
		this.scale1MenuItem = new JRadioButtonMenuItem("x 1", false);
		this.scale2MenuItem = new JRadioButtonMenuItem("x 2", true);
		this.scale3MenuItem = new JRadioButtonMenuItem("x 3", false);
		this.scale4MenuItem = new JRadioButtonMenuItem("x 4", false);
		this.scale5MenuItem = new JRadioButtonMenuItem("x 5", false);
		final ButtonGroup scaleGroup = new ButtonGroup();
		scaleGroup.add(this.scale1MenuItem);
		scaleGroup.add(this.scale2MenuItem);
		scaleGroup.add(this.scale3MenuItem);
		scaleGroup.add(this.scale4MenuItem);
		scaleGroup.add(this.scale5MenuItem);

		zoomMenu.add(this.zoomManuallyMenuItem);
		zoomMenu.add(this.zoomThisFileMenuItem);
		zoomMenu.add(this.zoomThisGroupMenuItem);

		exportMenu.add(this.imageMenuItem);

		displayMenu.add(this.fileSeparetorMenuItem);
		displayMenu.add(this.groupSeparetorMenuItem);
		displayMenu.add(this.diagonalLineMenuItem);
		displayMenu.add(this.numericalInformationMenuItem);

		granularityMenu.add(this.autoGranularityMenuItem);
		granularityMenu.add(scaleMenu);
		granularityMenu.add(new JSeparator());
		granularityMenu.add(this.fineGranularityMenuItem);
		granularityMenu.add(this.roughGranularityMenuItem);

		scaleMenu.add(this.scale1MenuItem);
		scaleMenu.add(this.scale2MenuItem);
		scaleMenu.add(this.scale3MenuItem);
		scaleMenu.add(this.scale4MenuItem);
		scaleMenu.add(this.scale5MenuItem);

		this.add(this.overViewMenuItem);
		this.add(zoomMenu);
		this.add(exportMenu);
		this.add(displayMenu);
		this.add(granularityMenu);
		this.add(this.historyMenu);

		this.addPopupMenuListener(new PopupMenuListener() {

			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				ScatterPlotPopupMenu.this.historyMenu.removeAll();
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

				for (java.util.Iterator<Area> historyIterator = historyList
						.iterator(); historyIterator.hasNext();) {
					Area area = historyIterator.next();
					final int startIndex = area.startIndex;
					final int interruptIndex = area.interruptIndex;
					final int restartIndex = area.restartIndex;
					final int endIndex = area.endIndex;

					if (interruptIndex == -1) {

						final GUIFile startElement = IDIndexMap.instance()
								.getFile(startIndex);
						final GUIFile endElement = IDIndexMap.instance()
								.getFile(endIndex);

						JMenuItem historyMenuItem = new JMenuItem(
								GUIFileLabelManager.SINGLETON
										.getIDLabel(startElement)
										+ " --- "
										+ GUIFileLabelManager.SINGLETON
												.getIDLabel(endElement));
						historyMenu.add(historyMenuItem);
						historyMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								parentContainer.zoomUp(startIndex, endIndex);
							}
						});

					} else {

						GUIFile startElement = IDIndexMap.instance().getFile(
								startIndex);
						GUIFile interruptElement = IDIndexMap.instance()
								.getFile(interruptIndex);
						GUIFile restartElement = IDIndexMap.instance().getFile(
								restartIndex);
						GUIFile endElement = IDIndexMap.instance().getFile(
								endIndex);

						JMenuItem historyMenuItem = new JMenuItem(
								GUIFileLabelManager.SINGLETON
										.getIDLabel(startElement)
										+ " -- "
										+ GUIFileLabelManager.SINGLETON
												.getIDLabel(interruptElement)
										+ " __ "
										+ GUIFileLabelManager.SINGLETON
												.getIDLabel(restartElement)
										+ " --- "
										+ GUIFileLabelManager.SINGLETON
												.getIDLabel(endElement));
						historyMenu.add(historyMenuItem);
						historyMenuItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								parentContainer.zoomUp(startIndex,
										interruptIndex, restartIndex, endIndex);
							}
						});
					}
				}
			}
		});

		this.overViewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.reset();
			}
		});

		this.zoomManuallyMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.setZoomMode(true);
			}
		});

		this.zoomThisFileMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});

		this.zoomThisGroupMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});

		this.imageMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.outputScatterPlotImage();
			}
		});

		this.groupSeparetorMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
			}
		});

		this.fileSeparetorMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
			}
		});

		this.diagonalLineMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
			}
		});

		this.numericalInformationMenuItem
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						parentContainer.repaint();
					}
				});

		this.autoGranularityMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
				if (autoGranularityMenuItem.isSelected()) {
					scale1MenuItem.setEnabled(true);
					scale2MenuItem.setEnabled(true);
					scale3MenuItem.setEnabled(true);
					scale4MenuItem.setEnabled(true);
					scale5MenuItem.setEnabled(true);
				} else {
					scale1MenuItem.setEnabled(false);
					scale2MenuItem.setEnabled(false);
					scale3MenuItem.setEnabled(false);
					scale4MenuItem.setEnabled(false);
					scale5MenuItem.setEnabled(false);
				}
			}
		});

		this.fineGranularityMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
				if (autoGranularityMenuItem.isSelected()) {
					scale1MenuItem.setEnabled(true);
					scale2MenuItem.setEnabled(true);
					scale3MenuItem.setEnabled(true);
					scale4MenuItem.setEnabled(true);
					scale5MenuItem.setEnabled(true);
				} else {
					scale1MenuItem.setEnabled(false);
					scale2MenuItem.setEnabled(false);
					scale3MenuItem.setEnabled(false);
					scale4MenuItem.setEnabled(false);
					scale5MenuItem.setEnabled(false);
				}
			}
		});

		this.roughGranularityMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
				if (autoGranularityMenuItem.isSelected()) {
					scale1MenuItem.setEnabled(true);
					scale2MenuItem.setEnabled(true);
					scale3MenuItem.setEnabled(true);
					scale4MenuItem.setEnabled(true);
					scale5MenuItem.setEnabled(true);
				} else {
					scale1MenuItem.setEnabled(false);
					scale2MenuItem.setEnabled(false);
					scale3MenuItem.setEnabled(false);
					scale4MenuItem.setEnabled(false);
					scale5MenuItem.setEnabled(false);
				}
			}
		});

		this.scale1MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
			}
		});

		this.scale2MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
			}
		});

		this.scale3MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
			}
		});

		this.scale4MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
			}
		});

		this.scale5MenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parentContainer.repaint();
			}
		});
	}

	public boolean getStateFileSeparetor() {
		return this.fileSeparetorMenuItem.isSelected();
	}

	public boolean getStateGroupSeparetor() {
		return this.groupSeparetorMenuItem.isSelected();
	}

	public boolean getStateDiagonalLine() {
		return this.diagonalLineMenuItem.isSelected();
	}

	public boolean getStateNumericalInformation() {
		return this.numericalInformationMenuItem.isSelected();
	}

	public boolean isAutoGranularity() {
		return this.autoGranularityMenuItem.isSelected();
	}

	public boolean isFineGranularity() {
		return this.fineGranularityMenuItem.isSelected();
	}

	public boolean isRoughGranularity() {
		return this.roughGranularityMenuItem.isSelected();
	}

	public int getScaleFactor() {
		if (this.scale1MenuItem.isSelected()) {
			return 1;
		} else if (this.scale2MenuItem.isSelected()) {
			return 2;
		} else if (this.scale3MenuItem.isSelected()) {
			return 3;
		} else if (this.scale4MenuItem.isSelected()) {
			return 4;
		} else if (this.scale5MenuItem.isSelected()) {
			return 5;
		}
		return 1;
	}

	final private JMenuItem overViewMenuItem;

	final private JMenuItem zoomManuallyMenuItem;

	final private JMenuItem zoomThisFileMenuItem;

	final private JMenuItem zoomThisGroupMenuItem;

	final private JMenuItem imageMenuItem;

	final private JCheckBoxMenuItem fileSeparetorMenuItem;

	final private JCheckBoxMenuItem groupSeparetorMenuItem;

	final private JCheckBoxMenuItem diagonalLineMenuItem;

	final private JCheckBoxMenuItem numericalInformationMenuItem;

	final private JRadioButtonMenuItem autoGranularityMenuItem;

	final private JRadioButtonMenuItem fineGranularityMenuItem;

	final private JRadioButtonMenuItem roughGranularityMenuItem;

	private final JRadioButtonMenuItem scale1MenuItem;

	private final JRadioButtonMenuItem scale2MenuItem;

	private final JRadioButtonMenuItem scale3MenuItem;

	private final JRadioButtonMenuItem scale4MenuItem;

	private final JRadioButtonMenuItem scale5MenuItem;

	final private JMenu historyMenu;
}
