package com.example1.battlepokemon.model;

public class BattleState {
    private Pokemon pokemon1;
    private Pokemon pokemon2;
    private String turn;
    private String winner;

    public BattleState() {
    }

    public BattleState(Pokemon pokemon1, Pokemon pokemon2, String turn, String winner) {
        this.pokemon1 = pokemon1;
        this.pokemon2 = pokemon2;
        this.turn = turn;
        this.winner = winner;
    }

    public Pokemon getPokemon1() {
        return pokemon1;
    }

    public void setPokemon1(Pokemon pokemon1) {
        this.pokemon1 = pokemon1;
    }

    public Pokemon getPokemon2() {
        return pokemon2;
    }

    public void setPokemon2(Pokemon pokemon2) {
        this.pokemon2 = pokemon2;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }
}