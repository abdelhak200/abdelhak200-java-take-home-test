package ch.ben.hellofreash.service;

import ch.ben.hellofreash.model.Event;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class EventServiceTest {
   private List<Event> events = new ArrayList<Event>();
   private List<List<Event>> lOfList = new ArrayList<List<Event>>();
   private EventService eventService;

   @Before
   public void setUp() throws FileNotFoundException, IOException {
      events.add(new Event(1607340341814L, 0.0360791311D, 1563887095));
      events.add(new Event(1607341261814L, 0.0231608748D, 1539565646));
      events.add(new Event(1607341291814L, 0.0876221433D, 1194727708));
      events.add(new Event(1607341311814L, 0.0554600768D, 2127711810));
      events.add(new Event(1607341341814L, 0.0442672968D, 1282509067));
      events.sort(Comparator.comparing(Event::getTime));
      lOfList.add(events);
      eventService = new EventService();
   }

   @Test
   public void testGetEventWithFileNullOrEmpty() {
      Assertions.assertEquals("<400 BAD_REQUEST Bad Request,No data was recorded!,[]>", eventService.getEvent((MultipartFile)null).toString());
      Assertions.assertEquals("<400 BAD_REQUEST Bad Request,No data was recorded!,[]>", eventService.getEvent(new MockMultipartFile("data.csv", new byte[0])).toString());
   }

   @Test
   public void testGetEventWithNoValidEvents() {
      Assertions.assertEquals("The data was successfully processed but there were some events not valid\n", ((String)eventService.getEvent(new MockMultipartFile("data.csv", new byte[100])).getBody()).split("\\[")[0]);
   }

   @Test(
      expected = IllegalArgumentException.class
   )
   public void testGetEventWithException() {
      eventService.getEvent(new MockMultipartFile((String)null, new byte[100]));
   }

   @Test
   public void testGetStats() {
      List<Event> list = new ArrayList<Event>();
      list.add(new Event(1607341261814L, 0.0231608748D, 1539565646));
      list.add(new Event(1607341271814L, 0.0586780608D, 111212767));
      list.add(new Event(1607341291814L, 0.0876221433D, 1194727708));
      Assertions.assertEquals("3,0.1694610789,0.0564870263,2845506121,948502040.333", eventService.getStats(eventService.splitList(list)));
   }

   @Test
   public void testGetsplitList() {
      Assertions.assertEquals(1, eventService.splitList(events).get(0).size());
      Assertions.assertEquals(3, eventService.splitList(events).get(1).size());
      Assertions.assertEquals(1, eventService.splitList(events).get(2).size());
   }
}
