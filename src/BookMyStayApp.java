import java.io.*;
import java.util.*;

// ================= INVENTORY =================
class RoomInventory implements Serializable {
    Map<String, Integer> availability = new HashMap<>();

    RoomInventory() {
        availability.put("Single Room", 2);
        availability.put("Double Room", 1);
    }

    void display() {
        System.out.println("Inventory: " + availability);
    }
}

// ================= RESERVATION =================
class Reservation implements Serializable {
    String guest;
    String roomType;
    String resId;

    Reservation(String g, String t, String id) {
        guest = g;
        roomType = t;
        resId = id;
    }
}

// ================= BOOKING HISTORY =================
class BookingHistory implements Serializable {
    List<Reservation> list = new ArrayList<>();

    void add(Reservation r) {
        list.add(r);
    }

    void display() {
        System.out.println("\nBooking History:");
        for (Reservation r : list) {
            System.out.println(r.resId + " | " + r.guest + " | " + r.roomType);
        }
    }
}

// ================= PERSISTENCE SERVICE =================
class PersistenceService {

    private static final String FILE_NAME = "booking_data.ser";

    // SAVE
    void save(RoomInventory inventory, BookingHistory history) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(inventory);
            oos.writeObject(history);

            System.out.println("✅ Data saved successfully");

        } catch (IOException e) {
            System.out.println("❌ Error saving data: " + e.getMessage());
        }
    }

    // LOAD
    Object[] load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            RoomInventory inv = (RoomInventory) ois.readObject();
            BookingHistory hist = (BookingHistory) ois.readObject();

            System.out.println("✅ Data loaded successfully");

            return new Object[]{inv, hist};

        } catch (Exception e) {
            System.out.println("⚠ No previous data found. Starting fresh.");
            return null;
        }
    }
}

// ================= MAIN =================
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("===== Book My Stay App v12.1 =====");

        PersistenceService persistence = new PersistenceService();

        RoomInventory inventory;
        BookingHistory history;

        // LOAD EXISTING DATA
        Object[] data = persistence.load();

        if (data != null) {
            inventory = (RoomInventory) data[0];
            history = (BookingHistory) data[1];
        } else {
            inventory = new RoomInventory();
            history = new BookingHistory();
        }

        // SIMULATE NEW BOOKING
        Reservation r1 = new Reservation("Abishek", "Single Room", "R1");
        history.add(r1);

        // DISPLAY CURRENT STATE
        inventory.display();
        history.display();

        // SAVE DATA BEFORE EXIT
        persistence.save(inventory, history);

        System.out.println("\nSystem state persisted ✔");
    }
}