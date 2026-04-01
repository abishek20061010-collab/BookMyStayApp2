import java.util.*;

// ================= CUSTOM EXCEPTION =================
class InvalidBookingException extends Exception {
    InvalidBookingException(String message) {
        super(message);
    }
}

// ================= ROOM =================
abstract class Room {
    String type;
    int beds;
    double price;

    Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }
}

class SingleRoom extends Room {
    SingleRoom() { super("Single Room", 1, 1000); }
}

class DoubleRoom extends Room {
    DoubleRoom() { super("Double Room", 2, 2000); }
}

class SuiteRoom extends Room {
    SuiteRoom() { super("Suite Room", 3, 5000); }
}

// ================= INVENTORY =================
class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    RoomInventory() {
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    int getAvailability(String type) {
        return availability.getOrDefault(type, -1);
    }

    void reduce(String type) throws InvalidBookingException {
        int current = getAvailability(type);

        if (current == -1) {
            throw new InvalidBookingException("Invalid Room Type: " + type);
        }

        if (current <= 0) {
            throw new InvalidBookingException("No rooms available for: " + type);
        }

        availability.put(type, current - 1);
    }
}

// ================= RESERVATION =================
class Reservation {
    String guestName;
    String roomType;
    String reservationId;
    String roomId;

    Reservation(String guestName, String roomType, String reservationId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.reservationId = reservationId;
    }
}

// ================= QUEUE =================
class BookingQueue {
    Queue<Reservation> q = new LinkedList<>();

    void add(Reservation r) { q.add(r); }
    Reservation next() { return q.poll(); }
    boolean has() { return !q.isEmpty(); }
}

// ================= HISTORY =================
class BookingHistory {
    List<Reservation> list = new ArrayList<>();
    void add(Reservation r) { list.add(r); }
    List<Reservation> getAll() { return list; }
}

// ================= BOOKING SERVICE =================
class BookingService {

    Set<String> usedIds = new HashSet<>();
    int counter = 1;

    void process(BookingQueue q, RoomInventory inv, BookingHistory history) {

        while (q.has()) {

            Reservation r = q.next();

            try {
                // 🔥 VALIDATION
                if (r.roomType == null || r.roomType.isEmpty()) {
                    throw new InvalidBookingException("Room type cannot be empty");
                }

                // 🔥 INVENTORY CHECK + REDUCE
                inv.reduce(r.roomType);

                // 🔥 GENERATE UNIQUE ID
                String roomId = r.roomType.substring(0,2).toUpperCase() + counter++;

                if (usedIds.contains(roomId)) {
                    throw new InvalidBookingException("Duplicate Room ID generated!");
                }

                usedIds.add(roomId);
                r.roomId = roomId;

                // 🔥 STORE HISTORY
                history.add(r);

                System.out.println("✅ Booking Confirmed: " + r.guestName +
                        " | Room: " + r.roomType +
                        " | RoomID: " + roomId);

            } catch (InvalidBookingException e) {
                // 🔥 GRACEFUL FAILURE
                System.out.println("❌ Booking Failed for " + r.guestName +
                        " → " + e.getMessage());
            }
        }
    }
}

// ================= MAIN =================
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App v9.1 =====");

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();
        BookingHistory history = new BookingHistory();

        // ✅ VALID BOOKINGS
        queue.add(new Reservation("Abishek", "Single Room", "R1"));
        queue.add(new Reservation("Rahul", "Double Room", "R2"));

        // ❌ INVALID CASES
        queue.add(new Reservation("Kiran", "Invalid Room", "R3")); // wrong type
        queue.add(new Reservation("Arun", "", "R4"));              // empty type

        BookingService service = new BookingService();
        service.process(queue, inventory, history);

        // SHOW HISTORY
        System.out.println("\n=== Booking History ===");
        for (Reservation r : history.getAll()) {
            System.out.println(r.guestName + " | " + r.roomType + " | " + r.roomId);
        }

        System.out.println("\nApplication running safely after errors ✔");
    }
}