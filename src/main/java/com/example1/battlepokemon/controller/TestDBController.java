package com.example1.battlepokemon.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example1.battlepokemon.service.BattleEventPublisher;

/**
 * Controlador de prueba para publicar eventos de escritura hacia Firestore.
 *
 * <p>
 * No escribe directamente en la base de datos: solo construye el payload y
 * publica eventos para que sean procesados por los listeners.
 * </p>
 */
@RestController
public class TestDBController {
    private final BattleEventPublisher battleEventPublisher;

    public TestDBController(BattleEventPublisher battleEventPublisher) {
        this.battleEventPublisher = battleEventPublisher;
    }

    /**
     * Publica un evento de prueba con datos de un Pokémon por defecto.
     *
     * @return mensaje con el identificador del evento publicado
     */
    @GetMapping("/firebase-battle-pokemon")
    public String testFirebase() {

        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", "Pokemon 1");
        payload.put("vida", 100);

        String eventId = battleEventPublisher.publishFirebaseWriteEvent("battle-pokemon", "pokemon1", payload);

        return "Evento publicado para Firebase en battle-pokemon/pokemon1. eventId=" + eventId;
    }

    /**
     * Publica un evento para crear o actualizar un Pokémon en Firestore.
     *
     * @param pokemonId identificador del documento a crear/actualizar
     * @param request   cuerpo con nombre y vida del Pokémon
     * @return mensaje con el identificador del evento publicado
     */
    @PostMapping("/battle-pokemon/{pokemonId}")
    public String upsertPokemon(@PathVariable String pokemonId, @RequestBody PokemonWriteRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("nombre", request.nombre());
        payload.put("vida", request.vida());

        String eventId = battleEventPublisher.publishFirebaseWriteEvent("battle-pokemon", pokemonId, payload);
        return "Evento publicado para Firebase en battle-pokemon/" + pokemonId + ". eventId=" + eventId;
    }

    /**
     * DTO de entrada para la escritura de un Pokémon.
     *
     * @param nombre nombre del Pokémon
     * @param vida   puntos de vida del Pokémon
     */
    public record PokemonWriteRequest(String nombre, Integer vida) {
    }
}
