package org.example.model;

public class Jigglypuff extends Pokemon {
    public Jigglypuff(int nivel) {
        super("Jigglypuff", nivel, 115, 45, 20, TipoEnum.NORMAL);
        this.adicionarMovimento(new Movimento("Tapa", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Desabafo", TipoEnum.NORMAL, 50, false, 0));
        this.adicionarMovimento(new Movimento("Hiper Raio", TipoEnum.NORMAL, 90, true, 3)); // Especial
    }
}