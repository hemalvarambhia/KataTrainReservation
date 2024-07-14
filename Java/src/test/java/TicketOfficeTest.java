import org.junit.*;

import java.util.ArrayList;

public class TicketOfficeTest {
    private TicketOffice ticketOffice;

    @Before
    public void openTicketOffice() {
        ticketOffice = new TicketOffice();
    }


    @Test
    public void testReservingNoSeats() {
        ReservationRequest request = new ReservationRequest("train-LDN-OXF", 0);

        Reservation actual = ticketOffice.makeReservation(request);

        Assert.assertEquals("train-LDN-OXF", actual.trainId);
        assertNoReservationMade(actual);
    }

    @Test
    public void testReservingNoSeatsOnAnyTrain() {
        ReservationRequest singleSeat = new ReservationRequest("train-LDN-EDB", 0);

        Reservation actual = ticketOffice.makeReservation(singleSeat);

        Assert.assertEquals("train-LDN-EDB", actual.trainId);
        assertNoReservationMade(actual);
    }

    @Test
    @Ignore("TODO")
    public void testReservingASeatOnTrainWithNoCoaches() {}

    @Test
    public void testReservingASeatOnTrainWithOneCoachThatIsEmpty() {
        ReservationRequest singleSeat = new ReservationRequest("train-LDN-LIV", 1);

        Reservation actual = ticketOffice.makeReservation(singleSeat);

        Assert.assertEquals("train-LDN-LIV", actual.trainId);
        Assert.assertEquals(1, actual.seatNumbers().length);
    }

    private void assertNoReservationMade(Reservation reservation) {
        Assert.assertTrue(reservation.nothingBooked());
    }
}
