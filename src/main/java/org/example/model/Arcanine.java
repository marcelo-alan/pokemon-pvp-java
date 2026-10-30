package org.example.model;

public class Arcanine extends Pokemon {
    public Arcanine(int nivel) {
        super("Arcanine", nivel, 130, 110, 80, TipoEnum.FOGO);
        this.adicionarMovimento(new Movimento("Investida de Fogo", TipoEnum.FOGO, 65, false, 0));
        this.adicionarMovimento(new Movimento("Lança-Chamas", TipoEnum.FOGO, 90, true, 2, "Queimado", 0.5));
        this.adicionarMovimento(new Movimento("Velocidade Extrema", TipoEnum.NORMAL, 75, false, 1));
    }
}