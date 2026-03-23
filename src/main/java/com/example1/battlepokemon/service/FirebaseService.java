package com.example1.battlepokemon.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

/**
 * Servicio de infraestructura para persistir datos en Firestore.
 */
@Service
public class FirebaseService {

    private static final String COLLECTION_BATTLE = "battle-pokemon";

    public String guardarDato() {
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Pokemon 1");
        data.put("vida", 100);

        return guardarDato(COLLECTION_BATTLE, "pokemon1", data, null);
    }

    public String guardarDato(String collection, String document, Map<String, Object> payload, String eventId) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> data = new HashMap<>(payload);
            if (eventId != null && !eventId.isBlank()) {
                data.put("eventId", eventId);
            }

            db.collection(collection)
              .document(document)
              .set(data)
              .get();

            return "Datos guardados correctamente";

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al guardar datos";
        }
    }

    public Map<String, Object> getPokemon(String document) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            DocumentSnapshot snapshot = db.collection(COLLECTION_BATTLE)
                                          .document(document)
                                          .get()
                                          .get();

            if (!snapshot.exists()) {
                return null;
            }

            return snapshot.getData();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean updatePokemonHp(String document, int hp) {
        try {
            Firestore db = FirestoreClient.getFirestore();

            Map<String, Object> update = new HashMap<>();
            update.put("vida", hp);

            db.collection(COLLECTION_BATTLE)
              .document(document)
              .update(update)
              .get();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}