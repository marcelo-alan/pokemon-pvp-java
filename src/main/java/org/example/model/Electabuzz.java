package org.example.model;

public class Electabuzz extends Pokemon {
    public Electabuzz(int nivel) {
        super("Electabuzz", nivel, 120, 105, 75, TipoEnum.ELETRICO);
        this.adicionarMovimento(new Movimento("Soco Relâmpago", TipoEnum.ELETRICO, 65, false, 0));
        this.adicionarMovimento(new Movimento("Trovão", TipoEnum.ELETRICO, 95, true, 3, "Paralisado", 0.5));
        this.adicionarMovimento(new Movimento("Investida", TipoEnum.NORMAL, 50, false, 0));
    }
}