package org.example.gui;

import org.example.model.Movimento;
import org.example.model.Pokemon;
import org.example.service.CatalogoPokemon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/** Tela onde cada treinador escolhe seus 3 Pokémon. */
public class TelaSelecao extends JPanel {
    private static final int COLUNAS = 6;
    private static final int MAX_EQUIPE = 3;

    private final JanelaJogo janela;
    private final int jogador;
    private final String nomeTreinador;
    private final List<CatalogoPokemon.Entrada> entradas = CatalogoPokemon.todos();
    private final Pokemon[] modelos;                 // instâncias só para mostrar status e golpes
    private final List<Integer> escolhidos = new ArrayList<>();
    private int hover = -1;
    private int maxHp, maxAtk, maxDef;

    private final BotaoPoke btnConfirmar = new BotaoPoke("CONFIRMAR", Estilo.VERDE);
    private final BotaoPoke btnLimpar = new BotaoPoke("LIMPAR", new Color(220, 90, 70));

    public TelaSelecao(JanelaJogo janela, int jogador, String nomeTreinador) {
        this.janela = janela;
        this.jogador = jogador;
        this.nomeTreinador = nomeTreinador;
        this.modelos = new Pokemon[entradas.size()];
        for (int i = 0; i < modelos.length; i++) {
            modelos[i] = entradas.get(i).criar();
            maxHp = Math.max(maxHp, modelos[i].getHpMaximo());
            maxAtk = Math.max(maxAtk, modelos[i].getAtaque());
            maxDef = Math.max(maxDef, modelos[i].getDefesa());
        }
        setLayout(null);
        add(btnConfirmar);
        add(btnLimpar);
        btnConfirmar.setEnabled(false);
        btnConfirmar.addActionListener(e -> confirmar());
        btnLimpar.addActionListener(e -> { escolhidos.clear(); atualizarBotoes(); repaint(); });

        MouseAdapter mouse = new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int h = cartaEm(e.getPoint());
                if (h != hover) { hover = h; repaint(); }
                setCursor(Cursor.getPredefinedCursor(h >= 0 || slotEm(e.getPoint()) >= 0
                        ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
            @Override public void mouseExited(MouseEvent e) { hover = -1; repaint(); }
            @Override public void mouseClicked(MouseEvent e) { clicar(e.getPoint()); }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    // ------------------------------------------------------------------ lógica

    private void clicar(Point p) {
        int c = cartaEm(p);
        if (c >= 0) {
            if (escolhidos.contains(c)) {
                escolhidos.remove(Integer.valueOf(c));
            } else if (escolhidos.size() < MAX_EQUIPE) {
                escolhidos.add(c);
            }
        } else {
            int s = slotEm(p);
            if (s >= 0 && s < escolhidos.size()) {
                escolhidos.remove(s);
            }
        }
        atualizarBotoes();
        repaint();
    }

    private void atualizarBotoes() {
        btnConfirmar.setEnabled(escolhidos.size() == MAX_EQUIPE);
    }

    private void confirmar() {
        List<Pokemon> equipe = new ArrayList<>();
        for (int i : escolhidos) {
            equipe.add(entradas.get(i).criar());   // instâncias novas e com HP cheio
        }
        janela.equipeConfirmada(jogador, equipe);
    }

    // ------------------------------------------------------------------ geometria

    private Rectangle area() { return new Rectangle(0, 0, getWidth(), getHeight()); }

    private int margem() { return (int) (getWidth() * 0.02); }

    private int topo() { return (int) (getHeight() * 0.14); }

    private Rectangle grade() {
        int m = margem();
        return new Rectangle(m, topo(), (int) (getWidth() * 0.62), getHeight() - topo() - m);
    }

    private Rectangle carta(int i) {
        Rectangle g = grade();
        int linhas = (int) Math.ceil(entradas.size() / (double) COLUNAS);
        int gap = (int) (getWidth() * 0.009);
        int cw = (g.width - gap * (COLUNAS - 1)) / COLUNAS;
        int ch = (g.height - gap * (linhas - 1)) / linhas;
        int col = i % COLUNAS, lin = i / COLUNAS;
        return new Rectangle(g.x + col * (cw + gap), g.y + lin * (ch + gap), cw, ch);
    }

    private int cartaEm(Point p) {
        for (int i = 0; i < entradas.size(); i++) if (carta(i).contains(p)) return i;
        return -1;
    }

    private Rectangle painelLateral() {
        Rectangle g = grade();
        int x = g.x + g.width + margem();
        return new Rectangle(x, g.y, getWidth() - x - margem(), g.height);
    }

    private Rectangle painelDetalhe() {
        Rectangle l = painelLateral();
        return new Rectangle(l.x, l.y, l.width, (int) (l.height * 0.60));
    }

    private Rectangle slot(int i) {
        Rectangle l = painelLateral();
        Rectangle d = painelDetalhe();
        int gap = (int) (getWidth() * 0.008);
        int y = d.y + d.height + gap;
        int h = (int) (l.height * 0.20);
        int w = (l.width - gap * (MAX_EQUIPE - 1)) / MAX_EQUIPE;
        return new Rectangle(l.x + i * (w + gap), y, w, h);
    }

    private int slotEm(Point p) {
        for (int i = 0; i < MAX_EQUIPE; i++) if (slot(i).contains(p)) return i;
        return -1;
    }

    @Override
    public void doLayout() {
        Rectangle l = painelLateral();
        Rectangle s = slot(0);
        int gap = (int) (getWidth() * 0.008);
        int y = s.y + s.height + gap;
        int h = Math.max(30, l.y + l.height - y);
        int w = (l.width - gap) / 2;
        btnLimpar.setBounds(l.x, y, w, h);
        btnConfirmar.setBounds(l.x + w + gap, y, w, h);
    }

    // ------------------------------------------------------------------ desenho

    @Override
    protected void paintComponent(Graphics g0) {
        Graphics2D g = (Graphics2D) g0.create();
        Estilo.suavizar(g);
        int w = getWidth(), h = getHeight();
        g.drawImage(Estilo.fundoExplosao(w, h), 0, 0, null);
        g.setColor(new Color(10, 20, 60, 110));
        g.fillRect(0, 0, w, h);

        // título
        float tam = h * 0.065f;
        String titulo = nomeTreinador + ", escolha seus 3 Pokémon";
        Estilo.logoCentralizado(g, titulo, Estilo.fonte(tam), w * 0.38f, h * 0.095f, tam * 0.10f);
        g.setFont(Estilo.fonte(h * 0.04f));
        String cont = escolhidos.size() + "/" + MAX_EQUIPE;
        FontMetrics fmc = g.getFontMetrics();
        Estilo.textoSombra(g, cont, w - margem() - fmc.stringWidth(cont) - (int) (w * 0.02), (int) (h * 0.095),
                escolhidos.size() == MAX_EQUIPE ? new Color(140, 255, 150) : Color.WHITE);

        for (int i = 0; i < entradas.size(); i++) desenharCarta(g, i);
        desenharDetalhe(g);
        for (int i = 0; i < MAX_EQUIPE; i++) desenharSlot(g, i);
        g.dispose();
    }

    private void desenharCarta(Graphics2D g, int i) {
        Rectangle r = carta(i);
        boolean sel = escolhidos.contains(i);
        boolean sobre = i == hover;
        int arc = Math.max(14, r.height / 6);

        g.setColor(new Color(0, 0, 0, 70));
        g.fillRoundRect(r.x + 3, r.y + 5, r.width, r.height, arc, arc);
        g.setColor(sel ? new Color(255, 248, 200) : sobre ? new Color(255, 255, 255) : new Color(240, 244, 255, 235));
        g.fillRoundRect(r.x, r.y, r.width, r.height, arc, arc);
        g.setColor(sel ? new Color(255, 196, 0) : Estilo.BORDA);
        g.setStroke(new BasicStroke(sel ? 6f : sobre ? 5f : 3.5f));
        g.drawRoundRect(r.x, r.y, r.width, r.height, arc, arc);

        Pokemon p = modelos[i];
        desenharIcone(g, Sprites.icone(p.getNome()), r.x + 4, r.y + (int) (r.height * 0.03),
                r.width - 8, (int) (r.height * 0.52));

        String nome = p.getNome();
        float tamNome = r.height * 0.115f;
        g.setFont(Estilo.fonte(tamNome));
        while (g.getFontMetrics().stringWidth(nome) > r.width - 10 && tamNome > 8) {
            tamNome -= 1;
            g.setFont(Estilo.fonte(tamNome));
        }
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Estilo.BORDA);
        g.drawString(nome, r.x + (r.width - fm.stringWidth(nome)) / 2, r.y + (int) (r.height * 0.69));

        int chipH = (int) (r.height * 0.13);
        String textoTipo = Estilo.nomeTipo(p.getTipo()).toUpperCase();
        int chipW;
        while (true) {
            g.setFont(Estilo.fonte(chipH * 0.58f));
            chipW = g.getFontMetrics().stringWidth(textoTipo) + chipH;
            if (chipW <= r.width - 10 || chipH <= 14) break;
            chipH -= 1;
        }
        Estilo.chipTipo(g, p.getTipo(), r.x + (r.width - chipW) / 2, r.y + (int) (r.height * 0.78), chipH);

        if (sel) {
            int n = escolhidos.indexOf(i) + 1;
            int d = (int) (r.height * 0.22);
            g.setColor(new Color(255, 196, 0));
            g.fillOval(r.x + r.width - d - 4, r.y + 4, d, d);
            g.setColor(Estilo.BORDA);
            g.setStroke(new BasicStroke(3f));
            g.drawOval(r.x + r.width - d - 4, r.y + 4, d, d);
            g.setFont(Estilo.fonte(d * 0.62f));
            FontMetrics f2 = g.getFontMetrics();
            String s = String.valueOf(n);
            g.drawString(s, r.x + r.width - d - 4 + (d - f2.stringWidth(s)) / 2, r.y + 4 + (d + f2.getAscent() - f2.getDescent()) / 2);
        }
    }

    private void desenharDetalhe(Graphics2D g) {
        Rectangle r = painelDetalhe();
        g.setColor(new Color(0, 0, 0, 70));
        g.fillRoundRect(r.x + 3, r.y + 5, r.width, r.height, 28, 28);
        g.setColor(Estilo.CREME);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 28, 28);
        g.setColor(Estilo.BORDA);
        g.setStroke(new BasicStroke(4f));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 28, 28);

        int foco = hover >= 0 ? hover : (escolhidos.isEmpty() ? -1 : escolhidos.get(escolhidos.size() - 1));
        if (foco < 0) {
            g.setFont(Estilo.fonte(r.height * 0.07f));
            FontMetrics fm = g.getFontMetrics();
            String[] linhas = {"Passe o mouse sobre um Pokémon", "para ver seus status e golpes.", "", "Clique para adicionar à equipe!"};
            int y = r.y + r.height / 2 - linhas.length * fm.getHeight() / 2;
            g.setColor(Estilo.BORDA);
            for (String l : linhas) {
                y += fm.getHeight();
                g.drawString(l, r.x + (r.width - fm.stringWidth(l)) / 2, y);
            }
            return;
        }
        Pokemon p = modelos[foco];
        float u = r.height / 13.2f;
        int pad = (int) (u * 0.7);

        g.setFont(Estilo.fonte(u * 1.05f));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Estilo.BORDA);
        g.drawString(p.getNome(), r.x + pad, r.y + (int) (u * 1.4));
        g.setFont(Estilo.fonte(u * 0.7f));
        String nv = "Nv" + p.getNivel();
        g.drawString(nv, r.x + r.width - pad - g.getFontMetrics().stringWidth(nv), r.y + (int) (u * 1.4));
        Estilo.chipTipo(g, p.getTipo(), r.x + pad, r.y + (int) (u * 1.8), (int) (u * 0.85));

