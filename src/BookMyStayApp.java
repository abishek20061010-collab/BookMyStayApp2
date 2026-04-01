import java.util.*;

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

    void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: ₹" + price);
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
        return availability.getOrDefault(type, 0);
    }

    void reduce(String type) {
        availability.put(type, availability.get(type) - 1);
    }

    void display() {
        System.out.println("\nInventory:");
        availability.forEach((k,v) -> System.out.println(k + ": " + v));
    }
}

// ================= SEARCH =================
class RoomSearchService {
    void search(RoomInventory inv, Room[] rooms) {
        System.out.println("\nAvailable Rooms:");
        for (Room r : rooms) {
            int a = inv.getAvailability(r.type);
            if (a > 0) {
                r.displayDetails();
                System.out.println("Available: " + a);
            }
        }
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

// ================= HISTORY (UC8) =================
class BookingHistory {
    List<Reservation> history = new ArrayList<>();

    void add(Reservation r) {
        history.add(r);
    }

    List<Reservation> getAll() {
        return history;
    }
}

// ================= REPORT SERVICE =================
class BookingReportService {

    void displayAll(List<Reservation> list) {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : list) {
            System.out.println("ResID: " + r.reservationId +
                    " | Guest: " + r.guestName +
                    " | Room: " + r.roomType +
                    " | RoomID: " + r.roomId);
        }
    }

    void summary(List<Reservation> list) {
        System.out.println("\n=== Summary Report ===");

        Map<String, Integer> countMap = new HashMap<>();

        for (Reservation r : list) {
            countMap.put(r.roomType,
                    countMap.getOrDefault(r.roomType, 0) + 1);
        }

        countMap.forEach((k,v) ->
                System.out.println(k + " booked: " + v));
    }
}

// ================= BOOKING SERVICE =================
class BookingService {

    Set<String> usedIds = new HashSet<>();
    int counter = 1;

    void process(BookingQueue q, RoomInventory inv, BookingHistory history) {

        while (q.has()) {

            Reservation r = q.next();

            if (inv.getAvailability(r.roomType) > 0) {

                String roomId = r.roomType.substring(0,2).toUpperCase() + counter++;

                if (!usedIds.contains(roomId)) {

                    usedIds.add(roomId);
                    inv.reduce(r.roomType);

                    r.roomId = roomId;

                    // 👉 UC8: store in history
                    history.add(r);

                    System.out.println("Confirmed: " + r.guestName +
                            " | RoomID: " + roomId);
                }
            } else {
                System.out.println("Failed: " + r.guestName);
            }
        }
    }
}

// ================= ADD-ONS =================
class AddOnService {
    String name;
    double cost;

    AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }
}

class AddOnManager {
    Map<String, List<AddOnService>> map = new HashMap<>();

    void add(String resId, AddOnService s) {
        map.putIfAbsent(resId, new ArrayList<>());
        map.get(resId).add(s);
    }

    double total(String resId) {
        return map.getOrDefault(resId, new ArrayList<>())
                .stream().mapToDouble(s -> s.cost).sum();
    }
}

// ================= MAIN =================
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App v8.1 =====");

        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        RoomInventory inventory = new RoomInventory();
        new RoomSearchService().search(inventory, rooms);

        BookingQueue queue = new BookingQueue();

        queue.add(new Reservation("Abishek","Single Room","R1"));
        queue.add(new Reservation("Rahul","Double Room","R2"));
        queue.add(new Reservation("Priya","Suite Room","R3"));

        BookingHistory history = new BookingHistory();

        BookingService service = new BookingService();
        service.process(queue, inventory, history);

        // UC7 (Add-ons)
        AddOnManager addOn = new AddOnManager();
        addOn.add("R1", new AddOnService("Breakfast",200));

        // UC8 (Reporting)
        BookingReportService report = new BookingReportService();
        report.displayAll(history.getAll());
        report.summary(history.getAll());

        inventory.display();
    }
}