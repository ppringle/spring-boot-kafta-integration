package com.learnkafka.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.EventType;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/library-event")
@Slf4j
public class LibraryEventsController {

    @Autowired
    private LibraryEventProducer libraryEventProducer;

    @PostMapping("")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody LibraryEvent libraryEvent)
            throws JsonProcessingException {
        libraryEvent.setType(EventType.NEW);
        libraryEventProducer.sendLibraryEventAsynch2(libraryEvent);
        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

}
