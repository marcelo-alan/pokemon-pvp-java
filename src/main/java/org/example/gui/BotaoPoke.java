package org.example.gui;

import javax.swing.*;
import java.awt.*;

/** Botão arredondado e colorido, no estilo dos menus de Pokémon. */
public class BotaoPoke extends JButton {
    private Color cor;
    private String linha2;
    private Image icone;

    public BotaoPoke(String texto, Color cor) {
        super(texto);
        this.cor = cor;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFocusable(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setCor(Color cor) { this.cor = cor; repaint(); }

    public void setLinha2(String linha2) { this.linha2 = linha2; repaint(); }

    public void setIcone(Image icone) { this.icone = icone; repaint(); }

    public Image getIcone() { return icone; }

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        int w = getWidth(), h = getHeight();
        int arc = Math.max(12, h / 3);
        boolean ativo = isEnabled();
        Color base = ativo ? cor : new Color(150, 152, 160);
        if (ativo && getModel().isRollover()) base = base.brighter();
        boolean apertado = ativo && getModel().isPressed();
        if (apertado) base = base.darker();
        int dy = apertado ? 3 : 0;

        g.setColor(new Color(0, 0, 0, 80));
        g.fillRoundRect(2, 6, w - 6, h - 8, arc, arc);
        g.setPaint(new GradientPaint(0, dy, base.brighter(), 0, h, base.darker()));
        g.fillRoundRect(0, dy, w - 4, h - 7, arc, arc);
        g.setColor(new Color(255, 255, 255, 70));
        g.fillRoundRect(6, dy + 4, w - 16, (h - 7) / 3, arc, arc);
        g.setColor(Estilo.BORDA);
        g.setStroke(new BasicStroke(3.5f));
        g.drawRoundRect(0, dy, w - 4, h - 7, arc, arc);

        int areaX = 10, areaW = w - 24;
        if (icone != null) {
            int iw = Math.max(1, icone.getWidth(null)), ih = Math.max(1, icone.getHeight(null));
            double esc = Math.min((h - 14) * 0.95 / ih, (w * 0.28) / iw);
            int dw = (int) (iw * esc), dh = (int) (ih * esc);
            Estilo.pixelado(g);
            g.drawImage(icone, 10, dy + (h - 7 - dh) / 2, dw, dh, null);
            areaX += dw + 8;
            areaW -= dw + 8;
        }

        Color corTexto = ativo ? Color.WHITE : new Color(228, 228, 232);
        boolean doisLinhas = linha2 != null;
        String t = getText();
        float tamanho = h * (doisLinhas ? 0.28f : 0.25f);
        g.setFont(Estilo.fonte(tamanho));
        while (g.getFontMetrics().stringWidth(t) > areaW && tamanho > 9) {
            tamanho -= 1;
            g.setFont(Estilo.fonte(tamanho));
        }
        FontMetrics fm = g.getFontMetrics();
        int ty = doisLinhas ? dy + (int) (h * 0.40) : dy + (h - 7 + fm.getAscent() - fm.getDescent()) / 2;
        Estilo.textoSombra(g, t, areaX + (areaW - fm.stringWidth(t)) / 2, ty, corTexto);
        if (doisLinhas) {
            float t2 = h * 0.19f;
            g.setFont(Estilo.fonte(t2));
            while (g.getFontMetrics().stringWidth(linha2) > areaW && t2 > 8) {
                t2 -= 1;
                g.setFont(Estilo.fonte(t2));
            }
            FontMetrics fm2 = g.getFontMetrics();
            Estilo.textoSombra(g, linha2, areaX + (areaW - fm2.stringWidth(linha2)) / 2, dy + (int) (h * 0.70), corTexto);
        }
        g.dispose();
    }
}
