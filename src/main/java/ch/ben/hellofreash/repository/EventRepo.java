package ch.ben.hellofreash.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Repository;

import ch.ben.hellofreash.model.Event;

@Repository
public class EventRepo implements IEventRepo {
	
	private List<List<Event>> listOfListEvents = Collections.synchronizedList(new ArrayList<List<Event>>());
	
	public void setListOfListEvents(List<List<Event>> listOfListEvents) {
		this.listOfListEvents = listOfListEvents;
	}

	public List<List<Event>> getListOfListEvents() {
		return this.listOfListEvents;
	}
	
}
