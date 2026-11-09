package org.example.service;

/**
 * Guarda o resultado de um ataque (dano, eficácia, STAB, efeito aplicado...).
 * Assim tanto o console quanto a interface gráfica podem mostrar o que aconteceu
 * sem repetir a lógica de cálculo.
 */
public class ResultadoAtaque {
    private final int dano;
    private final double multiplicadorTipo;
    private final boolean stab;
    private final String efeitoAplicado; // null se nenhum efeito foi aplicado
    private final boolean defensorDesmaiou;

    public ResultadoAtaque(int dano, double multiplicadorTipo, boolean stab,
                           String efeitoAplicado, boolean defensorDesmaiou) {
        this.dano = dano;
        this.multiplicadorTipo = multiplicadorTipo;
        this.stab = stab;
        this.efeitoAplicado = efeitoAplicado;
        this.defensorDesmaiou = defensorDesmaiou;
    }

    public int getDano() { return dano; }
    public double getMultiplicadorTipo() { return multiplicadorTipo; }
    public boolean isStab() { return stab; }
    public String getEfeitoAplicado() { return efeitoAplicado; }
    public boolean isDefensorDesmaiou() { return defensorDesmaiou; }
}
