import java.util.HashMap;

// ================= ABSTRACT ROOM CLASS =================
abstract class Room {
    String type;
    int beds;
    double price;

    // Constructor
    Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    // Display room details
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

    // Constructor
    RoomInventory() {
        availability = new HashMap<>();

        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    // Get availability
    int getAvailability(String roomType) {
        return availability.getOrDefault(roomType, 0);
    }

    // Update availability
    void updateAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    // Display inventory
    void displayInventory() {
        System.out.println("\n--- Room Inventory ---");
        for (String key : availability.keySet()) {
            System.out.println(key + " Available: " + availability.get(key));
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
        System.out.println("     Version: 3.1");
        System.out.println("=======================================");

        // ===== UC2 =====
        Room r1 = new SingleRoom();
        Room r2 = new DoubleRoom();
        Room r3 = new SuiteRoom();

        // ===== UC3 =====
        RoomInventory inventory = new RoomInventory();

        System.out.println("\n--- Room Details ---");

        r1.displayDetails();
        System.out.println("Available: " + inventory.getAvailability("Single Room") + "\n");

        r2.displayDetails();
        System.out.println("Available: " + inventory.getAvailability("Double Room") + "\n");

        r3.displayDetails();
        System.out.println("Available: " + inventory.getAvailability("Suite Room") + "\n");

        // Display centralized inventory
        inventory.displayInventory();

        System.out.println("\nApplication execution completed.");
    }
}