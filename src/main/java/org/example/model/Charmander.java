package org.example.model;

public class Charmander extends Pokemon {
    public Charmander(int nivel) {
        super("Charmander", nivel, 39, 52, 43, TipoEnum.FOGO);
        this.adicionarMovimento(new Movimento("Arranhão", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Brasa", TipoEnum.FOGO, 50, false, 0));
        this.adicionarMovimento(new Movimento("Lança-Chamas", TipoEnum.FOGO, 75, true, 2, "Queimado", 0.5));
    }
}