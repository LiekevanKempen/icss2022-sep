import java.util.Stack;

public class HANStack<T> implements IHANStack<T> {

    HANLinkedList<T> list;

    public HANStack() {
        list = new HANLinkedList();
    }

    @Override
    public void push(T value) {
        list.addFirst(value);
    }

    @Override
    public T pop() {
        T item = list.getFirst();
        list.removeFirst();
        return item;
    }

    @Override
    public T peek() {
        return list.getFirst();
    }
}
