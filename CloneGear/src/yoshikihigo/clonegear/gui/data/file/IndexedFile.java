package yoshikihigo.clonegear.gui.data.file;

import java.io.File;

public class IndexedFile implements Comparable {

	private File file;
	private int startIndex;
	private int endIndex;

	public IndexedFile(final File file) {
		this.file = file;
		this.startIndex = 0;
		this.endIndex = 0;
	}

	public IndexedFile(final String string) {
		this.file = new File(string);
		this.startIndex = 0;
		this.endIndex = 0;
	}

	public IndexedFile(final File file, final int startIndex, final int endIndex) {
		this.file = file;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}

	public void setStartIndex(final int startIndex) {
		this.startIndex = startIndex;
	}

	public void setEndIndex(final int endIndex) {
		this.endIndex = endIndex;
	}

	public int getStartIndex() {
		return this.startIndex;
	}

	public int getEndIndex() {
		return this.endIndex;
	}

	@Override
	public String toString() {
		return this.file.getName();
	}

	public boolean isFile() {
		return this.file.isFile();
	}

	public boolean isDirectory() {
		return this.file.isDirectory();
	}

	public String getAbsolutePath() {
		return this.file.getAbsolutePath();
	}

	public String getParent() {
		return this.file.getParent();
	}

	public String getName() {
		return this.file.getName();
	}

	public File getFile() {
		return this.file;
	}

	static public IndexedFile[] convert(final File[] fileList) {

		int elementNum = fileList.length;
		IndexedFile[] indexedFileList = new IndexedFile[elementNum];
		for (int i = 0; i < elementNum; i++) {
			indexedFileList[i] = new IndexedFile(fileList[i], i, i);
		}

		return indexedFileList;
	}

	@Override
	public boolean equals(final Object o) {

		if (o == null) {
			return false;
		}

		if (!(o instanceof IndexedFile)) {
			return false;
		}

		final IndexedFile target = (IndexedFile) o;
		return this.file.equals(target.file);
	}

	@Override
	public int hashCode() {
		return this.file.hashCode();
	}

	@Override
	public int compareTo(final Object o) {

		final IndexedFile leftFile = this;
		final IndexedFile rightFile = (IndexedFile) o;
		if (leftFile.getStartIndex() > rightFile.getStartIndex())
			return 1;
		else if (leftFile.getStartIndex() < rightFile.getStartIndex())
			return -1;
		else if (leftFile.getEndIndex() > rightFile.getEndIndex())
			return 1;
		else if (leftFile.getEndIndex() < rightFile.getEndIndex())
			return -1;
		else if (leftFile.getAbsolutePath().length() > rightFile
				.getAbsolutePath().length())
			return 1;
		else if (leftFile.getAbsolutePath().length() < rightFile
				.getAbsolutePath().length())
			return -1;
		else
			return 0;
	}
}
