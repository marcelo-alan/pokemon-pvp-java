package org.example.model;

public class Psyduck extends Pokemon {
    public Psyduck(int nivel) {
        super("Psyduck", nivel, 50, 52, 48, TipoEnum.AGUA);
        this.adicionarMovimento(new Movimento("Arranhão", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Revólver de Água", TipoEnum.AGUA, 40, false, 0));
        this.adicionarMovimento(new Movimento("Confusão", TipoEnum.PSIQUICO, 65, true, 2)); // Especial
    }
}