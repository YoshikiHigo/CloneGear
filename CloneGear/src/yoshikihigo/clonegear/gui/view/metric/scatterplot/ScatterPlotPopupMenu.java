package yoshikihigo.clonegear.gui.view.metric.scatterplot;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;


public class ScatterPlotPopupMenu extends JPopupMenu {

    public ScatterPlotPopupMenu(final ScatterPlotPanel parentContainer) {

        final JMenu displayMenu = new JMenu("Display");
        this.fileSeparetorMenuItem = new JCheckBoxMenuItem("File Separator", true);
        this.groupSeparetorMenuItem = new JCheckBoxMenuItem("Group Separator", true);
        this.coordinateLineMenuItem = new JCheckBoxMenuItem("Coordinate Line", true);

        final JMenu formatMenu = new JMenu("Format");
        this.squareMenuItem = new JRadioButtonMenuItem("square", true);
        this.rectangleMenuItem = new JRadioButtonMenuItem("rectangle", false);
        final ButtonGroup formatGroup = new ButtonGroup();
        formatGroup.add(this.squareMenuItem);
        formatGroup.add(this.rectangleMenuItem);

        final JMenu sortMenu = new JMenu("Clone Sets are sorted by");
        this.idSortMenuItem = new JRadioButtonMenuItem("ID", true);
        this.firstPositionSortMenuItem = new JRadioButtonMenuItem(
                "position of first code fragment", false);
        this.lastPositionSortMenuItem = new JRadioButtonMenuItem("position of last code fragment",
                false);
        this.middlePositionSortMenuItem = new JRadioButtonMenuItem(
                "median point of code fragments", false);
        this.rangeSortMenuItem = new JRadioButtonMenuItem(
                "range between first and last code fragments", false);
        this.nifSortMenuItem = new JRadioButtonMenuItem(
                "the number of files involving code fragments", false);

        final ButtonGroup sortGroup = new ButtonGroup();
        sortGroup.add(this.idSortMenuItem);
        sortGroup.add(this.firstPositionSortMenuItem);
        sortGroup.add(this.lastPositionSortMenuItem);
        sortGroup.add(this.middlePositionSortMenuItem);
        sortGroup.add(this.rangeSortMenuItem);
        sortGroup.add(this.nifSortMenuItem);

        final JMenu exportMenu = new JMenu("Export");
        this.imageMenuItem = new JMenuItem("Scatter Plot Image");

        displayMenu.add(this.fileSeparetorMenuItem);
        displayMenu.add(this.groupSeparetorMenuItem);
        displayMenu.add(this.coordinateLineMenuItem);

        formatMenu.add(this.squareMenuItem);
        formatMenu.add(this.rectangleMenuItem);

        sortMenu.add(this.idSortMenuItem);
        sortMenu.add(this.firstPositionSortMenuItem);
        sortMenu.add(this.lastPositionSortMenuItem);
        sortMenu.add(this.middlePositionSortMenuItem);
        sortMenu.add(this.rangeSortMenuItem);
        sortMenu.add(this.nifSortMenuItem);

        exportMenu.add(this.imageMenuItem);

        this.add(displayMenu);
        this.add(formatMenu);
        this.add(sortMenu);
        this.add(exportMenu);

        this.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
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

        this.coordinateLineMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentContainer.repaint();
            }
        });

        this.squareMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentContainer.repaint();
            }
        });

        this.rectangleMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentContainer.repaint();
            }
        });

        this.idSortMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentContainer.repaint();
            }
        });

        this.firstPositionSortMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentContainer.repaint();
            }
        });

        this.lastPositionSortMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentContainer.repaint();
            }
        });

        this.middlePositionSortMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentContainer.repaint();
            }
        });

        this.rangeSortMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentContainer.repaint();
            }
        });

        this.nifSortMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentContainer.repaint();
            }
        });

        this.imageMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                parentContainer.outputScatterPlotImage();
            }
        });
    }

    public boolean getStateFileSeparetor() {
        return this.fileSeparetorMenuItem.isSelected();
    }

    public boolean getStateGroupSeparetor() {
        return this.groupSeparetorMenuItem.isSelected();
    }

    public boolean getStateCoordinateLine() {
        return this.coordinateLineMenuItem.isSelected();
    }

    public boolean getStateSquare() {
        return this.squareMenuItem.isSelected();
    }

    public boolean getStateRectangle() {
        return this.rectangleMenuItem.isSelected();
    }

    public boolean getStateIDSort() {
        return this.idSortMenuItem.isSelected();
    }

    public boolean getStateFirstPositionSort() {
        return this.firstPositionSortMenuItem.isSelected();
    }

    public boolean getStateLastPositionSort() {
        return this.lastPositionSortMenuItem.isSelected();
    }

    public boolean getStateMiddlePositionSort() {
        return this.middlePositionSortMenuItem.isSelected();
    }

    public boolean getStateRangeSort() {
        return this.rangeSortMenuItem.isSelected();
    }

    public boolean getStateNIFSort() {
        return this.nifSortMenuItem.isSelected();
    }

    final private JCheckBoxMenuItem fileSeparetorMenuItem;

    final private JCheckBoxMenuItem groupSeparetorMenuItem;

    final private JCheckBoxMenuItem coordinateLineMenuItem;

    final private JRadioButtonMenuItem squareMenuItem;

    final private JRadioButtonMenuItem rectangleMenuItem;

    final private JMenuItem imageMenuItem;

    final private JRadioButtonMenuItem idSortMenuItem;

    final private JRadioButtonMenuItem firstPositionSortMenuItem;

    final private JRadioButtonMenuItem lastPositionSortMenuItem;

    final private JRadioButtonMenuItem middlePositionSortMenuItem;

    final private JRadioButtonMenuItem rangeSortMenuItem;

    final private JRadioButtonMenuItem nifSortMenuItem;
}
