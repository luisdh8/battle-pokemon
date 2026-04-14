package com.example1.battlepokemon.controller;

import com.example1.battlepokemon.model.AttackRequest;
import com.example1.battlepokemon.model.BattleState;
import com.example1.battlepokemon.model.BattleStatus;
import com.example1.battlepokemon.model.SelectRequest;
import com.example1.battlepokemon.service.BattleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/battle")
@CrossOrigin(origins = "http://localhost:5173")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    // ── Batalla ──────────────────────────────────────────────────────────────

    @GetMapping
    public BattleState getBattle() {
        return battleService.getBattle();
    }

    @PostMapping("/attack")
    public BattleState attack(@RequestBody AttackRequest request) {
        return battleService.attack(request);
    }

    // ── Lobby ────────────────────────────────────────────────────────────────

    /**
     * El jugador confirma su pokémon seleccionado.
     * Body: { "slot": "pokemon1", "name": "charmander", "hp": 78 }
     */
    @PostMapping("/select")
    public ResponseEntity<String> selectPokemon(@RequestBody SelectRequest request) {
        boolean ok = battleService.selectPokemon(request);
        return ok
            ? ResponseEntity.ok("Pokemon registrado")
            : ResponseEntity.internalServerError().body("Error al registrar");
    }

    /**
     * Devuelve cuántos pokémon están registrados: { "registered": 0|1|2 }
     */
    @GetMapping("/status")
    public BattleStatus getStatus() {
        return battleService.getStatus();
    }

    /**
     * Borra ambos slots para empezar una batalla nueva.
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetBattle() {
        battleService.resetBattle();
        return ResponseEntity.ok("Batalla reiniciada");
    }
}
