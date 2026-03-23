package com.example1.battlepokemon.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example1.battlepokemon.event.FirebaseWriteRequestedEvent;
import com.example1.battlepokemon.service.FirebaseService;

/**
 * Listener que consume eventos de escritura y delega la persistencia a
 * Firebase.
 *
 * <p>
 * Actúa como consumidor en la arquitectura basada en eventos: recibe el
 * evento y ejecuta la operación concreta de guardado en Firestore.
 * </p>
 */
@Component
public class FirebaseWriteEventListener {

    private final FirebaseService firebaseService;

    public FirebaseWriteEventListener(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    /**
     * Maneja eventos de solicitud de escritura en Firestore.
     *
     * @param event evento con destino y datos para persistir
     */
    @EventListener
    public void handleFirebaseWriteRequested(FirebaseWriteRequestedEvent event) {
        firebaseService.guardarDato(event.collection(), event.document(), event.payload(), event.eventId());
    }
}
