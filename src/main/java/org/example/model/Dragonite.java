package org.example.model;

public class Dragonite extends Pokemon {
    public Dragonite(int nivel) {
        super("Dragonite", nivel, 150, 130, 90, TipoEnum.DRAGAO);
        this.adicionarMovimento(new Movimento("Soco do Dragão", TipoEnum.DRAGAO, 70, false, 0));
        this.adicionarMovimento(new Movimento("Garra de Dragão", TipoEnum.DRAGAO, 90, true, 2, "Assustado", 0.4));
        this.adicionarMovimento(new Movimento("Hyper Raio", TipoEnum.NORMAL, 120, true, 3));
    }
}