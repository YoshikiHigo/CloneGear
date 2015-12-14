package yoshikihigo.clonegear.gui.view.metric.toolbar;

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
import yoshikihigo.clonegear.gui.view.metric.MetricAnalysisView;
import yoshikihigo.clonegear.gui.view.metric.MetricViewInterface;

public class ToolBarPanel extends JPanel implements Observer,
		MetricViewInterface {

	public ToolBarPanel(final MetricAnalysisView parentContainer) {

		super(new FlowLayout(FlowLayout.LEFT));

		{
			final JPanel metricGraphViewShowHide = new JPanel(new FlowLayout(
					FlowLayout.LEFT));
			final JLabel name = new JLabel("Metric Graph View");
			this.metricGraphShowButton = new JRadioButton("show", true);
			this.metricGraphHideButton = new JRadioButton("hide", false);
			final ButtonGroup metricGraphButtonGroup = new ButtonGroup();
			metricGraphButtonGroup.add(this.metricGraphShowButton);
			metricGraphButtonGroup.add(this.metricGraphHideButton);
			metricGraphViewShowHide.add(name);
			metricGraphViewShowHide.add(this.metricGraphShowButton);
			metricGraphViewShowHide.add(this.metricGraphHideButton);
			metricGraphViewShowHide.setBorder(new LineBorder(Color.black));
			this.add(metricGraphViewShowHide);

			this.metricGraphShowButton.addActionListener(e -> {
				if (ToolBarPanel.this.metricGraphShowButton.isSelected()) {
					parentContainer.showMetricGraphView();
				}
			});

			this.metricGraphHideButton.addActionListener(e -> {
				if (ToolBarPanel.this.metricGraphHideButton.isSelected()) {
					parentContainer.hideMetricGraphView();
				}
			});
		}

		{
			final JPanel uninterestingClonesShowNot = new JPanel(
					new FlowLayout(FlowLayout.LEFT));
			final JLabel name = new JLabel("Uninteresting Clones");
			this.uninterestingClonesShowButton = new JRadioButton("show", true);
			this.uninterestingClonesHideButton = new JRadioButton("not", false);
			final ButtonGroup uninterestingClonesButtonGroup = new ButtonGroup();
			uninterestingClonesButtonGroup
					.add(this.uninterestingClonesShowButton);
			uninterestingClonesButtonGroup
					.add(this.uninterestingClonesHideButton);
			uninterestingClonesShowNot.add(name);
			uninterestingClonesShowNot.add(this.uninterestingClonesShowButton);
			uninterestingClonesShowNot.add(this.uninterestingClonesHideButton);
			uninterestingClonesShowNot.setBorder(new LineBorder(Color.black));
			this.add(uninterestingClonesShowNot);

			this.uninterestingClonesShowButton.addActionListener(e -> {
				if (uninterestingClonesShowButton.isSelected()) {
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
		this.metricGraphShowButton.doClick();
		this.uninterestingClonesShowButton.doClick();
	}

	@Override
	public void update(Observable o, Object arg) {
	}

	private final JRadioButton metricGraphShowButton;
	private final JRadioButton metricGraphHideButton;
	private final JRadioButton uninterestingClonesShowButton;
	private final JRadioButton uninterestingClonesHideButton;
}
