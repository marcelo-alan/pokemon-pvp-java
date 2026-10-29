package org.example.model;

public class Bulbasaur extends Pokemon {
    public Bulbasaur(int nivel) {
        super("Bulbasaur", nivel, 45, 49, 49, TipoEnum.PLANTA);
        this.adicionarMovimento(new Movimento("Investida", TipoEnum.NORMAL, 40, false, 0));
        this.adicionarMovimento(new Movimento("Chicote de Cipó", TipoEnum.PLANTA, 45, false, 0));
        this.adicionarMovimento(new Movimento("Raio Solar", TipoEnum.PLANTA, 80, true, 3)); // Especial
    }
}