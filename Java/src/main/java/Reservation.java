import java.util.ArrayList;
import java.util.List;

public class Reservation {
	public final String trainId;
    private final String bookingId;
    private final List<Seat> seats;

    public static Reservation none(String train) {
        List<Seat> seatsReserved = new ArrayList<Seat>();
        return new Reservation(train, seatsReserved, "");
    }

    public Reservation(String trainId, List<Seat> seats, String bookingId) {
		this.trainId = trainId;
        this.bookingId = bookingId;
        this.seats = seats;
    }

    public String[] seatsReserved() {
        return new String[]{seats.get(0).number()};
    }

    public boolean nothingBooked() {
        return bookingId.isEmpty() && seats.isEmpty();
    }

    public String toString() {
        return String.format("Booking reference: %s train ID: %s, seats: %s", bookingId, trainId, seats);
    }

}
