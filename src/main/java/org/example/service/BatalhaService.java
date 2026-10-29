package org.example.service;

import org.example.model.Movimento;
import org.example.model.Pokemon;
import org.example.model.TipoEnum;

import java.util.Random;

public class BatalhaService {
    private final Random random = new Random();

    public void executarAtaque(Pokemon atacante, Pokemon defensor, Movimento movimento) {
        System.out.println(atacante.getNome() + " usou " + movimento.getNome() + "!");

        // 1. Verifica STAB (Bônus de Tipo: se o movimento é do mesmo tipo do Pokémon)
        double stab = (movimento.getTipo() == atacante.getTipo()) ? 1.5 : 1.0;

        // 2. Calcula a eficácia do tipo (Vantagem / Desvantagem)
        double multiplicadorTipo = movimento.getTipo().getMultiplicadorContra(defensor.getTipo());

        // 3. Fórmula de Dano corrigida com o operador de multiplicação (*) em falta
        double relacaoAtaqueDefesa = (double) atacante.getAtaque() / Math.max(1, defensor.getDefesa());
        double danoBase = (((2.0 * atacante.getNivel() / 5.0 + 2.0) * movimento.getPoder() * relacaoAtaqueDefesa) / 50.0) + 2.0;

        // 4. Aplica STAB, Multiplicador de Tipo e Variação Aleatória (entre 85% e 100% de dano)
        double fatorAleatorio = 0.85 + (random.nextDouble() * 0.15);
        int danoFinal = (int) Math.max(1, danoBase * stab * multiplicadorTipo * fatorAleatorio);

        // 5. Aplica o dano no defensor
        defensor.receberDano(danoFinal);

        // 6. Verifica se o movimento aplica Efeito Negativo (Status)
        if (movimento.isAplicaEfeito()) {
            if (Math.random() <= movimento.getChanceEfeito()) {
                defensor.setCondicaoNegativa(movimento.getNomeEfeito());
                System.out.println("⚠️ EFEITO NEGATIVO: O " + defensor.getNome() + " ficou " + movimento.getNomeEfeito() + "!");
            }
        }

        // 7. Mensagens de feedback detalhadas
        if (multiplicadorTipo > 1.0) {
            System.out.println("✨ Foi um golpe super eficaz!");
        } else if (multiplicadorTipo < 1.0 && multiplicadorTipo > 0.0) {
            System.out.println("📉 O golpe não foi muito eficaz...");
        } else if (multiplicadorTipo == 0.0) {
            System.out.println("❌ Não teve efeito nenhum!");
        }

        if (stab > 1.0) {
            System.out.println("🔥 Bônus de STAB aplicado (ataque do mesmo tipo)!");
        }

        System.out.println(defensor.getNome() + " sofreu " + danoFinal + " de dano. HP atual: " + defensor.getHpAtual() + "/" + defensor.getHpMaximo());

        if (defensor.estaDesmaiado()) {
            System.out.println("💀 " + defensor.getNome() + " desmaiou!");
        }
    }
}