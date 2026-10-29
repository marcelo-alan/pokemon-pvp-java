package org.example.model;

public class Geodude extends Pokemon {
    public Geodude(int nivel) {
        super("Geodude", nivel, 40, 80, 100, TipoEnum.PEDRA);
        this.adicionarMovimento(new Movimento("Investida", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Lançamento de Rocha", TipoEnum.PEDRA, 50, false, 0));
        this.adicionarMovimento(new Movimento("Terremoto", TipoEnum.TERRA, 80, true, 3)); // Especial
    }
}