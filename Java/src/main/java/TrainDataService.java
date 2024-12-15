import java.util.List;

public interface TrainDataService {
    List<Seat> availableSeatsOn(String train);
    Coach coaches(String train);
    boolean reserve(String train, String[] seats, String bookingReference);
    Coach firstCoach(String train);
}
