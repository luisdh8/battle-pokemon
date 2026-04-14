package com.example1.battlepokemon.model;

public class SelectRequest {
    private String slot;   // "pokemon1" | "pokemon2"
    private String name;
    private int hp;

    public SelectRequest() {}

    public String getSlot()  { return slot; }
    public void setSlot(String slot) { this.slot = slot; }

    public String getName()  { return name; }
    public void setName(String name) { this.name = name; }

    public int getHp()       { return hp; }
    public void setHp(int hp) { this.hp = hp; }
}
