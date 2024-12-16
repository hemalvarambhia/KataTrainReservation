import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Coach {
    private final List<Seat> seats;

    public Coach(List<Seat> seats) {
        this.seats = seats;
    }

    public boolean fullyBooked() {
        return seats.isEmpty();
    }

    public List<Seat> getSeats(int numberOfSeats) {
        return seats.stream().limit(numberOfSeats).collect(Collectors.toList());
    }
}
