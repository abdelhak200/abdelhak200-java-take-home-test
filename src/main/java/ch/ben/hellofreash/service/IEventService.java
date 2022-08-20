package ch.ben.hellofreash.service;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import ch.ben.hellofreash.model.Event;

public interface IEventService {

	public ResponseEntity<String> getEvent(MultipartFile file) throws IOException;

	public String getStats(List<List<Event>> listlistEvents);

	public List<List<Event>> splitList(List<Event> events);

}
