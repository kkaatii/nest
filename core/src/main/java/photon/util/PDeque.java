package photon.util;

import java.util.NoSuchElementException;

/**
 * Created by Dun Liu on 2/23/2017.
 */
public class PDeque<Item> extends PQueue<Item> {
    public void push(Item item) {
        Node<Item> oldFirst = first;
        first = new Node<Item>();
        first.item = item;
        first.next = oldFirst;
        N++;
    }

    public Item peekLast() {
        if (isEmpty()) throw new NoSuchElementException("PQueue underflow");
        return last.item;
    }
}
