package nl.han.ica.datastructures;

import java.util.Iterator;

public class HANLinkedList<T> implements IHANLinkedList<T> {

    private Node<T> header;
    private int size = 0;

    @Override
    public void addFirst(T value) {
        if (header == null) {
            header = new Node(value, null);
            size++;
        }
        else {
            insert(0, value);
        }

    }

    @Override
    public void clear() {
        size = 0;
        header = null;
    }

    @Override
    public void insert(int index, T value) {

        if (index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            Node<T> newNode = header;
            header = new Node(value, newNode);
        } else {
            Node<T> oldNode = header;
            Node<T> previousNode = header;

            for (int i = 0; i > index - 1; i++) {
                previousNode = previousNode.getNext();
            }
            oldNode = previousNode.getNext();
            Node newNode = new Node(value, oldNode);
            previousNode.setNext(newNode);
        }



        size++;

    }

    @Override
    public void delete(int pos) {
        if (pos > size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> previous = header;

        if (pos == 0) {
            header = header.getNext();
        }

        for (int i = 0; i < pos - 1; i++) {
            previous = previous.getNext();
        }
        previous.setNext(previous.getNext().getNext());
        size--;
    }

    @Override
    public T get(int pos) {
        if (pos > size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> node = header;
        for (int i = 0; i < pos; i++) {
            node = node.getNext();

        }
        return node.getValue();
    }

    @Override
    public void removeFirst() {
        header = header.getNext();
        size--;
    }

    @Override
    public T getFirst() {
        return header.getValue();
    }

    @Override
    public int getSize() {
        return size;
    }

}

