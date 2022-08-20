package ch.ben.hellofreash.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Set;

import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.ben.hellofreash.model.Event;
import ch.ben.hellofreash.repository.IEventRepo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EventService implements IEventService{

	@Autowired
	private final IEventRepo eventRepo ;
	
	private final int INTERVAL_BY_SECONDS = 60;
	private final int NUMBER_OF_COLUMN = 3;
	private final String DELIMITER = ",";

	public ResponseEntity<String> getEvent(MultipartFile file) throws IOException {

		Set<String> notValidEvents = Collections.synchronizedSet(new LinkedHashSet<String>());
		List<Event> validEvents = Collections.synchronizedList(new ArrayList<Event>());
		String line = null;

		if (file == null || file.isEmpty()) {
			return new ResponseEntity<String>("No data was recorded!", HttpStatus.BAD_REQUEST);
		} else {

			try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

				while ((line = br.readLine()) != null) {
					String[] eventArr = line.split(DELIMITER);
					if (eventArr.length == NUMBER_OF_COLUMN) {
						if (!eventArr[0].trim().isEmpty() && !eventArr[1].trim().isEmpty()
								&& !eventArr[2].trim().isEmpty()) {
							
							long time = Long.parseLong(eventArr[0]);
							double x = Double.parseDouble(eventArr[1]);
							int y = Integer.parseInt(eventArr[2]);
							
							validEvents.add(new Event(time, x, y));
							
						} else {
							notValidEvents.add("(" + eventArr[0] + "," + eventArr[1] + "," + eventArr[2] + ")\n");
						}
					} else {
						notValidEvents.add("(" + Arrays.asList(eventArr).toString() + ",)\n");
					}
				}

				validEvents.sort(Comparator.comparing(Event::getTime));
				
				eventRepo.setListOfListEvents(splitList(validEvents));

			}catch (IOException e) {
				throw new IOException("An error occurred while processing the file");
			} catch (NumberFormatException e) {
				throw new NumberFormatException("Number Format Exception");
			}  catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Illegal Argument Exception");
			}
		}
		return notValidEvents.size() > 0
				? new ResponseEntity<String>(
						"The data was successfully processed but there were some events not valid\n" + notValidEvents,
						HttpStatus.ACCEPTED)
				: new ResponseEntity<String>("The data was successfully processed", HttpStatus.ACCEPTED);

	}

	public String getStats(List<List<Event>> listlistEvents) {

		final DecimalFormat df = new DecimalFormat("###.000");
		StringBuilder sb = new StringBuilder();

		synchronized (listlistEvents) {
			for (List<Event> list : listlistEvents) {
				if (!list.isEmpty()) {
					
					long total = list.stream().count();
					
					DoubleSummaryStatistics statsX = list
							.stream()
							.map(e -> e.getX())
							.mapToDouble(x -> x)
							.summaryStatistics();
					
					double sumX = DoubleRounder.round(statsX.getSum(), 10);
					double avgX = DoubleRounder.round(statsX.getAverage(), 10);
					
					LongSummaryStatistics statsY = list
							.stream()
							.map(e -> e.getY())
							.mapToLong(y -> y)
							.summaryStatistics();
					
					long sumY = statsY.getSum();
					double avgY = statsY.getAverage();
					
					sb.append("\n").append(total + "," + sumX + "," + avgX + "," + sumY + "," + df.format(avgY));
				}
			}

			return sb.toString().replaceFirst("\n", "");
		}
	}

	public List<List<Event>> splitList(List<Event> events) {
		List<List<Event>> lOfList = new ArrayList<List<Event>>();
		
		synchronized (events) {
			if (!events.isEmpty()) {
				long prevTime = events.get(0).getTime() / 1000;
				List<Event> list = null;

				for (int i = 1; i < events.size(); ++i) {
					if (i == 1) {
						list = new ArrayList<Event>();
						list.add(events.get(0));
					}

					long currentTime = events.get(i).getTime() / 1000;
					if (currentTime - prevTime <= INTERVAL_BY_SECONDS) {
						list.add(events.get(i));
					} else if (currentTime - prevTime > INTERVAL_BY_SECONDS) {
						lOfList.add(list);
						prevTime = currentTime;
						list = new ArrayList<Event>();
						list.add(events.get(i));
					} else {
						list.add(events.get(i));
					}
				}

				lOfList.add(list);
			}

			return lOfList;
		}
	}

}
