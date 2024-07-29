import java.util.List;

public interface TrainDataService {
    List<Seat> availableSeatsOn(String train);

    boolean reserve(String train, String[] seats, String bookingReference);
}
