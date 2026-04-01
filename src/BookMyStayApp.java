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

// ================= INVENTORY (UC3) =================
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

    void displayInventory() {
        System.out.println("\n--- Room Inventory ---");
        for (String key : availability.keySet()) {
            System.out.println(key + " Available: " + availability.get(key));
        }
    }
}

// ================= SEARCH SERVICE (UC4) =================
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

        System.out.println("=== End of Search Results ===");
    }
}

// ================= RESERVATION (UC5) =================
class Reservation {
    String guestName;
    String roomType;

    Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    void displayReservation() {
        System.out.println("Guest: " + guestName + " | Room Type: " + roomType);
    }
}

// ================= BOOKING QUEUE (UC5) =================
class BookingQueue {

    private Queue<Reservation> queue;

    BookingQueue() {
        queue = new LinkedList<>();
    }

    // Add request (FIFO)
    void addRequest(Reservation reservation) {
        queue.add(reservation);
        System.out.println("Booking request added for " + reservation.guestName);
    }

    // Display all requests
    void displayQueue() {
        System.out.println("\n--- Booking Requests Queue (FIFO) ---");

        for (Reservation r : queue) {
            r.displayReservation();
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
        System.out.println("     Version: 5.1");
        System.out.println("=======================================");

        // ===== UC2 =====
        Room r1 = new SingleRoom();
        Room r2 = new DoubleRoom();
        Room r3 = new SuiteRoom();
        Room[] rooms = {r1, r2, r3};

        // ===== UC3 =====
        RoomInventory inventory = new RoomInventory();

        // ===== UC4 =====
        RoomSearchService searchService = new RoomSearchService();
        searchService.searchAvailableRooms(inventory, rooms);

        // ===== UC5 =====
        BookingQueue bookingQueue = new BookingQueue();

        // Simulating booking requests
        bookingQueue.addRequest(new Reservation("Abishek", "Single Room"));
        bookingQueue.addRequest(new Reservation("Rahul", "Double Room"));
        bookingQueue.addRequest(new Reservation("Priya", "Suite Room"));

        // Display queue (FIFO order)
        bookingQueue.displayQueue();

        // Inventory remains unchanged
        inventory.displayInventory();

        System.out.println("\nApplication execution completed.");
    }
}