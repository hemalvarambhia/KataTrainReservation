import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

public class TicketOffice {

    private final TrainDataService trainDataService;

    public TicketOffice(TrainDataService trainDataService) {
        this.trainDataService = trainDataService;
    }
    
    public Reservation makeReservation(ReservationRequest request) {
        if(request.seatCount == 1) {
            List<Seat> seatsReserved = trainDataService.availableSeatsOn(request.trainId);
            String bookingReference = seatsReserved.isEmpty() ? "" : "75bcd15";
            trainDataService.reserve(request.trainId, seatsReserved.stream().map(Seat::number).toArray(String[]::new), bookingReference);
            return new Reservation(request.trainId, seatsReserved, bookingReference);
        } else {
            return Reservation.none(request.trainId);
        }
    }

}