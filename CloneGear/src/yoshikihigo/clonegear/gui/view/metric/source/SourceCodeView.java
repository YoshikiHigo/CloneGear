package yoshikihigo.clonegear.gui.view.metric.source;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.util.SelectedEntities;
import yoshikihigo.clonegear.gui.view.metric.MetricViewInterface;
import yoshikihigo.clonegear.gui.view.metric.source.code.SourceCodeWindow;
import yoshikihigo.clonegear.gui.view.metric.source.path.FilePathView;

public class SourceCodeView extends JTabbedPane implements Observer,
		MetricViewInterface {

	private final java.util.HashMap<GUIClone, JPanel> sourceCodeWindowHashMap;

	public SourceCodeView() {

		super(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		this.sourceCodeWindowHashMap = new HashMap<GUIClone, JPanel>();
	}

	public void init() {
	}

	public void update(Observable o, Object arg) {

		if (o instanceof SelectedEntities) {

			final SelectedEntities selectedEntities = (SelectedEntities) o;
			if (selectedEntities.getLabel().equals(CLONE)) {

				for (final Iterator<GUIClone> fragmentIterator = this.sourceCodeWindowHashMap
						.keySet().iterator(); fragmentIterator.hasNext();) {

					final GUIClone codeFragment = fragmentIterator.next();
					if (!selectedEntities.get().contains(codeFragment)) {
						final JPanel panel = this.sourceCodeWindowHashMap
								.get(codeFragment);
						this.remove(panel);
						fragmentIterator.remove();
					}
				}

				for (final GUIClone clone : (List<GUIClone>) selectedEntities
						.get()) {

					if (!this.sourceCodeWindowHashMap.containsKey(clone)) {

						final JPanel panel = new JPanel(new BorderLayout());
						final FilePathView pathView = new FilePathView(
								clone.file.path);
						final SourceCodeWindow sourceCodeWindow = new SourceCodeWindow(
								clone);
						panel.add(pathView, BorderLayout.NORTH);
						panel.add(sourceCodeWindow.getScrollPane(),
								BorderLayout.CENTER);

						this.addTab(clone.file.getFileName(), panel);
						this.setSelectedComponent(panel);
						this.sourceCodeWindowHashMap.put(clone, panel);
					}
				}
			}
		}
	}
}
