package org.example.gui;

import javax.swing.*;
import java.awt.*;

/** Tela intermediária ("passe o computador"), para os treinadores não verem a escolha um do outro. */
public class TelaPassagem extends JPanel {
    private final String titulo;
    private final String nome;
    private final String aviso;
    private final BotaoPoke botao;

    public TelaPassagem(JanelaJogo janela, String titulo, String nome, String aviso,
                        String textoBotao, Runnable aoContinuar) {
        this.titulo = titulo;
        this.nome = nome;
        this.aviso = aviso;
        setLayout(null);
        botao = new BotaoPoke(textoBotao, Estilo.VERDE);
        botao.addActionListener(e -> aoContinuar.run());
        add(botao);
    }

    @Override
    public void doLayout() {
        int w = getWidth(), h = getHeight();
        int bw = (int) (w * 0.28), bh = (int) (h * 0.11);
        botao.setBounds((w - bw) / 2, (int) (h * 0.72), bw, bh);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        int w = getWidth(), h = getHeight();
        g.drawImage(Estilo.fundoExplosao(w, h), 0, 0, null);

        g.setColor(new Color(20, 30, 70, 175));
        int bx = (int) (w * 0.12), by = (int) (h * 0.14), bw = (int) (w * 0.76), bh = (int) (h * 0.50);
        g.fillRoundRect(bx, by, bw, bh, 40, 40);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(4f));
        g.drawRoundRect(bx, by, bw, bh, 40, 40);

        Font f1 = Estilo.fonte(h * 0.065f);
        g.setFont(f1);
        FontMetrics fm1 = g.getFontMetrics();
        Estilo.textoSombra(g, titulo, (w - fm1.stringWidth(titulo)) / 2, (int) (h * 0.27), Color.WHITE);

        float tam = Math.min(h * 0.14f, w * 0.9f / Math.max(6, nome.length()) * 1.6f);
        Estilo.logoCentralizado(g, nome, Estilo.fonte(tam), w / 2f, h * 0.44f, tam * 0.12f);

        g.setFont(Estilo.fonte(h * 0.045f));
        FontMetrics fm2 = g.getFontMetrics();
        Estilo.textoSombra(g, aviso, (w - fm2.stringWidth(aviso)) / 2, (int) (h * 0.57), new Color(255, 230, 120));
        g.dispose();
    }
}
