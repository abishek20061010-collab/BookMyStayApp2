import java.util.*;

// ================= CUSTOM EXCEPTION =================
class InvalidBookingException extends Exception {
    InvalidBookingException(String msg) { super(msg); }
}

// ================= INVENTORY =================
class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    RoomInventory() {
        availability.put("Single Room", 2);
        availability.put("Double Room", 1);
    }

    int get(String type) {
        return availability.getOrDefault(type, -1);
    }

    void reduce(String type) throws InvalidBookingException {
        int val = get(type);
        if (val <= 0) throw new InvalidBookingException("No availability for " + type);
        availability.put(type, val - 1);
    }

    void increase(String type) {
        availability.put(type, availability.get(type) + 1);
    }

    void display() {
        System.out.println("\nInventory:");
        availability.forEach((k,v) -> System.out.println(k + ": " + v));
    }
}

// ================= RESERVATION =================
class Reservation {
    String guest;
    String roomType;
    String resId;
    String roomId;
    boolean active = true;

    Reservation(String g, String t, String id) {
        guest = g; roomType = t; resId = id;
    }
}

// ================= HISTORY =================
class BookingHistory {
    List<Reservation> list = new ArrayList<>();

    void add(Reservation r) { list.add(r); }

    Reservation find(String resId) {
        for (Reservation r : list) {
            if (r.resId.equals(resId)) return r;
        }
        return null;
    }

    void display() {
        System.out.println("\nHistory:");
        for (Reservation r : list) {
            System.out.println(r.resId + " | " + r.guest +
                    " | " + r.roomType + " | " + r.roomId +
                    " | Active: " + r.active);
        }
    }
}

// ================= BOOKING =================
class BookingService {
    Set<String> used = new HashSet<>();
    int count = 1;

    void book(List<Reservation> requests, RoomInventory inv, BookingHistory hist) {
        for (Reservation r : requests) {
            try {
                inv.reduce(r.roomType);

                String roomId = r.roomType.substring(0,2).toUpperCase() + count++;

                if (used.contains(roomId))
                    throw new InvalidBookingException("Duplicate ID");

                used.add(roomId);
                r.roomId = roomId;

                hist.add(r);

                System.out.println("Booked: " + r.guest + " → " + roomId);

            } catch (Exception e) {
                System.out.println("Failed: " + r.guest + " → " + e.getMessage());
            }
        }
    }
}

// ================= CANCELLATION (UC10) =================
class CancellationService {

    private Stack<String> rollbackStack = new Stack<>();

    void cancel(String resId, BookingHistory hist, RoomInventory inv) {

        Reservation r = hist.find(resId);

        if (r == null) {
            System.out.println("❌ Invalid Reservation ID");
            return;
        }

        if (!r.active) {
            System.out.println("❌ Already Cancelled");
            return;
        }

        // LIFO tracking
        rollbackStack.push(r.roomId);

        // rollback inventory
        inv.increase(r.roomType);

        // mark inactive
        r.active = false;

        System.out.println("✅ Cancelled: " + r.guest +
                " | Room Released: " + r.roomId);
    }

    void showRollbackStack() {
        System.out.println("\nRollback Stack: " + rollbackStack);
    }
}

// ================= MAIN =================
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App v10.1 =====");

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();

        BookingService booking = new BookingService();

        List<Reservation> requests = Arrays.asList(
                new Reservation("Abishek", "Single Room", "R1"),
                new Reservation("Rahul", "Double Room", "R2")
        );

        // Booking
        booking.book(requests, inventory, history);

        history.display();
        inventory.display();

        // Cancellation
        CancellationService cancelService = new CancellationService();

        cancelService.cancel("R1", history, inventory); // valid
        cancelService.cancel("R1", history, inventory); // duplicate cancel
        cancelService.cancel("R5", history, inventory); // invalid

        cancelService.showRollbackStack();

        history.display();
        inventory.display();

        System.out.println("\nSystem remains consistent after rollback ✔");
    }
}