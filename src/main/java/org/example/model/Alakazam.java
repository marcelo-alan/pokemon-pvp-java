package org.example.model;

public class Alakazam extends Pokemon {
    public Alakazam(int nivel) {
        super("Alakazam", nivel, 105, 135, 55, TipoEnum.PSIQUICO);
        this.adicionarMovimento(new Movimento("Confusão", TipoEnum.PSIQUICO, 55, false, 0));
        this.adicionarMovimento(new Movimento("Psiquismo", TipoEnum.PSIQUICO, 90, true, 2, "Paralisado", 0.3));
        this.adicionarMovimento(new Movimento("Raio Psíquico", TipoEnum.PSIQUICO, 65, true, 1));
    }
}