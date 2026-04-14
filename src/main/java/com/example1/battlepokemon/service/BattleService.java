package com.example1.battlepokemon.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example1.battlepokemon.model.AttackRequest;
import com.example1.battlepokemon.model.BattleState;
import com.example1.battlepokemon.model.BattleStatus;
import com.example1.battlepokemon.model.Pokemon;
import com.example1.battlepokemon.model.SelectRequest;

@Service
public class BattleService {

    private final FirebaseService firebaseService;

    public BattleService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    // ── Lobby ────────────────────────────────────────────────────────────────

    public boolean selectPokemon(SelectRequest req) {
        return firebaseService.setPokemon(req.getSlot(), req.getName(), req.getHp());
    }

    public BattleStatus getStatus() {
        return new BattleStatus(firebaseService.countRegisteredPokemon());
    }

    /** Limpia pokémon1, pokémon2 y el estado del turno. */
    public void resetBattle() {
        firebaseService.deletePokemon("pokemon1");
        firebaseService.deletePokemon("pokemon2");
        firebaseService.deleteEstado();
        // Pre-crea el turno para que no haya lag en la primera lectura
        firebaseService.setTurn("pokemon1");
    }

    // ── Batalla ──────────────────────────────────────────────────────────────

    /**
     * Lee pokémon + turno desde Firestore.
     * El turno es la fuente de verdad: viene del doc "estado", no se calcula.
     */
    public BattleState getBattle() {
        Map<String, Object> p1Data = firebaseService.getPokemon("pokemon1");
        Map<String, Object> p2Data = firebaseService.getPokemon("pokemon2");

        if (p1Data == null || p2Data == null) {
            return new BattleState(
                new Pokemon("Pokemon 1", 100),
                new Pokemon("Pokemon 2", 100),
                "pokemon1", null
            );
        }

        Pokemon pokemon1 = new Pokemon(
            (String) p1Data.get("nombre"),
            ((Number) p1Data.get("vida")).intValue()
        );
        Pokemon pokemon2 = new Pokemon(
            (String) p2Data.get("nombre"),
            ((Number) p2Data.get("vida")).intValue()
        );

        // Determinar ganador
        String winner = null;
        if (pokemon1.getHp() == 0)      winner = "pokemon2";
        else if (pokemon2.getHp() == 0) winner = "pokemon1";

        // Leer turno desde Firestore (fuente de verdad)
        String turn = (winner != null) ? null : firebaseService.getTurn();

        return new BattleState(pokemon1, pokemon2, turn, winner);
    }

    public BattleState attack(AttackRequest request) {
        BattleState current = getBattle();
        if (current.getWinner() != null) return current;

        String attacker = request.getAttacker();
        int    damage   = request.getDamage();
        String nextTurn;

        if ("pokemon1".equals(attacker)) {
            int newHp = Math.max(0, current.getPokemon2().getHp() - damage);
            firebaseService.updatePokemonHp("pokemon2", newHp);
            current.getPokemon2().setHp(newHp);

            if (newHp == 0) {
                current.setWinner("pokemon1");
                current.setTurn(null);
                firebaseService.setTurn("done");
            } else {
                nextTurn = "pokemon2";
                current.setTurn(nextTurn);
                firebaseService.setTurn(nextTurn);   // ← persiste el turno
            }

        } else if ("pokemon2".equals(attacker)) {
            int newHp = Math.max(0, current.getPokemon1().getHp() - damage);
            firebaseService.updatePokemonHp("pokemon1", newHp);
            current.getPokemon1().setHp(newHp);

            if (newHp == 0) {
                current.setWinner("pokemon2");
                current.setTurn(null);
                firebaseService.setTurn("done");
            } else {
                nextTurn = "pokemon1";
                current.setTurn(nextTurn);
                firebaseService.setTurn(nextTurn);   // ← persiste el turno
            }
        }

        return current;
    }
}
