package ch.ben.hellofreash.repository;

import java.util.List;

import ch.ben.hellofreash.model.Event;

public interface IEventRepo {

	public void setListOfListEvents(List<List<Event>> listOfListEvents);

	public List<List<Event>> getListOfListEvents();
}
