package com.example1.battlepokemon.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

/**
 * Servicio de infraestructura para persistir datos en Firestore.
 *
 * <p>
 * Este servicio encapsula el acceso al cliente de Firebase y es invocado por
 * listeners de eventos para materializar las escrituras solicitadas.
 * </p>
 */
@Service
public class FirebaseService {

    /**
     * Escribe un documento de ejemplo en Firestore.
     *
     * @return mensaje con resultado de la operación
     */
    public String guardarDato() {
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Pokemon 1");
        data.put("vida", 100);

        return guardarDato("battle-pokemon", "pokemon1", data, null);
    }

    /**
     * Guarda datos en una colección/documento específicos de Firestore.
     *
     * @param collection nombre de la colección destino
     * @param document   identificador del documento destino
     * @param payload    contenido a persistir
     * @param eventId    identificador del evento origen para trazabilidad
     * @return mensaje con resultado de la operación
     */
    public String guardarDato(String collection, String document, Map<String, Object> payload, String eventId) {

        try {

            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> data = new HashMap<>(payload);
            if (eventId != null && !eventId.isBlank()) {
                data.put("eventId", eventId);
            }

            db.collection(collection)
                    .document(document)
                    .set(data);

            return "Datos guardados correctamente";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al guardar datos";
        }
    }
}