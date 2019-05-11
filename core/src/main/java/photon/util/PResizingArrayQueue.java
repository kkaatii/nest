package photon.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class PResizingArrayQueue<Item> implements Iterable<Item> {

    private Item[] array;
    private int size;

    public PResizingArrayQueue()                 // construct an empty randomized queue
    {
        array = (Item[]) new Object[2];
        size = 0;
    }

    public boolean isEmpty()                 // is the randomized queue empty?
    {
        return size == 0;
    }

    public int size()                        // return the number of items on the randomized queue
    {
        return size;
    }

    public void enqueue(Item item)           // add the item
    {
        if (item == null)
            throw new IllegalArgumentException();

        int len = array.length;
        if (size == len) {
            Item[] copy = (Item[]) new Object[len * 2];
            System.arraycopy(array, 0, copy, 0, size);
            array = copy;
        }
        array[size++] = item;
    }

    public Item dequeue()                    // remove and return a random item
    {
        if (size == 0)
            throw new NoSuchElementException();

        if (size == 1) {
            Item item = array[--size];
            array[0] = null;
            return item;
        } else {
            Item item = array[--size];
            array[size] = null;

            int len = array.length;
            if (size < len / 4) {
                Item[] copy = (Item[]) new Object[len / 2];
                System.arraycopy(array, 0, copy, 0, size);
                array = copy;
            }

            return item;
        }
    }

    public Iterator<Item> iterator()         // return an independent iterator over items in random order
    {
        return new SimpleIterator();
    }

    private class SimpleIterator implements Iterator<Item> {
        private int index;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return array[index++];
        }
    }


}
