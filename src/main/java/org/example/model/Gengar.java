package org.example.model;

public class Gengar extends Pokemon {
    public Gengar(int nivel) {
        super("Gengar", nivel, 110, 130, 60, TipoEnum.FANTASMA);
        this.adicionarMovimento(new Movimento("Língua Sombria", TipoEnum.FANTASMA, 50, false, 0));
        this.adicionarMovimento(new Movimento("Bola Sombria", TipoEnum.FANTASMA, 80, true, 2, "Assustado", 0.4));
        this.adicionarMovimento(new Movimento("Névoa Tóxica", TipoEnum.FANTASMA, 65, true, 3, "Envenenado", 0.6));
    }
}