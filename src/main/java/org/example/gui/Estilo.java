package org.example.gui;

import org.example.model.TipoEnum;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/** Cores, fontes e funções de desenho usadas por todas as telas. */
public final class Estilo {

    public static final Color AZUL_ESCURO = new Color(36, 44, 78);
    public static final Color BORDA = new Color(52, 58, 92);
    public static final Color CREME = new Color(252, 250, 236);
    public static final Color AMARELO = new Color(255, 214, 0);
    public static final Color AZUL_LOGO = new Color(40, 90, 170);
    public static final Color VERMELHO = new Color(214, 58, 58);
    public static final Color VERDE = new Color(76, 175, 80);

    private Estilo() { }

    // ------------------------------------------------------------------ tipos

    public static Color corTipo(TipoEnum t) {
        return switch (t) {
            case FOGO -> new Color(240, 128, 48);
            case AGUA -> new Color(104, 144, 240);
            case PLANTA -> new Color(120, 200, 80);
            case ELETRICO -> new Color(248, 208, 48);
            case NORMAL -> new Color(168, 168, 120);
            case ACO -> new Color(184, 184, 208);
            case DRAGAO -> new Color(112, 56, 248);
            case FADA -> new Color(238, 153, 172);
            case FANTASMA -> new Color(112, 88, 152);
            case GELO -> new Color(152, 216, 216);
            case INSETO -> new Color(168, 184, 32);
            case LUTADOR -> new Color(192, 48, 40);
            case PEDRA -> new Color(184, 160, 56);
            case PSIQUICO -> new Color(248, 88, 136);
            case SOMBRIO -> new Color(112, 88, 72);
            case TERRA -> new Color(224, 192, 104);
            case VENENOSO -> new Color(160, 64, 160);
            case VOADOR -> new Color(168, 144, 240);
        };
    }

    public static String nomeTipo(TipoEnum t) {
        return switch (t) {
            case FOGO -> "Fogo";
            case AGUA -> "Água";
            case PLANTA -> "Planta";
            case ELETRICO -> "Elétrico";
            case NORMAL -> "Normal";
            case ACO -> "Aço";
            case DRAGAO -> "Dragão";
            case FADA -> "Fada";
            case FANTASMA -> "Fantasma";
            case GELO -> "Gelo";
            case INSETO -> "Inseto";
            case LUTADOR -> "Lutador";
            case PEDRA -> "Pedra";
            case PSIQUICO -> "Psíquico";
            case SOMBRIO -> "Sombrio";
            case TERRA -> "Terra";
            case VENENOSO -> "Venenoso";
            case VOADOR -> "Voador";
        };
    }

    // ------------------------------------------------------------------ fontes

    public static Font fonte(float tamanho) {
        return new Font(Font.SANS_SERIF, Font.BOLD, Math.max(8, Math.round(tamanho)));
    }

    // ------------------------------------------------------------------ renderização

