package yoshikihigo.clonegear.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class CloneHash {

	final public byte[][] value;

	public CloneHash(final byte[][] value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		for (int index = 0; index < this.value.length; index++) {
			hash += this.value[index][0];
		}
		return hash;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof CloneHash)) {
			return false;
		}

		final CloneHash target = (CloneHash) o;
		if (this.value.length != target.value.length) {
			return false;
		}

		for (int index = 0; index < this.value.length; index++) {
			if (!Arrays.equals(this.value[index], target.value[index])) {
				return false;
			}
		}

		return true;
	}
}
