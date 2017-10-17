package ch.hsr.ifs.iltis.core.data;


/**
 * A templated abstract implementation of a Pair
 *
 * @author tstauber
 *
 * @param <T1>
 *            Template-type of first element
 * @param <T2>
 *            Template-type of second element
 */
public abstract class AbstractPair<T1, T2> {
	protected T1 first;
	protected T2 second;

	public AbstractPair(final T1 first, final T2 second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String toString() {
		return "First: " + first + "\nSecond: " + second;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractPair other = (AbstractPair) obj;
		if (first == null) {
			if (other.first != null) {
				return false;
			}
		} else if (!first.equals(other.first)) {
			return false;
		}
		if (second == null) {
			if (other.second != null) {
				return false;
			}
		} else if (!second.equals(other.second)) {
			return false;
		}
		return true;
	}

}
