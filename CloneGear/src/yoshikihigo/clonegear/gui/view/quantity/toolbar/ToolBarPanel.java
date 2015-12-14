package yoshikihigo.clonegear.gui.view.quantity.toolbar;

import java.awt.Color;
import java.awt.FlowLayout;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

import yoshikihigo.clonegear.gui.util.UninterestingClonesDisplay;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeAnalysisView;
import yoshikihigo.clonegear.gui.view.quantity.QuantitativeViewInterface;

public class ToolBarPanel extends JPanel implements QuantitativeViewInterface,
		Observer {

	public ToolBarPanel(final QuantitativeAnalysisView parentContainer) {

		super(new FlowLayout(FlowLayout.LEFT));

		{
			final JPanel groupPanelShowHide = new JPanel(new FlowLayout(
					FlowLayout.LEFT));
			final JLabel name = new JLabel("Group Selecton Panel");
			this.groupPanelShowButton = new JRadioButton("show", true);
			this.groupPanelHideButton = new JRadioButton("hide", false);
			final ButtonGroup groupPanelButtonGroup = new ButtonGroup();
			groupPanelButtonGroup.add(this.groupPanelShowButton);
			groupPanelButtonGroup.add(this.groupPanelHideButton);
			groupPanelShowHide.add(name);
			groupPanelShowHide.add(this.groupPanelShowButton);
			groupPanelShowHide.add(this.groupPanelHideButton);
			groupPanelShowHide.setBorder(new LineBorder(Color.black));
			this.add(groupPanelShowHide);

			this.groupPanelShowButton.addActionListener(e -> {
				if (groupPanelShowButton.isSelected()) {
					parentContainer.showGroupPanel();
				}
			});

			this.groupPanelHideButton.addActionListener(e -> {
				if (groupPanelHideButton.isSelected()) {
					parentContainer.hideGroupPanel();
				}
			});
		}

		{
			final JPanel groupPanelShowHide = new JPanel(new FlowLayout(
					FlowLayout.LEFT));
			final JLabel name = new JLabel("File Selecton Panel");
			this.filePanelShowButton = new JRadioButton("show", true);
			this.filePanelHideButton = new JRadioButton("hide", false);
			final ButtonGroup groupPanelButtonGroup = new ButtonGroup();
			groupPanelButtonGroup.add(this.filePanelShowButton);
			groupPanelButtonGroup.add(this.filePanelHideButton);
			groupPanelShowHide.add(name);
			groupPanelShowHide.add(this.filePanelShowButton);
			groupPanelShowHide.add(this.filePanelHideButton);
			groupPanelShowHide.setBorder(new LineBorder(Color.black));
			this.add(groupPanelShowHide);

			this.filePanelShowButton.addActionListener(e -> {
				if (filePanelShowButton.isSelected()) {
					parentContainer.showFilePanel();
				}
			});

			this.filePanelHideButton.addActionListener(e -> {
				if (filePanelHideButton.isSelected()) {
					parentContainer.hideFilePanel();
				}
			});
		}

		{
			final JPanel codeFragmentPanelShowHide = new JPanel(new FlowLayout(
					FlowLayout.LEFT));
			final JLabel name = new JLabel("Code Fragment Panel");
			this.codeFragmentPanelShowButton = new JRadioButton("show", true);
			this.codeFragmentPanelHideButton = new JRadioButton("hide", false);
			final ButtonGroup codeFragmentPanelButtonGroup = new ButtonGroup();
			codeFragmentPanelButtonGroup.add(this.codeFragmentPanelShowButton);
			codeFragmentPanelButtonGroup.add(this.codeFragmentPanelHideButton);
			codeFragmentPanelShowHide.add(name);
			codeFragmentPanelShowHide.add(this.codeFragmentPanelShowButton);
			codeFragmentPanelShowHide.add(this.codeFragmentPanelHideButton);
			codeFragmentPanelShowHide.setBorder(new LineBorder(Color.black));
			this.add(codeFragmentPanelShowHide);

			this.codeFragmentPanelShowButton.addActionListener(e -> {
				if (this.codeFragmentPanelShowButton.isSelected()) {
					parentContainer.showCodeFragmentListView();
				}
			});

			this.codeFragmentPanelHideButton.addActionListener(e -> {
				if (this.codeFragmentPanelHideButton.isSelected()) {
					parentContainer.hideCodeFragmentListView();
				}
			});
		}

		{
			final JPanel uninterestingClonesShowNot = new JPanel(
					new FlowLayout(FlowLayout.LEFT));
			final JLabel name = new JLabel("Uninteresting Clones");
			this.uninterstingClonesShowButton = new JRadioButton("show", true);
			this.uninterestingClonesHideButton = new JRadioButton("not", false);
			final ButtonGroup uninterestingClonesButtonGroup = new ButtonGroup();
			uninterestingClonesButtonGroup
					.add(this.uninterstingClonesShowButton);
			uninterestingClonesButtonGroup
					.add(this.uninterestingClonesHideButton);
			uninterestingClonesShowNot.add(name);
			uninterestingClonesShowNot.add(this.uninterstingClonesShowButton);
			uninterestingClonesShowNot.add(this.uninterestingClonesHideButton);
			uninterestingClonesShowNot.setBorder(new LineBorder(Color.black));
			this.add(uninterestingClonesShowNot);

			this.uninterstingClonesShowButton.addActionListener(e -> {
				if (this.uninterstingClonesShowButton.isSelected()) {
					UninterestingClonesDisplay.getInstance(UNINTERESTING).set(
							true);
				}
			});

			this.uninterestingClonesHideButton.addActionListener(e -> {
				if (uninterestingClonesHideButton.isSelected()) {
					UninterestingClonesDisplay.getInstance(UNINTERESTING).set(
							false);
				}
			});
		}

		{
			final JPanel resetPanel = new JPanel(
					new FlowLayout(FlowLayout.LEFT));
			final JButton resetButton = new JButton("Reset");
			resetPanel.add(resetButton);
			resetPanel.setBorder(new LineBorder(Color.black));
			this.add(resetPanel);

			resetButton.addActionListener(e -> parentContainer.reset());
		}
	}

	public void reset() {
		this.groupPanelShowButton.doClick();
		this.codeFragmentPanelShowButton.doClick();
		this.uninterstingClonesShowButton.doClick();
	}

	public void update(Observable o, Object arg) {
	}

	private final JRadioButton groupPanelShowButton;
	private final JRadioButton groupPanelHideButton;
	private final JRadioButton filePanelShowButton;
	private final JRadioButton filePanelHideButton;
	private final JRadioButton codeFragmentPanelShowButton;
	private final JRadioButton codeFragmentPanelHideButton;
	private final JRadioButton uninterstingClonesShowButton;
	private final JRadioButton uninterestingClonesHideButton;

}