    public static void suavizar(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    /** Mantém os "pixels" dos sprites nítidos ao ampliar. */
    public static void pixelado(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }

    // ------------------------------------------------------------------ textos

    public static void textoSombra(Graphics2D g, String texto, int x, int y, Color cor) {
        g.setColor(new Color(0, 0, 0, 150));
        g.drawString(texto, x + 1, y + 2);
        g.setColor(cor);
        g.drawString(texto, x, y);
    }

    public static void textoContorno(Graphics2D g, String texto, Font f, float x, float y,
                                     Color preenchimento, Color contorno, float espessura) {
        Shape s = new TextLayout(texto, f, g.getFontRenderContext())
                .getOutline(AffineTransform.getTranslateInstance(x, y));
        Stroke antigo = g.getStroke();
        g.setStroke(new BasicStroke(espessura, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(contorno);
        g.draw(s);
        g.setColor(preenchimento);
        g.fill(s);
        g.setStroke(antigo);
    }

    /** Texto estilo logo Pokémon (amarelo com contorno azul), centralizado em cx. */
    public static void logoCentralizado(Graphics2D g, String texto, Font f, float cx, float baseline,
                                        float espessura) {
        Rectangle2D b = new TextLayout(texto, f, g.getFontRenderContext()).getBounds();
        float x = (float) (cx - b.getWidth() / 2 - b.getX());
        // sombra vermelha
        textoContorno(g, texto, f, x + espessura * 0.4f, baseline + espessura * 0.5f,
                new Color(200, 30, 40), new Color(200, 30, 40), espessura + 2);
        textoContorno(g, texto, f, x, baseline, AMARELO, AZUL_LOGO, espessura);
    }

    public static List<String> quebrarLinhas(String texto, FontMetrics fm, int largura) {
        List<String> saida = new ArrayList<>();
        for (String paragrafo : texto.split("\n", -1)) {
            String atual = "";
            for (String palavra : paragrafo.split(" ")) {
                String tentativa = atual.isEmpty() ? palavra : atual + " " + palavra;
                if (atual.isEmpty() || fm.stringWidth(tentativa) <= largura) {
                    atual = tentativa;
                } else {
                    saida.add(atual);
                    atual = palavra;
                }
            }
            saida.add(atual);
        }
        return saida;
    }

    // ------------------------------------------------------------------ elementos

    public static int chipTipo(Graphics2D g, TipoEnum t, int x, int y, int h) {
        g.setFont(fonte(h * 0.58f));
        FontMetrics fm = g.getFontMetrics();
        String s = nomeTipo(t).toUpperCase(Locale.of("pt", "BR"));
        int w = fm.stringWidth(s) + h;
        g.setColor(corTipo(t));
        g.fillRoundRect(x, y, w, h, h, h);
        g.setColor(corTipo(t).darker().darker());
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, h, h, h);
        textoSombra(g, s, x + (w - fm.stringWidth(s)) / 2, y + (h + fm.getAscent() - fm.getDescent()) / 2, Color.WHITE);
        return w;
    }

    public static Color corHp(double fracao) {
        if (fracao > 0.5) return new Color(88, 200, 100);
        if (fracao > 0.2) return new Color(248, 200, 40);
        return new Color(232, 72, 60);
    }

    public static void barraHp(Graphics2D g, int x, int y, int w, int h, double fracao) {
        fracao = Math.max(0, Math.min(1, fracao));
        g.setColor(new Color(40, 44, 66));
        g.fillRoundRect(x, y, w, h, h, h);
        int fw = (int) ((w - 4) * fracao);
        if (fw > 0) {
            g.setColor(corHp(fracao));
            g.fillRoundRect(x + 2, y + 2, fw, h - 4, h - 4, h - 4);
            g.setColor(new Color(255, 255, 255, 80));
            g.fillRoundRect(x + 2, y + 2, fw, (h - 4) / 2, h - 4, h - 4);
        }
    }

    public static void pokebola(Graphics2D g, int cx, int cy, int r, boolean viva) {
        Color topo = viva ? new Color(230, 60, 60) : new Color(135, 135, 140);
        Color baixo = viva ? Color.WHITE : new Color(195, 195, 200);
        g.setColor(topo);
        g.fillArc(cx - r, cy - r, 2 * r, 2 * r, 0, 180);
        g.setColor(baixo);
        g.fillArc(cx - r, cy - r, 2 * r, 2 * r, 180, 180);
        g.setColor(BORDA);
        g.setStroke(new BasicStroke(Math.max(2f, r / 5f)));
        g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
        g.drawLine(cx - r, cy, cx + r, cy);
        int rb = Math.max(3, r / 3);
        g.setColor(Color.WHITE);
        g.fillOval(cx - rb, cy - rb, 2 * rb, 2 * rb);
        g.setColor(BORDA);
        g.drawOval(cx - rb, cy - rb, 2 * rb, 2 * rb);
    }

    // ------------------------------------------------------------------ fundo "explosão"

    private static BufferedImage cacheFundo;

    /** Fundo vermelho/laranja com explosão azul, igual à abertura de "Quem é esse Pokémon?". */
    public static BufferedImage fundoExplosao(int w, int h) {
        w = Math.max(1, w);
        h = Math.max(1, h);
        if (cacheFundo != null && cacheFundo.getWidth() == w && cacheFundo.getHeight() == h) {
            return cacheFundo;
        }
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        suavizar(g);
        g.setPaint(new GradientPaint(0, 0, new Color(238, 32, 34), 0, h, new Color(244, 122, 40)));
        g.fillRect(0, 0, w, h);

        Random rnd = new Random(7);
        // linhas de velocidade
        for (int i = 0; i < 140; i++) {
            int x = rnd.nextInt(w + 400) - 400;
            int y = rnd.nextInt(h);
            int comp = 300 + rnd.nextInt(1000);
            int esp = 2 + rnd.nextInt(Math.max(3, h / 40));
            int a = 18 + rnd.nextInt(28);
            g.setColor(rnd.nextBoolean() ? new Color(255, 255, 255, a) : new Color(150, 0, 10, a));
            g.fillPolygon(new int[]{x, x + comp, x + comp, x},
                    new int[]{y, y - comp / 10, y - comp / 10 + esp, y + esp}, 4);
        }
        // explosão azul
        int cx = (int) (w * 0.33), cy = (int) (h * 0.52);
        double alcance = Math.max(w, h) * 0.75;
        int[][] camadas = {{120, 0}, {140, 1}, {160, 2}};
        Color[] cores = {new Color(80, 150, 225), new Color(135, 195, 245), new Color(240, 248, 255)};
        for (int[] camada : camadas) {
            int n = camada[0];
            for (int i = 0; i < n; i++) {
                double ang = 2 * Math.PI * i / n + (rnd.nextDouble() - 0.5) * 0.06;
                double len = alcance * (0.45 + rnd.nextDouble() * 0.55) * (1 - camada[1] * 0.22);
                double hw = (0.05 + rnd.nextDouble() * 0.06) * (1 - camada[1] * 0.1);
                double r0 = Math.min(w, h) * 0.17;
                int[] xs = {(int) (cx + r0 * Math.cos(ang - hw)), (int) (cx + len * Math.cos(ang)), (int) (cx + r0 * Math.cos(ang + hw))};
                int[] ys = {(int) (cy + r0 * Math.sin(ang - hw)), (int) (cy + len * Math.sin(ang)), (int) (cy + r0 * Math.sin(ang + hw))};
                g.setColor(cores[camada[1]]);
                g.fillPolygon(xs, ys, 3);
            }
        }
        float raio = Math.min(w, h) * 0.34f;
        g.setPaint(new RadialGradientPaint(cx, cy, raio, new float[]{0f, 0.55f, 1f},
                new Color[]{new Color(255, 255, 255, 255), new Color(255, 255, 255, 200), new Color(255, 255, 255, 0)}));
        g.fillOval((int) (cx - raio), (int) (cy - raio), (int) (raio * 2), (int) (raio * 2));
        g.dispose();
        cacheFundo = img;
        return img;
    }
}
