package ch.ben.hellofreash.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ch.ben.hellofreash.repository.IEventRepo;
import ch.ben.hellofreash.service.IEventService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping({"/"})
public class EventController {
	
   @Autowired
   private final IEventService eventService;
   
   @Autowired
   private final IEventRepo eventRepo;

   @PostMapping({"/event"})
   public ResponseEntity<String> getEvent(@RequestParam("file") MultipartFile file) throws IOException {
      return eventService.getEvent(file);
   }

   @GetMapping({"/stats"})
   public String getStats() {
      return eventService.getStats(eventRepo.getListOfListEvents());
   }
   
}
