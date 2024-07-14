import org.junit.*;

import java.util.ArrayList;

public class TicketOfficeTest {

    @Test
    public void testReservingNoSeats() {
        TicketOffice ticketOffice = new TicketOffice();
        ReservationRequest request = new ReservationRequest("train-LDN-OXF", 0);

        Reservation actual = ticketOffice.makeReservation(request);

        Assert.assertEquals("train-LDN-OXF", actual.trainId);
        assertNoReservationMade(actual);
    }

    @Test
    @Ignore("TODO")
    public void testReservingASeatOnTrainWithNoCoaches() {}

    @Test
    @Ignore("TODO")
    public void testReservingASeatOnTrainWithCoachThatIsEmpty() {}

    private void assertNoReservationMade(Reservation reservation) {
        Assert.assertTrue(reservation.seats.isEmpty());
        Assert.assertTrue(reservation.bookingId.isEmpty());
    }
}
