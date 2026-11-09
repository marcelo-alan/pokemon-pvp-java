package org.example.gui;

import org.example.model.Pokemon;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/** Janela principal: guarda os dados da partida e troca entre as telas. */
public class JanelaJogo extends JFrame {
    private String nome1 = "Treinador 1";
    private String nome2 = "Treinador 2";
    private List<Pokemon> equipe1;
    private List<Pokemon> equipe2;

    public JanelaJogo() {
        super("Pokémon PVP");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 640));
        setSize(1280, 780);
        setLocationRelativeTo(null);
        mostrarInicio();
    }

    public void mostrar(JPanel tela) {
        setContentPane(tela);
        revalidate();
        repaint();
        tela.requestFocusInWindow();
        if (tela instanceof TelaBatalha batalha) {
            batalha.iniciar();
        }
    }

    public void mostrarInicio() {
        mostrar(new TelaInicio(this));
    }

    public void iniciarJogo(String n1, String n2) {
        nome1 = n1;
        nome2 = n2;
        mostrar(new TelaSelecao(this, 1, nome1));
    }

    /** Chamado quando um treinador termina de escolher seus 3 Pokémon. */
    public void equipeConfirmada(int jogador, List<Pokemon> equipe) {
        if (jogador == 1) {
            equipe1 = equipe;
            mostrar(new TelaPassagem(this, "Passe o computador para", nome2,
                    "Escolha seus Pokémon em segredo!", "CONTINUAR",
                    () -> mostrar(new TelaSelecao(this, 2, nome2))));
        } else {
            equipe2 = equipe;
            mostrar(new TelaPassagem(this, "Tudo pronto!", nome1 + "  x  " + nome2,
                    "Que comece a batalha!", "BATALHAR!",
                    () -> mostrar(new TelaBatalha(this, nome1, nome2, equipe1, equipe2))));
        }
    }

    public void mostrarFim(String vencedor) {
        mostrar(new TelaFim(this, vencedor, nome1, nome2));
    }

    /** Revanche: mesmos treinadores, equipes novas. */
    public void revanche() {
        mostrar(new TelaSelecao(this, 1, nome1));
    }
}
