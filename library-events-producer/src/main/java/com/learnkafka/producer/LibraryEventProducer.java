package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
public class LibraryEventProducer {

    public static final String TOPIC_NAME = "library-events";

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendLibraryEventAsynch(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);
        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture =
                kafkaTemplate.sendDefault(key, value);
        sendResultListenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });
    }

    public void sendLibraryEventAsynch2(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, TOPIC_NAME);

        ListenableFuture<SendResult<Integer, String>> sendResultListenableFuture = kafkaTemplate.send(producerRecord);
        sendResultListenableFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });
    }

    public SendResult<Integer, String> sendLibraryEventSync(LibraryEvent libraryEvent) throws JsonProcessingException,
            ExecutionException,
            InterruptedException {

        SendResult<Integer, String> sendResult;

        Integer key = libraryEvent.getEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        try {
            sendResult = kafkaTemplate.sendDefault(key, value).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to send message to Kafka !", e);
            throw e;
        }

        return sendResult;
    }

    public void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message sent to Kafka successfully with key: {}, value: {}, partition: {}", key, value,
                result.getRecordMetadata().partition());
    }

    public void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Failed to send message to Kafka with key: {}, value: {} !" +
                "", key, value, ex);
        try {
            throw ex;
        } catch (Throwable e) {
            log.error("Error in onFailure: {}", e.getMessage());
        }
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {
        List<Header> recordHeaders = List.of(new RecordHeader("event-source",
                "scanner".getBytes(StandardCharsets.UTF_8)));
        return new ProducerRecord<>(topic, null, key, value, recordHeaders);
    }

}
