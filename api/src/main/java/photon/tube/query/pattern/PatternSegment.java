package photon.tube.query.pattern;

public class PatternSegment<T> {
    int index;
    int times;
    boolean isInfinite;
    public final T unit;

    PatternSegment(int index, int times, boolean isInfinite, T unit) {
        this.index = index;
        this.times = times;
        this.isInfinite = isInfinite;
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatternSegment<?> that = (PatternSegment<?>) o;
        return index == that.index && times == that.times && isInfinite == that.isInfinite && unit.equals(that.unit);
    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + times;
        result = 31 * result + (isInfinite ? 1 : 0);
        result = 31 * result + unit.hashCode();
        return result;
    }
}
