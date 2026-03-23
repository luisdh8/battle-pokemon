package com.example1.battlepokemon.controller;

import com.example1.battlepokemon.model.AttackRequest;
import com.example1.battlepokemon.model.BattleState;
import com.example1.battlepokemon.service.BattleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/battle")
@CrossOrigin(origins = "http://localhost:5173")
public class BattleController {

    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping
    public BattleState getBattle() {
        return battleService.getBattle();
    }

    @PostMapping("/attack")
    public BattleState attack(@RequestBody AttackRequest request) {
        return battleService.attack(request);
    }
}