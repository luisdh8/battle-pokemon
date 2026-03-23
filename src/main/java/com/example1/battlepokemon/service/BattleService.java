package com.example1.battlepokemon.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example1.battlepokemon.model.AttackRequest;
import com.example1.battlepokemon.model.BattleState;
import com.example1.battlepokemon.model.Pokemon;

@Service
public class BattleService {

    private final FirebaseService firebaseService;

    public BattleService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public BattleState getBattle() {
        Map<String, Object> p1 = firebaseService.getPokemon("pokemon1");
        Map<String, Object> p2 = firebaseService.getPokemon("pokemon2");

        if (p1 == null || p2 == null) {
            return new BattleState(
                new Pokemon("Pokemon 1", 100),
                new Pokemon("Pokemon 2", 100),
                "pokemon1",
                null
            );
        }

        Pokemon pokemon1 = new Pokemon(
            (String) p1.get("nombre"),
            ((Number) p1.get("vida")).intValue()
        );

        Pokemon pokemon2 = new Pokemon(
            (String) p2.get("nombre"),
            ((Number) p2.get("vida")).intValue()
        );

        String winner = null;
        String turn = "pokemon1";

        if (pokemon1.getHp() == 0) {
            winner = "pokemon2";
            turn = null;
        } else if (pokemon2.getHp() == 0) {
            winner = "pokemon1";
            turn = null;
        }

        return new BattleState(pokemon1, pokemon2, turn, winner);
    }

    public BattleState attack(AttackRequest request) {
        BattleState currentBattle = getBattle();

        if (currentBattle.getWinner() != null) {
            return currentBattle;
        }

        String attacker = request.getAttacker();
        int damage = request.getDamage();

        if (!currentBattle.getTurn().equals(attacker)) {
            return currentBattle;
        }

        if ("pokemon1".equals(attacker)) {
            int newHp = Math.max(0, currentBattle.getPokemon2().getHp() - damage);
            firebaseService.updatePokemonHp("pokemon2", newHp);

            currentBattle.getPokemon2().setHp(newHp);

            if (newHp == 0) {
                currentBattle.setWinner("pokemon1");
                currentBattle.setTurn(null);
            } else {
                currentBattle.setTurn("pokemon2");
            }

        } else if ("pokemon2".equals(attacker)) {
            int newHp = Math.max(0, currentBattle.getPokemon1().getHp() - damage);
            firebaseService.updatePokemonHp("pokemon1", newHp);

            currentBattle.getPokemon1().setHp(newHp);

            if (newHp == 0) {
                currentBattle.setWinner("pokemon2");
                currentBattle.setTurn(null);
            } else {
                currentBattle.setTurn("pokemon1");
            }
        }

        return currentBattle;
    }
}