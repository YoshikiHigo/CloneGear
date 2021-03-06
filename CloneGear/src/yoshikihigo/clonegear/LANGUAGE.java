package yoshikihigo.clonegear;

import java.io.File;

import yoshikihigo.clonegear.data.CFile;
import yoshikihigo.clonegear.data.CPPFile;
import yoshikihigo.clonegear.data.HTMLFile;
import yoshikihigo.clonegear.data.JSPFile;
import yoshikihigo.clonegear.data.JavaFile;
import yoshikihigo.clonegear.data.JavascriptFile;
import yoshikihigo.clonegear.data.PHPFile;
import yoshikihigo.clonegear.data.PythonFile;
import yoshikihigo.clonegear.data.SourceFile;

public enum LANGUAGE {

	JAVA("JAVA") {
		@Override
		public boolean isTarget(final File file) {
			return file.isFile() && file.getName().endsWith(".java");
		}

		@Override
		public SourceFile getSourceFile(final File file, final int groupID) {
			if (this.isTarget(file)) {
				return new JavaFile(file.getAbsolutePath(), groupID);
			}
			return null;
		}
	},

	JAVASCRIPT("JAVASCRIPT") {
		@Override
		public boolean isTarget(final File file) {
			return file.isFile() && file.getName().endsWith(".js");
		}

		@Override
		public SourceFile getSourceFile(final File file, final int groupID) {
			if (this.isTarget(file)) {
				return new JavascriptFile(file.getAbsolutePath(), groupID);
			}
			return null;
		}
	},

	JSP("JSP") {
		@Override
		public boolean isTarget(final File file) {
			return file.isFile() && file.getName().endsWith(".jsp");
		}

		@Override
		public SourceFile getSourceFile(final File file, final int groupID) {
			if (this.isTarget(file)) {
				return new JSPFile(file.getAbsolutePath(), groupID);
			}
			return null;
		}
	},

	C("C") {
		@Override
		public boolean isTarget(final File file) {
			return file.isFile()
					&& (file.getName().endsWith(".c") || file.getName()
							.endsWith(".h"));
		}

		@Override
		public SourceFile getSourceFile(final File file, final int groupID) {
			if (this.isTarget(file)) {
				return new CFile(file.getAbsolutePath(), groupID);
			}
			return null;
		}
	},

	CPP("CPP") {
		@Override
		public boolean isTarget(final File file) {
			return file.isFile()
					&& (file.getName().endsWith(".cpp")
							|| file.getName().endsWith(".cxx")
							|| file.getName().endsWith(".hh")
							|| file.getName().endsWith(".hpp") || file
							.getName().endsWith("hxx"));
		}

		@Override
		public SourceFile getSourceFile(final File file, final int groupID) {
			if (this.isTarget(file)) {
				return new CPPFile(file.getAbsolutePath(), groupID);
			}
			return null;
		}
	},

	HTML("HTML") {
		@Override
		public boolean isTarget(final File file) {
			return file.isFile()
					&& (file.getName().endsWith(".html") || file.getName()
							.endsWith(".htm"));
		}

		@Override
		public SourceFile getSourceFile(final File file, final int groupID) {
			if (this.isTarget(file)) {
				return new HTMLFile(file.getAbsolutePath(), groupID);
			}
			return null;
		}
	},

	PHP("PHP") {
		@Override
		public boolean isTarget(final File file) {
			return file.isFile() && file.getName().endsWith(".php");
		}

		@Override
		public SourceFile getSourceFile(final File file, final int groupID) {
			if (this.isTarget(file)) {
				return new PHPFile(file.getAbsolutePath(), groupID);
			}
			return null;
		}
	},

	PYTHON("PYTHON") {
		@Override
		public boolean isTarget(final File file) {
			return file.isFile() && file.getName().endsWith(".py");
		}

		@Override
		public SourceFile getSourceFile(final File file, final int groupID) {
			if (this.isTarget(file)) {
				return new PythonFile(file.getAbsolutePath(), groupID);
			}
			return null;
		}
	};

	final public String value;

	private LANGUAGE(final String value) {
		this.value = value;
	}

	abstract boolean isTarget(File file);

	abstract SourceFile getSourceFile(File file, int groupID);
}