        String[] rot = {"HP", "ATAQUE", "DEFESA"};
        int[] val = {p.getHpMaximo(), p.getAtaque(), p.getDefesa()};
        int[] max = {maxHp, maxAtk, maxDef};
        Color[] cor = {new Color(88, 200, 100), new Color(240, 128, 48), new Color(104, 144, 240)};
        int labW = (int) (u * 3.4);
        int barX = r.x + pad + labW;
        int barW = r.width - pad * 2 - labW - (int) (u * 1.6);
        for (int i = 0; i < 3; i++) {
            int y = r.y + (int) (u * (3.5 + i * 0.95));
            g.setFont(Estilo.fonte(u * 0.52f));
            g.setColor(Estilo.BORDA);
            g.drawString(rot[i], r.x + pad, y + (int) (u * 0.5));
            g.setColor(new Color(40, 44, 66));
            int bh = (int) (u * 0.5);
            g.fillRoundRect(barX, y + (int) (u * 0.1), barW, bh, bh, bh);
            g.setColor(cor[i]);
            g.fillRoundRect(barX + 2, y + (int) (u * 0.1) + 2, Math.max(4, (int) ((barW - 4) * (val[i] / (double) max[i]))), bh - 4, bh, bh);
            g.setColor(Estilo.BORDA);
            g.drawString(String.valueOf(val[i]), barX + barW + (int) (u * 0.2), y + (int) (u * 0.5));
        }

