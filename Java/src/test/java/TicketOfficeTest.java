import org.junit.*;

import java.util.ArrayList;

public class TicketOfficeTest {

    @Test
    public void testReservingNoSeats() {
        TicketOffice ticketOffice = new TicketOffice();
        ReservationRequest request = new ReservationRequest("train-LDN-OXF", 0);

        Reservation actual = ticketOffice.makeReservation(request);

        Assert.assertEquals("train-LDN-OXF", actual.trainId);
        Assert.assertTrue(actual.seats.isEmpty());
        Assert.assertTrue(actual.bookingId.isEmpty());
    }

    @Test
    @Ignore("TODO")
    public void testReservingASeatOnTrainWithNoCoaches() {}

    @Test
    @Ignore("TODO")
    public void testReservingASeatOnTrainWithCoachThatIsEmpty() {}
}
