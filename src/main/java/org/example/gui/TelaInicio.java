package org.example.gui;

import javax.swing.*;
import java.awt.*;

/** Tela de abertura: título e nomes dos dois treinadores. */
public class TelaInicio extends JPanel {
    private final JTextField campo1 = criarCampo("Treinador 1");
    private final JTextField campo2 = criarCampo("Treinador 2");
    private final BotaoPoke botao = new BotaoPoke("COMEÇAR", Estilo.VERDE);
    private final JLabel rotulo1 = criarRotulo("Treinador 1");
    private final JLabel rotulo2 = criarRotulo("Treinador 2");

    public TelaInicio(JanelaJogo janela) {
        setLayout(null);
        add(rotulo1);
        add(campo1);
        add(rotulo2);
        add(campo2);
        add(botao);
        botao.addActionListener(e -> {
            String n1 = campo1.getText().trim();
            String n2 = campo2.getText().trim();
            janela.iniciarJogo(n1.isEmpty() ? "Treinador 1" : n1, n2.isEmpty() ? "Treinador 2" : n2);
        });
        campo1.addActionListener(e -> campo2.requestFocusInWindow());
        campo2.addActionListener(e -> botao.doClick());
    }

    private static JTextField criarCampo(String texto) {
        JTextField c = new JTextField(texto);
        c.setHorizontalAlignment(JTextField.CENTER);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Estilo.BORDA, 3, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        c.setCaretColor(Estilo.BORDA);
        c.setSelectionColor(Estilo.AMARELO);
        return c;
    }

    private static JLabel criarRotulo(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(Color.WHITE);
        return l;
    }

    @Override
    public void doLayout() {
        int w = getWidth(), h = getHeight();
        int fw = (int) (w * 0.34);
        int x = (w - fw) / 2;
        int y = (int) (h * 0.50);
        int campoH = (int) (h * 0.075);
        int rotH = (int) (h * 0.04);
        Font fCampo = Estilo.fonte(campoH * 0.5f);
        Font fRot = Estilo.fonte(rotH * 0.75f);
        rotulo1.setFont(fRot);
        rotulo2.setFont(fRot);
        campo1.setFont(fCampo);
        campo2.setFont(fCampo);
        rotulo1.setBounds(x, y, fw, rotH);
        campo1.setBounds(x, y + rotH, fw, campoH);
        int y2 = y + rotH + campoH + (int) (h * 0.025);
        rotulo2.setBounds(x, y2, fw, rotH);
        campo2.setBounds(x, y2 + rotH, fw, campoH);
        int bh = (int) (h * 0.095);
        botao.setBounds(x, y2 + rotH + campoH + (int) (h * 0.035), fw, bh);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        int w = getWidth(), h = getHeight();
        g.drawImage(Estilo.fundoExplosao(w, h), 0, 0, null);

        // Pokémon decorativos
        Estilo.pixelado(g);
        desenharSprite(g, Sprites.costas("Charmander"), (int) (w * 0.17), (int) (h * 0.80), h / 90.0);
        desenharSprite(g, Sprites.frente("Squirtle"), (int) (w * 0.84), (int) (h * 0.80), h / 90.0);
        Estilo.suavizar(g);

        // título
        float tam = Math.min(w * 0.12f, h * 0.20f);
        Estilo.logoCentralizado(g, "POKÉMON", Estilo.fonte(tam), w / 2f, h * 0.22f, tam * 0.12f);
        Font fSub = Estilo.fonte(tam * 0.38f);
        Estilo.logoCentralizado(g, "BATALHA PVP", fSub, w / 2f, h * 0.22f + tam * 0.62f, tam * 0.05f);

        // caixa do formulário
        int fw = (int) (w * 0.40), fh = (int) (h * 0.40);
        int fx = (w - fw) / 2, fy = (int) (h * 0.47);
        g.setColor(new Color(20, 30, 70, 175));
        g.fillRoundRect(fx, fy, fw, fh, 36, 36);
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(4f));
        g.drawRoundRect(fx, fy, fw, fh, 36, 36);
        g.dispose();
    }

    private void desenharSprite(Graphics2D g, Sprites.Animacao anim, int cx, int baseY, double escala) {
        int w = (int) (anim.largura() * escala), h = (int) (anim.altura() * escala);
        g.setColor(new Color(0, 0, 0, 60));
        g.fillOval(cx - w / 2, baseY - h / 12, w, h / 6);
        g.drawImage(anim.quadro(), cx - w / 2, baseY - h, w, h, null);
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
}
