package org.example;

import org.example.gui.JanelaJogo;

import javax.swing.*;

/** Ponto de entrada da versão com interface gráfica. (A versão de console continua na classe Main.) */
public class MainGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JanelaJogo janela = new JanelaJogo();
            janela.setVisible(true);
        });
    }
}
