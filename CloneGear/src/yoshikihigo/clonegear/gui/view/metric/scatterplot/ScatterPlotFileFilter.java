package yoshikihigo.clonegear.gui.view.metric.scatterplot;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

class ScatterPlotFileFilter extends FileFilter {

	private Hashtable<String, ScatterPlotFileFilter> filters;

	private String description;
	private String fullDescription;
	private boolean useExtensionsInDescription;

	public ScatterPlotFileFilter() {
		this.filters = new Hashtable<>();
		this.description = null;
		this.fullDescription = null;
		this.useExtensionsInDescription = true;
	}

	public ScatterPlotFileFilter(final String extension) {
		this(extension, null);
	}

	public ScatterPlotFileFilter(final String extension,
			final String description) {
		this();
		if (extension != null) {
			this.addExtension(extension);
		}
		if (description != null) {
			this.setDescription(description);
		}
	}

	public ScatterPlotFileFilter(final String[] filters) {
		this(filters, null);
	}

	public ScatterPlotFileFilter(final String[] filters,
			final String description) {

		this();

		for (int i = 0; i < filters.length; i++) {
			// add filters one by one
			this.addExtension(filters[i]);
		}

		if (description != null) {
			this.setDescription(description);
		}
	}

	@Override
	public boolean accept(final File f) {
		if (f != null) {
			if (f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			if (extension != null && filters.get(getExtension(f)) != null) {
				return true;
			}
			;
		}
		return false;
	}

	public String getExtension(final File f) {
		if (f != null) {
			String filename = f.getName();
			int i = filename.lastIndexOf('.');
			if (i > 0 && i < filename.length() - 1) {
				return filename.substring(i + 1).toLowerCase();
			}
			;
		}
		return null;
	}

	public void addExtension(String extension) {
		if (this.filters == null) {
			filters = new Hashtable<>(5);
		}
		this.filters.put(extension.toLowerCase(), this);
		this.fullDescription = null;
	}

	@Override
	public String getDescription() {
		if (this.fullDescription == null) {
			if (this.description == null || this.isExtensionListInDescription()) {
				this.fullDescription = this.description == null ? "("
						: this.description + " (";
				// build the description from the extension list
				final Enumeration<String> extensions = this.filters.keys();
				if (extensions != null) {
					this.fullDescription += "." + extensions.nextElement();
					while (extensions.hasMoreElements()) {
						this.fullDescription += ", ."
								+ extensions.nextElement();
					}
				}
				this.fullDescription += ")";
			} else {
				this.fullDescription = this.description;
			}
		}
		return this.fullDescription;
	}

	public void setDescription(final String description) {
		this.description = description;
		this.fullDescription = null;
	}

	public void setExtensionListInDescription(final boolean b) {
		this.useExtensionsInDescription = b;
		this.fullDescription = null;
	}

	public boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
	}

	public boolean isRegisteredExtension(final String extension) {

		if ((extension != null) && (this.filters.get(extension) != null))
			return true;
		else
			return false;

	}
}
