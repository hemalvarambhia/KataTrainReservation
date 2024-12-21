import org.jmock.Expectations;
import org.jmock.junit5.JUnit5Mockery;
import org.junit.*;
import org.jmock.Mockery;
import java.util.List;

public class ReservingSeatsOnATrainTest {
    private TicketOffice ticketOffice;
    private final Mockery context = new JUnit5Mockery();
    private final TrainDataService trainDataService = context.mock(TrainDataService.class);
    private final BookingReferenceGenerator referenceGenerator = context.mock(BookingReferenceGenerator.class);
    private final ReservationPolicy reservationPolicy = context.mock(ReservationPolicy.class);

    @Before
    public void openTicketOffice() {
        ticketOffice = new TicketOffice(trainDataService, referenceGenerator, reservationPolicy);
    }

    @Test
    public void testASingleSeatCanBeReservedWhenReservationPolicyAllows() {
        ReservationRequest singleSeat = new ReservationRequest("train-LDN-LIV", 1);
        context.checking(
                new Expectations() {{
                    allowing(reservationPolicy).isSatisfiedBy(singleSeat); will(returnValue(true));
                    allowing(trainDataService).firstCoach(with(equal("train-LDN-LIV"))); will(returnValue(aCoachWith(Seat.numbers("A1"))));
                    allowing(referenceGenerator).generate(); will(returnValue("a booking reference"));
                    oneOf(trainDataService).reserve(
                            with(equal("train-LDN-LIV")),
                            with(new String[]{"A1"}),
                            with("a booking reference")
                    ); will(returnValue(true));
        }});

        Reservation actual = ticketOffice.makeReservation(singleSeat);

        assertReservationMadeOn("train-LDN-LIV", new String[] {"A1"}, actual);
        context.assertIsSatisfied();
    }

    @Test
    public void testMoreThanOneSeatCanBeReservedWhenReservationPolicyAllows() {
        ReservationRequest multipleSeats = new ReservationRequest("train-LDN-CAR", 2);
        context.checking(
                new Expectations() {{
                    allowing(reservationPolicy).isSatisfiedBy(multipleSeats); will(returnValue(true));
                    allowing(trainDataService).firstCoach(with(equal("train-LDN-CAR"))); will(returnValue(aCoachWith(Seat.numbers("A1", "A2"))));
                    allowing(referenceGenerator).generate(); will(returnValue("a booking reference"));
                    oneOf(trainDataService).reserve(
                            with(equal("train-LDN-CAR")),
                            with(new String[]{"A1", "A2"}),
                            with(equal("a booking reference"))
                    ); will(returnValue(true));
                }});

        Reservation actual = ticketOffice.makeReservation(multipleSeats);

        assertReservationMadeOn("train-LDN-CAR", new String[] {"A1", "A2"}, actual);
        context.assertIsSatisfied();
    }

    @Test
    public void testOnlyNumberOfSeatsRequestedAreReserved() {
        ReservationRequest twoSeats = new ReservationRequest("train-LDN-LIV", 2);
        context.checking( new Expectations(){{
                              allowing(reservationPolicy).isSatisfiedBy(twoSeats); will(returnValue(true));
                              allowing(trainDataService).firstCoach(with(equal("train-LDN-LIV"))); will(returnValue(aCoachWith(Seat.numbers("A1", "A2", "A3"))));
                              allowing(referenceGenerator).generate(); will(returnValue("a booking reference"));
                              oneOf(trainDataService).reserve(
                                      with(equal("train-LDN-LIV")),
                                      with(new String[]{"A1", "A2"}),
                                      with("a booking reference")
                              ); will(returnValue(true));
                          }}
        );

        Reservation actual = ticketOffice.makeReservation(twoSeats);

        assertReservationMadeOn("train-LDN-LIV", new String[] {"A1", "A2"}, actual);
        context.assertIsSatisfied();
    }

    @Ignore("Test list: booking seats on the first coach that satisfies the reservation policy")
    @Test
    public void testTicketOfficeBooksSeatsOnFirstCoachThatSatisfiesReservationPolicy() {
        context.assertIsSatisfied();
    }


    private Coach aCoachWith(List<Seat> seats) {
        return new Coach(seats);
    }

    private void assertReservationMadeOn(String train, String[] seats, Reservation actual) {
        Assert.assertEquals(train, actual.trainId);
        Assert.assertArrayEquals(seats, actual.seatsReserved());
        Assert.assertFalse("Booking Reference assigned", actual.bookingReference().isEmpty());
    }
}
