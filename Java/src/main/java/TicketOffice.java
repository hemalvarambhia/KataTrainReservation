import java.util.ArrayList;

public class TicketOffice {
    
    public Reservation makeReservation(ReservationRequest request) {
		return new Reservation(request.trainId, new ArrayList<Seat>(), "");
    }

}