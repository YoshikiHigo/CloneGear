package yoshikihigo.clonegear;

import java.io.File;

import yoshikihigo.clonegear.data.CFile;
import yoshikihigo.clonegear.data.CPPFile;
import yoshikihigo.clonegear.data.JavaFile;
import yoshikihigo.clonegear.data.PythonFile;
import yoshikihigo.clonegear.data.SourceFile;

public enum LANGUAGE {

	JAVA("JAVA") {
		@Override
		public boolean isTarget(final File file) {
			return file.isFile() && file.getName().endsWith(".java");
		}

		@Override
		public SourceFile getSourceFile(final File file) {
			if (this.isTarget(file)) {
				return new JavaFile(file.getAbsolutePath());
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
		public SourceFile getSourceFile(final File file) {
			if (this.isTarget(file)) {
				return new CFile(file.getAbsolutePath());
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
		public SourceFile getSourceFile(final File file) {
			if (this.isTarget(file)) {
				return new CPPFile(file.getAbsolutePath());
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
		public SourceFile getSourceFile(final File file) {
			if (this.isTarget(file)) {
				return new PythonFile(file.getAbsolutePath());
			}
			return null;
		}
	};

	final public String value;

	private LANGUAGE(final String value) {
		this.value = value;
	}

	abstract boolean isTarget(File file);

	abstract SourceFile getSourceFile(File file);
}
