package org.example.model;

public class Eevee extends Pokemon {
    public Eevee(int nivel) {
        super("Eevee", nivel, 55, 55, 50, TipoEnum.NORMAL);
        this.adicionarMovimento(new Movimento("Investida", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Ataque Rápido", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Investida Dupla", TipoEnum.NORMAL, 70, true, 2)); // Especial
    }
}