import java.util.ArrayList;
import java.util.List;

public class TicketOffice {
    
    public Reservation makeReservation(ReservationRequest request) {
        return Reservation.none(request.trainId);
    }

}