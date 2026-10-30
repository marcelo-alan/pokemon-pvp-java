package org.example.model;

public class Gyarados extends Pokemon {
    public Gyarados(int nivel) {
        super("Gyarados", nivel, 140, 125, 79, TipoEnum.AGUA);
        this.adicionarMovimento(new Movimento("Cascata", TipoEnum.AGUA, 70, false, 0));
        this.adicionarMovimento(new Movimento("Hidro Bomba", TipoEnum.AGUA, 110, true, 3));
        this.adicionarMovimento(new Movimento("Mordida", TipoEnum.NORMAL, 60, false, 0));
    }
}