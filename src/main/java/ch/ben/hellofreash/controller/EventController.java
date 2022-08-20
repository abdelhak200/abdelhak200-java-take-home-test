package ch.ben.hellofreash.controller;

import ch.ben.hellofreash.service.EventService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/"})
public class EventController {
	
   @Autowired
   private final EventService eventService;

   @PostMapping({"/event"})
   public ResponseEntity<String> getEvent(@RequestParam("file") MultipartFile file) {
      return eventService.getEvent(file);
   }

   @GetMapping({"/stats"})
   public String getStats() {
      return eventService.getStats(eventService.getEvents());
   }
   
}
