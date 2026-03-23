package com.example1.battlepokemon.event;

import java.time.Instant;
import java.util.Map;

/**
 * Evento de dominio que representa una solicitud de escritura en Firestore.
 *
 * <p>
 * Este evento se publica desde la capa de entrada (controladores/servicios)
 * y se consume en listeners para desacoplar la petición HTTP de la operación de
 * persistencia.
 * </p>
 *
 * @param eventId    identificador único del evento para trazabilidad
 * @param collection nombre de la colección objetivo en Firestore
 * @param document   identificador del documento objetivo en Firestore
 * @param payload    datos a persistir en el documento
 * @param createdAt  fecha y hora de creación del evento
 */
public record FirebaseWriteRequestedEvent(
        String eventId,
        String collection,
        String document,
        Map<String, Object> payload,
        Instant createdAt) {
}
