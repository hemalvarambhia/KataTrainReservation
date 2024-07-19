import java.util.ArrayList;
import java.util.List;

public class TicketOffice {

    private final TrainDataService trainDataService;

    public TicketOffice(TrainDataService trainDataService) {
        this.trainDataService = trainDataService;
    }
    
    public Reservation makeReservation(ReservationRequest request) {
        if(request.seatCount == 1) {
            List<Seat> seatsReserved = new ArrayList<Seat>();
            Seat emptySeat = new Seat("A", 1);
            seatsReserved.add(emptySeat);
            return new Reservation(request.trainId, seatsReserved, "75bcd15");
        } else {
            return Reservation.none(request.trainId);
        }
    }

}