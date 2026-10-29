package org.example.model;

public class Squirtle extends Pokemon {
    public Squirtle(int nivel) {
        super("Squirtle", nivel, 44, 48, 65, TipoEnum.AGUA);
        this.adicionarMovimento(new Movimento("Investida", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Revólver de Água", TipoEnum.AGUA, 40, false, 0));
        this.adicionarMovimento(new Movimento("Jato d'Água", TipoEnum.AGUA, 75, true, 2)); // Especial
    }
}