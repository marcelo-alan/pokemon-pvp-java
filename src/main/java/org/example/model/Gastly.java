package org.example.model;

public class Gastly extends Pokemon {
    public Gastly(int nivel) {
        super("Gastly", nivel, 30, 35, 30, TipoEnum.FANTASMA);
        this.adicionarMovimento(new Movimento("Língua", TipoEnum.VENENOSO, 30, false, 0));
        this.adicionarMovimento(new Movimento("Bola Sombria", TipoEnum.FANTASMA, 55, false, 0));
        this.adicionarMovimento(new Movimento("Come-Noite", TipoEnum.FANTASMA, 80, true, 3, "Envenenado", 0.5));
    }
}