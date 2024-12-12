import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Seat {
    public final String coach;
    public final int seatNumber;

    public static List<Seat> numbers(String... seatNumbers) {
        return Arrays.stream(seatNumbers)
                .map(Seat::from)
                .collect(Collectors.toList());
    }

    private static Seat from(String seat) {
        String coach = Character.toString(seat.charAt(0));
        int seatNumber = Character.getNumericValue(seat.charAt(1));
        return new Seat(coach, seatNumber);
    }

    public Seat(String coach, int seatNumber) {
        this.coach = coach;
        this.seatNumber = seatNumber;
    }

    public String number() {
        return coach + seatNumber;
    }

    public String toString() {
        return number();
    }

    public boolean equals(Object o) {
        Seat other = (Seat)o;
        return coach==other.coach && seatNumber==other.seatNumber;
    }
}