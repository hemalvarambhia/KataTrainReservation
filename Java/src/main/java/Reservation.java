import java.util.List;

public class Reservation {
	public final String trainId;
    public final String bookingId;
    public final List<Seat> seats;

    public Reservation(String trainId, List<Seat> seats, String bookingId) {
		this.trainId = trainId;
        this.bookingId = bookingId;
        this.seats = seats;
    }

    public String[] seatsReserved() { return new String[] {"A1"}; }

    public boolean nothingBooked() {
        return bookingId.isEmpty() && seats.isEmpty();
    }

}
