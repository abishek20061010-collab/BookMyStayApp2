import java.util.*;

// ================= INVENTORY (THREAD SAFE) =================
class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    RoomInventory() {
        availability.put("Single Room", 2);
    }

    // synchronized critical section
    public synchronized boolean allocate(String type) {
        int count = availability.getOrDefault(type, 0);

        if (count > 0) {
            availability.put(type, count - 1);
            return true;
        }
        return false;
    }

    void display() {
        System.out.println("Final Inventory: " + availability);
    }
}

// ================= RESERVATION =================
class Reservation {
    String guest;
    String roomType;

    Reservation(String g, String t) {
        guest = g;
        roomType = t;
    }
}

// ================= THREAD-SAFE QUEUE =================
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void add(Reservation r) {
        queue.add(r);
    }

    public synchronized Reservation get() {
        return queue.poll();
    }
}

// ================= BOOKING PROCESSOR (THREAD) =================
class BookingProcessor extends Thread {

    private BookingQueue queue;
    private RoomInventory inventory;

    BookingProcessor(BookingQueue q, RoomInventory inv, String name) {
        super(name);
        this.queue = q;
        this.inventory = inv;
    }

    public void run() {
        while (true) {

            Reservation r;

            // synchronized fetch
            synchronized (queue) {
                r = queue.get();
            }

            if (r == null) break;

            // critical section (inventory)
            boolean success = inventory.allocate(r.roomType);

            if (success) {
                System.out.println(getName() + " ✅ Booked for " + r.guest);
            } else {
                System.out.println(getName() + " ❌ Failed for " + r.guest);
            }
        }
    }
}

// ================= MAIN =================
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App v11.1 =====");

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        // Multiple requests (simulate concurrency)
        queue.add(new Reservation("A", "Single Room"));
        queue.add(new Reservation("B", "Single Room"));
        queue.add(new Reservation("C", "Single Room"));
        queue.add(new Reservation("D", "Single Room"));

        // Multiple threads (users)
        Thread t1 = new BookingProcessor(queue, inventory, "Thread-1");
        Thread t2 = new BookingProcessor(queue, inventory, "Thread-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        inventory.display();

        System.out.println("\nThread-safe execution completed ✔");
    }
}