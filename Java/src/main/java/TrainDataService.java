import java.util.List;

public interface TrainDataService {
    public List<Seat> availableSeatsOn(String train);

    public boolean reserve(String train, String[] seats, String bookingReference);
}
