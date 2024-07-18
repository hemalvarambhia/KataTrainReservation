import java.util.ArrayList;
import java.util.List;

public class TicketOffice {
    
    public Reservation makeReservation(ReservationRequest request) {
        List<Seat> seatsReserved = new ArrayList<Seat>();
        return new Reservation(request.trainId, seatsReserved, "");
    }

}