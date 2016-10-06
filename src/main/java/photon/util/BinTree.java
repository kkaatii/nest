package photon.util;

/**
 * Created by dan on 10/6/16.
 */
public class BinTree {

    protected BinTree left = null, right = null;
    private int id;

    public BinTree getLeft() {
        return left;
    }

    public void setLeft(BinTree left) {
        this.left = left;
    }

    public BinTree getRight() {
        return right;
    }

    public void setRight(BinTree right) {
        this.right = right;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
