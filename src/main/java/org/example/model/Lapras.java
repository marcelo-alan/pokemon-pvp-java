package org.example.model;

public class Lapras extends Pokemon {
    public Lapras(int nivel) {
        super("Lapras", nivel, 150, 95, 85, TipoEnum.AGUA);
        this.adicionarMovimento(new Movimento("Pistola de Água", TipoEnum.AGUA, 50, false, 0));
        this.adicionarMovimento(new Movimento("Raio de Gelo", TipoEnum.AGUA, 85, true, 2, "Congelado", 0.3));
        this.adicionarMovimento(new Movimento("Surfe", TipoEnum.AGUA, 90, true, 2));
    }
}