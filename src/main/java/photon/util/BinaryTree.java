package photon.util;

/**
 * Created by dan on 10/6/16.
 */
public class BinaryTree {

    protected BinaryTree left = null, right = null;
    private int id;

    public BinaryTree getLeft() {
        return left;
    }

    public void setLeft(BinaryTree left) {
        this.left = left;
    }

    public BinaryTree getRight() {
        return right;
    }

    public void setRight(BinaryTree right) {
        this.right = right;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
