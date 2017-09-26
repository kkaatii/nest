package photon.tube.query.search;

public class SequencePatternElement<T> {
    final int index;
    final int times;
    final boolean isIndefinite;
    public final T unit;

    SequencePatternElement(int index, T unit, int times, boolean isIndefinite) {
        this.index = index;
        this.times = times;
        this.isIndefinite = isIndefinite;
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SequencePatternElement<?> that = (SequencePatternElement<?>) o;
        return index == that.index && times == that.times && isIndefinite == that.isIndefinite && unit.equals(that.unit);
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + times;
        result = 31 * result + (isIndefinite ? 1 : 0);
        result = 31 * result + unit.hashCode();
        return result;
    }
}
