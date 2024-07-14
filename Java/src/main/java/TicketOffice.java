import java.util.ArrayList;

public class TicketOffice {
    
    public Reservation makeReservation(ReservationRequest request) {
		return new Reservation("train-LDN-OXF", new ArrayList<Seat>(), "");
    }

}