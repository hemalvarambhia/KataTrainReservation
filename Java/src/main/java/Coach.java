import java.util.ArrayList;
import java.util.List;

public class Coach {
    private final List<Seat> seats;

    public Coach(List<Seat> seats) {
        this.seats = seats;
    }

    public List<Seat> getSeats() {
        return new ArrayList<>(seats);
    }

    public boolean fullyBooked() {
        return seats.isEmpty();
    }
}
