package photon.util;

/**
 * Created by Dun Liu on 1/29/2017.
 */
public class ImmutableTuple<U, V> {
    private U left;
    private V right;

    public ImmutableTuple(U left, V right) {
        this.left = left;
        this.right = right;
    }

    public U left() {
        return left;
    }

    public V right() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableTuple<?, ?> that = (ImmutableTuple<?, ?>) o;

        if (left != null ? !left.equals(that.left) : that.left != null) return false;
        return right != null ? right.equals(that.right) : that.right == null;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }
}
