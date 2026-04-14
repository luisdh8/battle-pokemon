package com.example1.battlepokemon.model;

public class BattleStatus {
    private int registered;   // 0, 1 ó 2

    public BattleStatus() {}
    public BattleStatus(int registered) { this.registered = registered; }

    public int getRegistered() { return registered; }
    public void setRegistered(int registered) { this.registered = registered; }
}
