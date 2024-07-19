import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.*;
import org.jmock.Mockery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketOfficeTest {
    private TicketOffice ticketOffice;
    private final Mockery context = new JUnit5Mockery();
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
        TrainDataService trainDataService = context.mock(TrainDataService.class);
        context.checking(new Expectations() {{
            List<Seat> freeSeats = new ArrayList<Seat>();
            freeSeats.add(new Seat("A", 1));
            oneOf(trainDataService).availableSeatsOn(with(equal("train-LDN-LIV"))); will(returnValue(freeSeats));
        }});

        ReservationRequest singleSeat = new ReservationRequest("train-LDN-LIV", 1);

        Reservation actual = ticketOffice.makeReservation(singleSeat);

        Assert.assertEquals("train-LDN-LIV", actual.trainId);
        Assert.assertArrayEquals(new String[] {"A1"}, actual.seatsReserved());
    }

    private void assertNoReservationMade(Reservation reservation) {
        Assert.assertTrue(reservation.nothingBooked());
    }
}
