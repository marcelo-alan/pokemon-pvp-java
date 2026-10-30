package org.example.model;

public class Snorlax extends Pokemon {
    public Snorlax(int nivel) {
        super("Snorlax", nivel, 160, 110, 65, TipoEnum.NORMAL);
        this.adicionarMovimento(new Movimento("Investida", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Body Slam", TipoEnum.NORMAL, 85, true, 2, "Paralisado", 0.3));
        this.adicionarMovimento(new Movimento("Descanso", TipoEnum.NORMAL, 0, true, 3, "Dormindo", 1.0));
    }
}