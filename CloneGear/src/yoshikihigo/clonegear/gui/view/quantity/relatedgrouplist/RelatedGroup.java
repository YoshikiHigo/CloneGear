package yoshikihigo.clonegear.gui.view.quantity.relatedgrouplist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yoshikihigo.clonegear.gui.data.clone.GUIClone;
import yoshikihigo.clonegear.gui.data.file.FileOffsetData;
import yoshikihigo.clonegear.gui.data.file.GUIFile;
import yoshikihigo.clonegear.gui.data.file.GUIFileManager;

public class RelatedGroup {

	static public RelatedGroup[] getRelatedGroups(final int groupID) {
		return new RelatedGroup[0];
	}

	final private int leftGroupID;
	final private int rightGroupID;

	final private int[] leftLines;
	final private int[] rightLines;

	final private List<GUIClone> leftClones;
	final private List<GUIClone> rightClones;

	public RelatedGroup(final int leftGroupID, final int rightGroupID) {

		this.leftClones = new ArrayList<>();
		this.rightClones = new ArrayList<>();

		this.leftGroupID = leftGroupID;
		this.rightGroupID = rightGroupID;

		final int leftLOC = GUIFileManager.SINGLETON
				.getGroupLOC(this.leftGroupID);
		final int rightLOC = GUIFileManager.SINGLETON
				.getGroupLOC(this.rightGroupID);
		this.leftLines = new int[leftLOC];
		this.rightLines = new int[rightLOC];
		Arrays.fill(this.leftLines, -1);
		Arrays.fill(this.rightLines, -1);
	}

	public void addLeftClone(final int fileID, final GUIClone clone) {

		final GUIFile firstFile = GUIFileManager.SINGLETON.getFile(
				this.leftGroupID, 0);
		final GUIFile ownerFile = GUIFileManager.SINGLETON.getFile(
				this.leftGroupID, fileID);
		final int groupOffset = FileOffsetData.SINGLETON.get(firstFile);
		final int fileOffset = FileOffsetData.SINGLETON.get(ownerFile);

		final int fromLine = clone.fromLine + fileOffset - groupOffset;
		final int toLine = clone.toLine + fileOffset - groupOffset;
		final int rnr = clone.getRNR();

		for (int i = fromLine; i <= toLine; i++) {
			if (this.leftLines[i] < 0) {
				this.leftLines[i] = rnr;
			} else if (rnr < this.leftLines[i]) {
				this.leftLines[i] = rnr;
			}
		}

		this.leftClones.add(clone);
	}

	public void addRightClone(final int fileID, final GUIClone clone) {

		final GUIFile firstFile = GUIFileManager.SINGLETON.getFile(
				this.rightGroupID, 0);
		final GUIFile ownerFile = GUIFileManager.SINGLETON.getFile(
				this.rightGroupID, fileID);
		final int groupOffset = FileOffsetData.SINGLETON.get(firstFile);
		final int fileOffset = FileOffsetData.SINGLETON.get(ownerFile);

		final int fromToken = clone.fromLine + fileOffset - groupOffset;
		final int toToken = clone.toLine + fileOffset - groupOffset;
		final int rnr = clone.getRNR();

		for (int i = fromToken; i <= toToken; i++) {
			if (this.rightLines[i] < 0) {
				this.rightLines[i] = rnr;
			} else if (rnr < this.rightLines[i]) {
				this.rightLines[i] = rnr;
			}
		}

		this.rightClones.add(clone);
	}

	int getLeftGroupID() {
		return this.leftGroupID;
	}

	int getLeftNOC() {
		return this.getNOC(this.leftClones, 0);
	}

	int getLeftNOC(final int rnr) {
		return this.getNOC(this.leftClones, rnr);
	}

	int getLeftROC() {
		return this.getROC(this.leftLines, 0);
	}

	int getLeftROC(final int rnr) {
		return this.getROC(this.leftLines, rnr);
	}

	int getRightROC() {
		return this.getROC(this.rightLines, 0);
	}

	int getRightROC(final int rnr) {
		return this.getROC(this.rightLines, rnr);
	}

	int getRightNOC() {
		return this.getNOC(this.rightClones, 0);
	}

	int getRightNOC(final int rnr) {
		return this.getNOC(this.rightClones, rnr);
	}

	int getRightGroupID() {
		return this.rightGroupID;
	}

	private int getROC(final int[] token, final int rnr) {

		int clonedTokenNumber = 0;
		for (int i = 0; i < token.length; i++) {
			if (rnr <= token[i]) {
				clonedTokenNumber++;
			}
		}

		return (100 * clonedTokenNumber) / token.length;
	}

	private int getNOC(final List<GUIClone> clones, final int rnr) {
		return (int) clones.stream().filter(clone -> rnr <= clone.getRNR())
				.count();
	}
}
