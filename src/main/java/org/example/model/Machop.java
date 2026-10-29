package org.example.model;

public class Machop extends Pokemon {
    public Machop(int nivel) {
        super("Machop", nivel, 70, 80, 50, TipoEnum.LUTADOR);
        this.adicionarMovimento(new Movimento("Investida", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Golpe Caratê", TipoEnum.LUTADOR, 50, false, 0));
        this.adicionarMovimento(new Movimento("Soco Dinâmico", TipoEnum.LUTADOR, 75, true, 2)); // Especial
    }
}