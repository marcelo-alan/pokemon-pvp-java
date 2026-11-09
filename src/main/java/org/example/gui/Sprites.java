package org.example.gui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Carrega as imagens dos Pokémon da pasta resources/sprites.
 * Frente e costas são animações (uma "tira" de quadros em PNG, descrita em animacoes.txt);
 * o ícone é uma imagem estática já recortada.
 */
public final class Sprites {

    /** Animação feita de vários quadros lado a lado. */
    public static final class Animacao {
        private final BufferedImage[] quadros;
        private final int[] atrasos;
        private final int duracaoTotal;
        private final long inicio = System.currentTimeMillis();

        Animacao(BufferedImage[] quadros, int[] atrasos) {
            this.quadros = quadros;
            this.atrasos = atrasos;
            int soma = 0;
            for (int a : atrasos) soma += a;
            this.duracaoTotal = Math.max(1, soma);
        }

        /** Quadro que deve aparecer neste momento. */
        public BufferedImage quadro() {
            if (quadros.length == 1) return quadros[0];
            long t = (System.currentTimeMillis() - inicio) % duracaoTotal;
            int acumulado = 0;
            for (int i = 0; i < quadros.length; i++) {
                acumulado += atrasos[i];
                if (t < acumulado) return quadros[i];
            }
            return quadros[0];
        }

        public int largura() { return quadros[0].getWidth(); }

        public int altura() { return quadros[0].getHeight(); }
    }

    private static final Map<String, String[]> INDICE = new HashMap<>();
    private static final Map<String, Animacao> ANIMACOES = new HashMap<>();
    private static final Map<String, BufferedImage> ICONES = new HashMap<>();

    static {
        try (InputStream in = Sprites.class.getResourceAsStream("/sprites/animacoes.txt")) {
            if (in != null) {
                BufferedReader leitor = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                String linha;
                while ((linha = leitor.readLine()) != null) {
                    String[] p = linha.trim().split(" ");
                    if (p.length >= 5) INDICE.put(p[0], p);
                }
            }
        } catch (IOException e) {
            System.err.println("Não foi possível ler as animações: " + e.getMessage());
        }
    }

    private Sprites() { }

    public static String chave(String nomePokemon) {
        return nomePokemon.toLowerCase(Locale.ROOT);
    }

    public static Animacao frente(String nomePokemon) { return animacao("frente", chave(nomePokemon)); }

    public static Animacao costas(String nomePokemon) { return animacao("costas", chave(nomePokemon)); }

    public static BufferedImage icone(String nomePokemon) {
        String nome = chave(nomePokemon);
        return ICONES.computeIfAbsent(nome, n -> {
            try (InputStream in = Sprites.class.getResourceAsStream("/sprites/icone/" + n + ".png")) {
                if (in != null) return ImageIO.read(in);
            } catch (IOException ignorado) { }
            return substituto(n);
        });
    }

    private static Animacao animacao(String pasta, String nome) {
        String chave = pasta + "/" + nome;
        return ANIMACOES.computeIfAbsent(chave, k -> {
            String[] d = INDICE.get(k);
            if (d != null) {
                try (InputStream in = Sprites.class.getResourceAsStream("/sprites/" + k + ".png")) {
                    if (in != null) {
                        BufferedImage folha = ImageIO.read(in);
                        int w = Integer.parseInt(d[1]), h = Integer.parseInt(d[2]), n = Integer.parseInt(d[3]);
                        String[] tempos = d[4].split(",");
                        BufferedImage[] quadros = new BufferedImage[n];
                        int[] atrasos = new int[n];
                        for (int i = 0; i < n; i++) {
                            quadros[i] = folha.getSubimage(i * w, 0, w, h);
                            atrasos[i] = Math.max(30, Integer.parseInt(tempos[Math.min(i, tempos.length - 1)]));
                        }
                        return new Animacao(quadros, atrasos);
                    }
                } catch (IOException | RuntimeException e) {
                    System.err.println("Falha ao carregar " + k + ": " + e.getMessage());
                }
            }
            return new Animacao(new BufferedImage[]{substituto(nome)}, new int[]{1000});
        });
    }

    /** Imagem simples usada se o arquivo do sprite não for encontrado. */
    private static BufferedImage substituto(String nome) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        Estilo.suavizar(g);
        g.setColor(new Color(200, 200, 210));
        g.fillOval(4, 4, 56, 56);
        g.setColor(Estilo.BORDA);
        g.setStroke(new BasicStroke(3f));
        g.drawOval(4, 4, 56, 56);
        g.setFont(Estilo.fonte(30));
        String s = nome.isEmpty() ? "?" : nome.substring(0, 1).toUpperCase();
        FontMetrics fm = g.getFontMetrics();
        g.drawString(s, 32 - fm.stringWidth(s) / 2, 32 + fm.getAscent() / 2 - 3);
        g.dispose();
        return img;
    }
}