        g.setFont(Estilo.fonte(u * 0.6f));
        g.setColor(Estilo.BORDA);
        g.drawString("GOLPES", r.x + pad, r.y + (int) (u * 7.1));
        int yy = (int) (u * 7.35);
        for (Movimento m : p.getMovimentos()) {
            int chipH = (int) (u * 0.72);
            int cw = Estilo.chipTipo(g, m.getTipo(), r.x + pad, r.y + yy, chipH);
            g.setFont(Estilo.fonte(u * 0.62f));
            g.setColor(Estilo.BORDA);
            int ty = r.y + yy + chipH - (int) (u * 0.12);
            g.drawString(m.getNome(), r.x + pad + cw + (int) (u * 0.3), ty);
            String info = "Poder " + m.getPoder();
            FontMetrics fi = g.getFontMetrics();
            g.drawString(info, r.x + r.width - pad - fi.stringWidth(info), ty);
            yy += chipH;

            String nota = null;
            if (m.isAplicaEfeito()) {
                nota = "Especial | " + m.getNomeEfeito() + " (" + Math.round(m.getChanceEfeito() * 100)
                        + "%) | recarga até " + m.getCooldownMaximo();
            } else if (m.isEhEspecial()) {
                nota = "Especial | recarga até " + m.getCooldownMaximo();
            }
            if (nota != null) {
                g.setFont(Estilo.fonte(u * 0.46f));
                g.setColor(new Color(130, 60, 140));
                g.drawString(nota, r.x + pad + (int) (u * 0.2), r.y + yy + (int) (u * 0.42));
                yy += (int) (u * 0.62);
            } else {
                yy += (int) (u * 0.30);
            }
        }
    }

    /** Desenha a imagem dentro do retângulo, mantendo a proporção e centralizada. */
    private void desenharIcone(Graphics2D g, Image img, int bx, int by, int bw, int bh) {
        int iw = Math.max(1, img.getWidth(null)), ih = Math.max(1, img.getHeight(null));
        double esc = Math.min(bw / (double) iw, bh / (double) ih);
        int dw = (int) (iw * esc), dh = (int) (ih * esc);
        Estilo.pixelado(g);
        g.drawImage(img, bx + (bw - dw) / 2, by + (bh - dh) / 2, dw, dh, null);
    }

    private void desenharSlot(Graphics2D g, int i) {
        Rectangle r = slot(i);
        int arc = Math.max(14, r.height / 4);
        g.setColor(new Color(0, 0, 0, 70));
        g.fillRoundRect(r.x + 3, r.y + 5, r.width, r.height, arc, arc);
        boolean cheio = i < escolhidos.size();
        g.setColor(cheio ? new Color(255, 248, 200) : new Color(230, 235, 250, 190));
        g.fillRoundRect(r.x, r.y, r.width, r.height, arc, arc);
        g.setColor(cheio ? new Color(255, 196, 0) : Estilo.BORDA);
        g.setStroke(new BasicStroke(cheio ? 5f : 3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f,
                cheio ? null : new float[]{10f, 8f}, 0f));
        g.drawRoundRect(r.x, r.y, r.width, r.height, arc, arc);
        if (cheio) {
            Pokemon p = modelos[escolhidos.get(i)];
            desenharIcone(g, Sprites.icone(p.getNome()), r.x + 4, r.y + 3, r.width - 8, (int) (r.height * 0.66));
            float tamNome = r.height * 0.16f;
            g.setFont(Estilo.fonte(tamNome));
            while (g.getFontMetrics().stringWidth(p.getNome()) > r.width - 8 && tamNome > 8) {
                tamNome -= 1;
                g.setFont(Estilo.fonte(tamNome));
            }
            FontMetrics fm = g.getFontMetrics();
            g.setColor(Estilo.BORDA);
            g.drawString(p.getNome(), r.x + (r.width - fm.stringWidth(p.getNome())) / 2, r.y + r.height - (int) (r.height * 0.07));
        } else {
            g.setFont(Estilo.fonte(r.height * 0.42f));
            FontMetrics fm = g.getFontMetrics();
            String s = String.valueOf(i + 1);
            g.setColor(new Color(52, 58, 92, 120));
            g.drawString(s, r.x + (r.width - fm.stringWidth(s)) / 2, r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2);
        }
    }
}
