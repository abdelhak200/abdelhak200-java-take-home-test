package ch.ben.hellofreash.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import ch.ben.hellofreash.model.Event;
import ch.ben.hellofreash.repository.EventRepo;
import ch.ben.hellofreash.repository.IEventRepo;

public class EventServiceTest {

	private final List<Event> events = new ArrayList<Event>();
	private final List<List<Event>> lOfList = new ArrayList<List<Event>>();

	@Autowired
	private IEventService eventService;
	@Autowired
	private IEventRepo eventRepo;

	@Before
	public void setUp() throws FileNotFoundException, IOException {
		events.add(new Event(1607340341814L, 0.0360791311D, 1563887095));
		events.add(new Event(1607341261814L, 0.0231608748D, 1539565646));
		events.add(new Event(1607341291814L, 0.0876221433D, 1194727708));
		events.add(new Event(1607341311814L, 0.0554600768D, 2127711810));
		events.add(new Event(1607341341814L, 0.0442672968D, 1282509067));

		events.sort(Comparator.comparing(Event::getTime));

		lOfList.add(events);

		eventRepo = new EventRepo();
		eventService = new EventService(eventRepo);
	}

	@Test
	public void testGetEventWithValidEvents() throws IOException {
		String data = "1660823515877,0.0008648246,1317680268\n" + "1660823467883,0.0063320566,1282584786\n"
				+ "1660823451884,0.0056576715,1523873043\n" + "1660823511899,0.1009529233,2125694651\n"
				+ "1660823459893,0.0507758632,2027065172\n" + "1660823504887,0.0079884389,1345427054\n"
				+ "1660823515907,0.1131252646,1604335032\n" + "1660823503907,0.0932387933,1702655459\n"
				+ "1660823496892,0.0065222657,1899270384\n" + "1660823498904,0.0083652996,1204884384";
		Assertions.assertEquals("The data was successfully processed",
				eventService.getEvent(new MockMultipartFile("file", "data.csv", "text/plain", data.getBytes()))
						.getBody().split("\\[")[0]);
	}

	@Test
	public void testGetEventWithNoValidEvents() throws IOException {
		String data = "1660823515877,0.0008648246,1317680268\n" + "1660823467883,0.0063320566,1282584786\n"
				+ "1660823528905,0.0004674584,1486293623\n" + "1660823460908,0.0596778281,1763278708\n"
				+ "1660823503886,0.0070513515,1598036102\n" + "1660823451884,0.0056576715,1523873043\n" + "\n"
				+ "1660823459893,0.0507758632,2027065172\n" + ",0.0079884389,1345427054\n"
				+ "1660823515907,0.1131252646,1604335032\n" + "1660823503907,,1702655459\n"
				+ "1660823496892,0.0065222657,1899270384\n" + "1660823498904,0.0083652996,";
		Assertions.assertEquals("The data was successfully processed but there were some events not valid\n",
				eventService.getEvent(new MockMultipartFile("file", "data.csv", "text/plain", data.getBytes()))
						.getBody().split("\\[")[0]);
	}

	@Test
	public void testGetEventWithFileNullOrEmpty() throws IOException {
		String data = "";
		Assertions.assertEquals("<400 BAD_REQUEST Bad Request,No data was recorded!,[]>",
				eventService.getEvent(null).toString());
		Assertions.assertEquals("<400 BAD_REQUEST Bad Request,No data was recorded!,[]>", eventService
				.getEvent(new MockMultipartFile("file", "data.csv", "text/plain", data.getBytes())).toString());
	}

	@Test(expected = NumberFormatException.class)
	public void testGetEventWithNumberFormatException() throws IOException {
		String data = "aaaabbbbcccc,0.0008648246,1317680268";
		Assertions.assertEquals("Number Format Exception", eventService
				.getEvent(new MockMultipartFile("file", "data.csv", "text/plain", data.getBytes())).toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetEventWithIllegalArgumentException() throws IOException {
		EventService eventService = mock(EventService.class);
		MockMultipartFile mockMockMultipartFile = mock(MockMultipartFile.class);
		
		when(eventService.getEvent(mockMockMultipartFile)).thenThrow(new IllegalArgumentException("Illegal Argument Exception"));
		
		Assertions.assertEquals("Illegal Argument Exception",eventService.getEvent(mockMockMultipartFile));
	}

	@Test(expected = IOException.class)
	public void testGetEventWithIOException() throws IOException {
		EventService eventService = mock(EventService.class);
		MockMultipartFile mockMockMultipartFile = mock(MockMultipartFile.class);
		
		when(eventService.getEvent(mockMockMultipartFile)).thenThrow(new IOException("An error occurred while processing the file"));
		
		Assertions.assertEquals("An error occurred while processing the file", eventService.getEvent(mockMockMultipartFile));
	}

	@Test
	public void testGetStats() {
		List<Event> list = new ArrayList<Event>();
		list.add(new Event(1607341261814L, 0.0231608748D, 1539565646));
		list.add(new Event(1607341271814L, 0.0586780608D, 111212767));
		list.add(new Event(1607341291814L, 0.0876221433D, 1194727708));
		Assertions.assertEquals("3,0.1694610789,0.0564870263,2845506121,948502040.333",
				eventService.getStats(eventService.splitList(list)));
	}

	@Test
	public void testGetsplitList() {
		Assertions.assertEquals(1, eventService.splitList(events).get(0).size());
		Assertions.assertEquals(3, eventService.splitList(events).get(1).size());
		Assertions.assertEquals(1, eventService.splitList(events).get(2).size());
	}
}
