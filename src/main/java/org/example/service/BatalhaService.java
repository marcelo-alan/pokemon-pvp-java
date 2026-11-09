package org.example.service;

import org.example.model.Movimento;
import org.example.model.Pokemon;

import java.util.Random;

public class BatalhaService {
    private final Random random = new Random();

    /**
     * Calcula e APLICA o ataque (dano e efeito negativo), sem imprimir nada.
     * É usado pela interface gráfica e também pela versão de console.
     */
    public ResultadoAtaque calcularAtaque(Pokemon atacante, Pokemon defensor, Movimento movimento) {
        // 1. Verifica STAB (Bônus de Tipo: se o movimento é do mesmo tipo do Pokémon)
        double stab = (movimento.getTipo() == atacante.getTipo()) ? 1.5 : 1.0;

        // 2. Calcula a eficácia do tipo (Vantagem / Desvantagem)
        double multiplicadorTipo = movimento.getTipo().getMultiplicadorContra(defensor.getTipo());

        // 3. Fórmula de dano
        double relacaoAtaqueDefesa = (double) atacante.getAtaque() / Math.max(1, defensor.getDefesa());
        double danoBase = (((2.0 * atacante.getNivel() / 5.0 + 2.0) * movimento.getPoder() * relacaoAtaqueDefesa) / 50.0) + 2.0;

        // 4. Aplica STAB, Multiplicador de Tipo e Variação Aleatória (entre 85% e 100% de dano)
        double fatorAleatorio = 0.85 + (random.nextDouble() * 0.15);
        int danoFinal = (int) Math.max(1, danoBase * stab * multiplicadorTipo * fatorAleatorio);

        // 5. Aplica o dano no defensor
        defensor.receberDano(danoFinal);

        // 6. Verifica se o movimento aplica Efeito Negativo (Status)
        String efeitoAplicado = null;
        if (movimento.isAplicaEfeito()) {
            if (Math.random() <= movimento.getChanceEfeito()) {
                defensor.setCondicaoNegativa(movimento.getNomeEfeito());
                efeitoAplicado = movimento.getNomeEfeito();
            }
        }

        return new ResultadoAtaque(danoFinal, multiplicadorTipo, stab > 1.0,
                efeitoAplicado, defensor.estaDesmaiado());
    }

    /** Versão de console: calcula o ataque e mostra as mensagens no terminal. */
    public void executarAtaque(Pokemon atacante, Pokemon defensor, Movimento movimento) {
        System.out.println(atacante.getNome() + " usou " + movimento.getNome() + "!");

        ResultadoAtaque r = calcularAtaque(atacante, defensor, movimento);

        if (r.getEfeitoAplicado() != null) {
            System.out.println("⚠️ EFEITO NEGATIVO: O " + defensor.getNome() + " ficou " + r.getEfeitoAplicado() + "!");
        }

        double multiplicadorTipo = r.getMultiplicadorTipo();
        if (multiplicadorTipo > 1.0) {
            System.out.println("✨ Foi um golpe super eficaz!");
        } else if (multiplicadorTipo < 1.0 && multiplicadorTipo > 0.0) {
            System.out.println("📉 O golpe não foi muito eficaz...");
        } else if (multiplicadorTipo == 0.0) {
            System.out.println("❌ Não teve efeito nenhum!");
        }

        if (r.isStab()) {
            System.out.println("🔥 Bônus de STAB aplicado (ataque do mesmo tipo)!");
        }

        System.out.println(defensor.getNome() + " sofreu " + r.getDano() + " de dano. HP atual: " + defensor.getHpAtual() + "/" + defensor.getHpMaximo());

        if (defensor.estaDesmaiado()) {
            System.out.println("💀 " + defensor.getNome() + " desmaiou!");
        }
    }
}
