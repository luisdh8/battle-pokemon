package com.example1.battlepokemon.service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.example1.battlepokemon.event.FirebaseWriteRequestedEvent;

/**
 * Servicio responsable de publicar eventos de la batalla/persistencia.
 *
 * <p>
 * Centraliza la construcción de eventos y su publicación en el bus interno
 * de Spring para mantener desacoplada la capa web de la capa de
 * infraestructura.
 * </p>
 */
@Service
public class BattleEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public BattleEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Publica un evento de escritura en Firestore.
     *
     * @param collection colección destino en Firestore
     * @param document   documento destino en Firestore
     * @param payload    datos que se guardarán en el documento
     * @return identificador único del evento publicado
     */
    public String publishFirebaseWriteEvent(String collection, String document, Map<String, Object> payload) {
        String eventId = UUID.randomUUID().toString();

        FirebaseWriteRequestedEvent event = new FirebaseWriteRequestedEvent(
                eventId,
                collection,
                document,
                payload,
                Instant.now());

        eventPublisher.publishEvent(event);
        return eventId;
    }
}
