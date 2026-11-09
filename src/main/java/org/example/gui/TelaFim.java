package org.example.gui;

import javax.swing.*;
import java.awt.*;

/** Tela final com o vencedor. */
public class TelaFim extends JPanel {
    private final String vencedor;
    private final BotaoPoke revanche = new BotaoPoke("REVANCHE", Estilo.VERDE);
    private final BotaoPoke novoJogo = new BotaoPoke("NOVO JOGO", new Color(70, 130, 220));

    public TelaFim(JanelaJogo janela, String vencedor, String nome1, String nome2) {
        this.vencedor = vencedor;
        setLayout(null);
        add(revanche);
        add(novoJogo);
        revanche.addActionListener(e -> janela.revanche());
        novoJogo.addActionListener(e -> janela.mostrarInicio());
    }

    private Timer relogio;

    @Override
    public void addNotify() {
        super.addNotify();
        relogio = new Timer(70, e -> repaint());
        relogio.start();
    }

    @Override
    public void removeNotify() {
        if (relogio != null) relogio.stop();
        super.removeNotify();
    }

    @Override
    public void doLayout() {
        int w = getWidth(), h = getHeight();
        int bw = (int) (w * 0.24), bh = (int) (h * 0.11);
        int gap = (int) (w * 0.03);
        int y = (int) (h * 0.78);
        revanche.setBounds(w / 2 - bw - gap / 2, y, bw, bh);
        novoJogo.setBounds(w / 2 + gap / 2, y, bw, bh);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        int w = getWidth(), h = getHeight();
        g.drawImage(Estilo.fundoExplosao(w, h), 0, 0, null);

        g.setColor(new Color(20, 30, 70, 175));
        int bx = (int) (w * 0.12), by = (int) (h * 0.12), bw = (int) (w * 0.76), bh = (int) (h * 0.55);
        g.fillRoundRect(bx, by, bw, bh, 40, 40);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(4f));
        g.drawRoundRect(bx, by, bw, bh, 40, 40);

        float t1 = Math.min(w * 0.09f, h * 0.15f);
        Estilo.logoCentralizado(g, "VITÓRIA!", Estilo.fonte(t1), w / 2f, h * 0.30f, t1 * 0.12f);

        float t2 = Math.min(h * 0.12f, w * 0.9f / Math.max(6, vencedor.length()) * 1.6f);
        Estilo.logoCentralizado(g, vencedor, Estilo.fonte(t2), w / 2f, h * 0.46f, t2 * 0.12f);

        g.setFont(Estilo.fonte(h * 0.05f));
        String s = "é o novo Mestre Pokémon!";
        FontMetrics fm = g.getFontMetrics();
        Estilo.textoSombra(g, s, (w - fm.stringWidth(s)) / 2, (int) (h * 0.555), Color.WHITE);

        // desfile de Pokémon comemorando
        Estilo.pixelado(g);
        String[] nomes = {"Pikachu", "Charmander", "Bulbasaur", "Squirtle", "Eevee"};
        int n = nomes.length;
        for (int i = 0; i < n; i++) {
            Sprites.Animacao anim = Sprites.frente(nomes[i]);
            double esc = h / 330.0;
            int sw = (int) (anim.largura() * esc), sh = (int) (anim.altura() * esc);
            int cx = (int) (w * (0.14 + 0.72 * i / (n - 1)));
            g.drawImage(anim.quadro(), cx - sw / 2, (int) (h * 0.715) - sh, sw, sh, null);
        }
        g.dispose();
    }
}
