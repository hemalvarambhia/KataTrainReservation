import org.junit.*;

public class TicketOfficeTest {

    @Test
    public void testFailingHookup() {
        Assert.assertEquals(4, 2 + 2);
    }

    @Test
    @Ignore("TODO")
    public void testReservingNoSeats() {}

    @Test
    @Ignore("TODO")
    public void testReservingASeatOnTrainWithNoCoaches() {}

    @Test
    @Ignore("TODO")
    public void testReservingASeatOnTrainWithCoachThatIsEmpty() {}
}
