package com.example1.battlepokemon.model;

public class AttackRequest {
    private String attacker;
    private int damage;

    public AttackRequest() {
    }

    public AttackRequest(String attacker, int damage) {
        this.attacker = attacker;
        this.damage = damage;
    }

    public String getAttacker() {
        return attacker;
    }

    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}