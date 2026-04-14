package com.example1.battlepokemon.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class FirebaseService {

    private static final String COLLECTION = "battle-pokemon";
    private static final String DOC_ESTADO = "estado";   // guarda { turno: "pokemon1" }

    // ── Pokémon ──────────────────────────────────────────────────────────────

    public Map<String, Object> getPokemon(String document) {
        try {
            DocumentSnapshot snap = db().collection(COLLECTION)
                                        .document(document).get().get();
            return snap.exists() ? snap.getData() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int countRegisteredPokemon() {
        int count = 0;
        if (getPokemon("pokemon1") != null) count++;
        if (getPokemon("pokemon2") != null) count++;
        return count;
    }

    public boolean setPokemon(String slot, String name, int hp) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("nombre", name);
            data.put("vida", hp);
            db().collection(COLLECTION).document(slot).set(data).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePokemonHp(String document, int hp) {
        try {
            Map<String, Object> update = new HashMap<>();
            update.put("vida", hp);
            db().collection(COLLECTION).document(document).update(update).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePokemon(String slot) {
        try {
            db().collection(COLLECTION).document(slot).delete().get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Turno (doc "estado") ─────────────────────────────────────────────────

    /** Lee el turno actual desde Firestore. Devuelve "pokemon1" si no existe. */
    public String getTurn() {
        try {
            DocumentSnapshot snap = db().collection(COLLECTION)
                                        .document(DOC_ESTADO).get().get();
            if (snap.exists() && snap.contains("turno")) {
                return (String) snap.get("turno");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "pokemon1";
    }

    /** Persiste el turno en Firestore. */
    public boolean setTurn(String turn) {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("turno", turn);
            db().collection(COLLECTION).document(DOC_ESTADO).set(data).get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Borra el documento de estado (turno). */
    public boolean deleteEstado() {
        try {
            db().collection(COLLECTION).document(DOC_ESTADO).delete().get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── Legado ───────────────────────────────────────────────────────────────

    public String guardarDato() {
        Map<String, Object> data = new HashMap<>();
        data.put("nombre", "Pokemon 1");
        data.put("vida", 100);
        return guardarDato(COLLECTION, "pokemon1", data, null);
    }

    public String guardarDato(String collection, String document,
                               Map<String, Object> payload, String eventId) {
        try {
            Map<String, Object> data = new HashMap<>(payload);
            if (eventId != null && !eventId.isBlank()) data.put("eventId", eventId);
            db().collection(collection).document(document).set(data).get();
            return "Datos guardados correctamente";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al guardar datos";
        }
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private Firestore db() { return FirestoreClient.getFirestore(); }
}
