import java.util.*;

// ================= ABSTRACT ROOM CLASS =================
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

// ================= ROOM TYPES =================
class SingleRoom extends Room {
    SingleRoom() {
        super("Single Room", 1, 1000);
    }
}

class DoubleRoom extends Room {
    DoubleRoom() {
        super("Double Room", 2, 2000);
    }
}

class SuiteRoom extends Room {
    SuiteRoom() {
        super("Suite Room", 3, 5000);
    }
}

// ================= INVENTORY =================
class RoomInventory {
    private HashMap<String, Integer> availability;

    RoomInventory() {
        availability = new HashMap<>();
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    void reduceAvailability(String roomType) {
        int current = availability.getOrDefault(roomType, 0);
        if (current > 0) {
            availability.put(roomType, current - 1);
        }
    }

    void displayInventory() {
        System.out.println("\n--- Room Inventory ---");
        for (String key : availability.keySet()) {
            System.out.println(key + " Available: " + availability.get(key));
        }
    }
}

// ================= SEARCH SERVICE =================
class RoomSearchService {
    void searchAvailableRooms(RoomInventory inventory, Room[] rooms) {
        System.out.println("\n=== Available Rooms ===");

        for (Room room : rooms) {
            int available = inventory.getAvailability(room.type);

            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available + "\n");
            }
        }
    }
}

// ================= RESERVATION =================
class Reservation {
    String guestName;
    String roomType;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

// ================= BOOKING QUEUE =================
class BookingQueue {
    Queue<Reservation> queue = new LinkedList<>();

    void addRequest(Reservation r) {
        queue.add(r);
        System.out.println("Request added: " + r.guestName);
    }

    Reservation getNextRequest() {
        return queue.poll(); // FIFO
    }

    boolean hasRequests() {
        return !queue.isEmpty();
    }
}

// ================= BOOKING SERVICE (UC6) =================
class BookingService {

    private Set<String> allocatedRoomIds = new HashSet<>();
    private HashMap<String, Set<String>> roomAllocations = new HashMap<>();
    private int idCounter = 1;

    void processBookings(BookingQueue queue, RoomInventory inventory) {

        System.out.println("\n=== Processing Bookings ===");

        while (queue.hasRequests()) {

            Reservation r = queue.getNextRequest();

            String roomType = r.roomType;

            if (inventory.getAvailability(roomType) > 0) {

                // Generate unique room ID
                String roomId = roomType.substring(0, 2).toUpperCase() + idCounter++;

                // Ensure uniqueness
                if (!allocatedRoomIds.contains(roomId)) {
                    allocatedRoomIds.add(roomId);

                    // Map room type → allocated IDs
                    roomAllocations.putIfAbsent(roomType, new HashSet<>());
                    roomAllocations.get(roomType).add(roomId);

                    // Reduce inventory
                    inventory.reduceAvailability(roomType);

                    System.out.println("Booking Confirmed!");
                    System.out.println("Guest: " + r.guestName);
                    System.out.println("Room Type: " + roomType);
                    System.out.println("Allocated Room ID: " + roomId + "\n");

                }

            } else {
                System.out.println("Booking Failed for " + r.guestName +
                        " (No rooms available for " + roomType + ")");
            }
        }
    }
}

// ================= MAIN APPLICATION =================
public class BookMyStayApp {

    public static void main(String[] args) {

        // ===== UC1 =====
        System.out.println("=======================================");
        System.out.println("     Welcome to Book My Stay App");
        System.out.println("     Hotel Booking Management System");
        System.out.println("     Version: 6.1");
        System.out.println("=======================================");

        // ===== UC2 =====
        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // ===== UC3 =====
        RoomInventory inventory = new RoomInventory();

        // ===== UC4 =====
        new RoomSearchService().searchAvailableRooms(inventory, rooms);

        // ===== UC5 =====
        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("Abishek", "Single Room"));
        queue.addRequest(new Reservation("Rahul", "Double Room"));
        queue.addRequest(new Reservation("Priya", "Suite Room"));
        queue.addRequest(new Reservation("Kiran", "Suite Room"));
        queue.addRequest(new Reservation("Arun", "Suite Room")); // extra to show failure

        // ===== UC6 =====
        BookingService service = new BookingService();
        service.processBookings(queue, inventory);

        // Final inventory
        inventory.displayInventory();

        System.out.println("\nApplication execution completed.");
    }
}