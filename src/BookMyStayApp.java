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
        System.out.println("Room Type: " + type);
        System.out.println("Beds: " + beds);
        System.out.println("Price per night: ₹" + price);
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
    private HashMap<String, Integer> availability = new HashMap<>();

    RoomInventory() {
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    void reduceAvailability(String type) {
        if (availability.get(type) > 0) {
            availability.put(type, availability.get(type) - 1);
        }
    }

    void displayInventory() {
        System.out.println("\n--- Inventory ---");
        availability.forEach((k,v) -> System.out.println(k + ": " + v));
    }
}

// ================= SEARCH =================
class RoomSearchService {
    void search(RoomInventory inv, Room[] rooms) {
        System.out.println("\n=== Available Rooms ===");
        for (Room r : rooms) {
            int avail = inv.getAvailability(r.type);
            if (avail > 0) {
                r.displayDetails();
                System.out.println("Available: " + avail + "\n");
            }
        }
    }
}

// ================= RESERVATION =================
class Reservation {
    String guestName;
    String roomType;
    String reservationId;

    Reservation(String guestName, String roomType, String reservationId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.reservationId = reservationId;
    }
}

// ================= QUEUE =================
class BookingQueue {
    Queue<Reservation> queue = new LinkedList<>();

    void addRequest(Reservation r) {
        queue.add(r);
    }

    Reservation next() {
        return queue.poll();
    }

    boolean has() {
        return !queue.isEmpty();
    }
}

// ================= BOOKING SERVICE =================
class BookingService {

    Set<String> usedRoomIds = new HashSet<>();
    Map<String, Set<String>> allocations = new HashMap<>();
    int counter = 1;

    void process(BookingQueue q, RoomInventory inv) {

        while (q.has()) {
            Reservation r = q.next();

            if (inv.getAvailability(r.roomType) > 0) {

                String roomId = r.roomType.substring(0,2).toUpperCase() + counter++;

                if (!usedRoomIds.contains(roomId)) {

                    usedRoomIds.add(roomId);

                    allocations.putIfAbsent(r.roomType, new HashSet<>());
                    allocations.get(r.roomType).add(roomId);

                    inv.reduceAvailability(r.roomType);

                    System.out.println("Confirmed: " + r.guestName +
                            " | RoomID: " + roomId +
                            " | ReservationID: " + r.reservationId);
                }
            } else {
                System.out.println("Failed: " + r.guestName);
            }
        }
    }
}

// ================= ADD-ON SERVICE =================
class AddOnService {
    String name;
    double cost;

    AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }
}

// ================= ADD-ON MANAGER =================
class AddOnServiceManager {

    Map<String, List<AddOnService>> servicesMap = new HashMap<>();

    void addService(String reservationId, AddOnService service) {
        servicesMap.putIfAbsent(reservationId, new ArrayList<>());
        servicesMap.get(reservationId).add(service);
    }

    double calculateTotal(String reservationId) {
        double total = 0;
        List<AddOnService> list = servicesMap.getOrDefault(reservationId, new ArrayList<>());
        for (AddOnService s : list) {
            total += s.cost;
        }
        return total;
    }

    void displayServices(String reservationId) {
        System.out.println("\nAdd-ons for Reservation " + reservationId);

        List<AddOnService> list = servicesMap.get(reservationId);

        if (list != null) {
            for (AddOnService s : list) {
                System.out.println(s.name + " - ₹" + s.cost);
            }
            System.out.println("Total Add-on Cost: ₹" + calculateTotal(reservationId));
        } else {
            System.out.println("No add-ons selected.");
        }
    }
}

// ================= MAIN =================
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App v7.1 =====");

        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        RoomInventory inventory = new RoomInventory();
        new RoomSearchService().search(inventory, rooms);

        BookingQueue queue = new BookingQueue();

        queue.addRequest(new Reservation("Abishek","Single Room","R1"));
        queue.addRequest(new Reservation("Rahul","Double Room","R2"));

        BookingService booking = new BookingService();
        booking.process(queue, inventory);

        // ===== UC7 =====
        AddOnServiceManager manager = new AddOnServiceManager();

        manager.addService("R1", new AddOnService("Breakfast", 200));
        manager.addService("R1", new AddOnService("WiFi", 100));

        manager.addService("R2", new AddOnService("Spa", 500));

        manager.displayServices("R1");
        manager.displayServices("R2");

        inventory.displayInventory();
    }
}