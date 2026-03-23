package com.example1.battlepokemon.service;

import com.example1.battlepokemon.model.AttackRequest;
import com.example1.battlepokemon.model.BattleState;
import com.example1.battlepokemon.model.Pokemon;
import org.springframework.stereotype.Service;

@Service
public class BattleService {

    private final BattleState battleState;

    public BattleService() {
        this.battleState = new BattleState(
                new Pokemon("Pikachu", 100),
                new Pokemon("Charmander", 100),
                "pokemon1",
                null
        );
    }

    public BattleState getBattle() {
        return battleState;
    }

    public BattleState attack(AttackRequest request) {
        if (battleState.getWinner() != null) {
            return battleState;
        }

        String attacker = request.getAttacker();
        int damage = request.getDamage();

        if (!battleState.getTurn().equals(attacker)) {
            return battleState;
        }

        if ("pokemon1".equals(attacker)) {
            int newHp = Math.max(0, battleState.getPokemon2().getHp() - damage);
            battleState.getPokemon2().setHp(newHp);

            if (newHp == 0) {
                battleState.setWinner("pokemon1");
                battleState.setTurn(null);
            } else {
                battleState.setTurn("pokemon2");
            }
        } else if ("pokemon2".equals(attacker)) {
            int newHp = Math.max(0, battleState.getPokemon1().getHp() - damage);
            battleState.getPokemon1().setHp(newHp);

            if (newHp == 0) {
                battleState.setWinner("pokemon2");
                battleState.setTurn(null);
            } else {
                battleState.setTurn("pokemon1");
            }
        }

        return battleState;
    }
}