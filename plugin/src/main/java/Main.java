import org.heaphop.Visualizer;

public class Main {

    public static void main(String[] args) {
        LinkedList ll = new LinkedList();
        ll.value = 1;
        ll.ll = new LinkedList();
        ll.ll.value = 2;
        ll.ll.ll = new LinkedList();
        ll.ll.ll.value = 3;
        ll.ll.ll.ll = new LinkedList();
        ll.ll.ll.ll.value = 4;
    }

}
class LinkedList implements Visualizer {
    LinkedList ll;
    int value;
    String nume = "Andrei";
    int[] a = new int[]{1, 2, 3};
}
