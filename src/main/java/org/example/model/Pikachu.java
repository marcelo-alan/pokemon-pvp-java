package org.example.model;

public class Pikachu extends Pokemon {
    public Pikachu(int nivel) {
        super("Pikachu", nivel, 35, 55, 40, TipoEnum.ELETRICO);
        this.adicionarMovimento(new Movimento("Ataque Rápido", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Choque do Trovão", TipoEnum.ELETRICO, 40, false, 0));
        this.adicionarMovimento(new Movimento("Trovão", TipoEnum.ELETRICO, 85, true, 3, "Paralisado", 0.4));
    }
}