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

    public String bookingReference() {
        return bookingId;
    }

    public String[] seatsReserved() {
        return seats.stream().map(Seat::number).toArray(String[]::new);
    }

    public boolean nothingBooked() {
        return bookingId.isEmpty() && seats.isEmpty();
    }

    public String toString() {
        return String.format("Booking reference: %s train ID: %s, seats: %s", bookingId, trainId, seats);
    }

}
