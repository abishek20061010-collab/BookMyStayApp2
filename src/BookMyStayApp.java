import java.util.HashMap;

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

// ================= INVENTORY CLASS (UC3) =================
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

    void updateAvailability(String roomType, int count) {
        availability.put(roomType, count);
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

            // Show only available rooms
            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available + "\n");
            }
        }

        System.out.println("=== End of Search Results ===");
    }
}

// ================= MAIN APPLICATION =================
public class BookMyStayApp {

    public static void main(String[] args) {

        // ===== UC1 =====
        System.out.println("=======================================");
        System.out.println("     Welcome to Book My Stay App");
        System.out.println("     Hotel Booking Management System");
        System.out.println("     Version: 4.1");
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

        // Perform search (READ-ONLY)
        searchService.searchAvailableRooms(inventory, rooms);

        // Show inventory separately (unchanged)
        inventory.displayInventory();

        System.out.println("\nApplication execution completed.");
    }
}