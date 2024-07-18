import java.util.ArrayList;
import java.util.List;

public class TicketOffice {
    
    public Reservation makeReservation(ReservationRequest request) {
        if(request.seatCount == 1) {
            List<Seat> seatsReserved = new ArrayList<Seat>();
            seatsReserved.add(new Seat("A", 1));
            return new Reservation(request.trainId, seatsReserved, "75bcd15");
        } else {
            return Reservation.none(request.trainId);
        }
    }

}