package org.example.gui;

import org.example.model.Movimento;
import org.example.model.Pokemon;
import org.example.service.BatalhaService;
import org.example.service.ResultadoAtaque;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.DoubleConsumer;

/**
 * Tela de batalha. As regras são as mesmas da versão de console:
 * turnos alternados, troca de Pokémon (não vale logo após entrar em campo),
 * golpes especiais com recarga, eficácia de tipos, STAB e efeitos de status.
 */
public class TelaBatalha extends JPanel {

    /** Um passo de uma sequência (mensagem, animação, ação...). Chama "proximo" quando termina. */
    private interface Passo { void executar(Runnable proximo); }

    private final JanelaJogo janela;
    private final String[] nomes = new String[2];
    private final List<List<Pokemon>> equipes = new ArrayList<>();
    private final Pokemon[] ativo = new Pokemon[2];
    private final boolean[] trocouRecentemente = new boolean[2];
    private final BatalhaService batalha = new BatalhaService();

    private int vez = 0;                 // 0 = treinador 1, 1 = treinador 2
    private boolean recargaAtualizada = false;
    private boolean finalizado = false;

    private final Cena cena = new Cena();
    private final CaixaTexto caixa = new CaixaTexto();
    private final CardLayout cartas = new CardLayout();
    private final PainelMenu menu = new PainelMenu(cartas);
    private final JPanel cartaVazia = new JPanel();
    private final JPanel cartaAcoes = criarCarta(new GridLayout(1, 2, 12, 0));
    private final JPanel cartaGolpes = criarCarta(new GridLayout(2, 2, 10, 10));
    private final JPanel cartaTroca = criarCarta(new GridLayout(2, 2, 10, 10));

    public TelaBatalha(JanelaJogo janela, String nome1, String nome2, List<Pokemon> equipe1, List<Pokemon> equipe2) {
        this.janela = janela;
        nomes[0] = nome1;
        nomes[1] = nome2;
        equipes.add(equipe1);
        equipes.add(equipe2);
        ativo[0] = equipe1.get(0);
        ativo[1] = equipe2.get(0);
        cena.definirAtivo(0, ativo[0]);
        cena.definirAtivo(1, ativo[1]);
        cena.escala[0] = 0;
        cena.escala[1] = 0;

        cartaVazia.setOpaque(false);
        menu.add(cartaVazia, "vazio");
        menu.add(cartaAcoes, "acoes");
        menu.add(cartaGolpes, "golpes");
        menu.add(cartaTroca, "troca");
        cartas.show(menu, "vazio");

        setLayout(new GridBagLayout());
        setBackground(new Color(20, 30, 60));
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.weightx = 1; c.weighty = 0.72;
        cena.setPreferredSize(new Dimension(100, 72));
        add(cena, c);
        c.gridy = 1; c.gridwidth = 1; c.weighty = 0.28; c.weightx = 0.56;
        c.insets = new Insets(0, 10, 10, 5);
        caixa.setPreferredSize(new Dimension(56, 28));
        add(caixa, c);
        c.gridx = 1; c.weightx = 0.44;
        c.insets = new Insets(0, 5, 10, 10);
        menu.setPreferredSize(new Dimension(44, 28));
        add(menu, c);

        // Enter ou Espaço aceleram os textos
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke("ENTER"), "pular");
        im.put(KeyStroke.getKeyStroke("SPACE"), "pular");
        am.put("pular", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { caixa.pular(); }
        });
    }

    private static JPanel criarCarta(LayoutManager lm) {
        JPanel p = new JPanel(lm);
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        return p;
    }

    // ================================================================== fluxo do jogo

    /** Começa a batalha (chamado depois que a tela aparece). */
    public void iniciar() {
        List<Passo> passos = new ArrayList<>();
        passos.add(msg("Batalha entre " + nomes[0] + " e " + nomes[1] + "!"));
        passos.add(msg(nomes[1] + " enviou " + ativo[1].getNome() + "!"));
        passos.add(anim(500, t -> cena.escala[1] = t));
        passos.add(msg("Vai, " + ativo[0].getNome() + "!"));
        passos.add(anim(500, t -> cena.escala[0] = t));
        sequencia(passos, this::iniciarTurno);
    }

    private void iniciarTurno() {
        if (finalizado) return;
        cena.vez = vez;
        recargaAtualizada = false;
        Pokemon a = ativo[vez];
        int reservas = reservasVivas(vez);
        boolean podeTrocar = reservas > 0 && !trocouRecentemente[vez];

        String texto = nomes[vez] + ": o que " + a.getNome() + " fará?";
        if (trocouRecentemente[vez]) {
            texto += "\n(" + a.getNome() + " acabou de entrar e não pode ser trocado neste turno.)";
        } else if (reservas == 0) {
            texto += "\n(Não há Pokémon de reserva.)";
        }
        caixa.fixar(texto);
        mostrarAcoes(podeTrocar);
    }

    private int reservasVivas(int lado) {
        int n = 0;
        for (Pokemon p : equipes.get(lado)) {
            if (p != ativo[lado] && !p.estaDesmaiado()) n++;
        }
        return n;
    }

    private boolean temVivos(int lado) {
        for (Pokemon p : equipes.get(lado)) if (!p.estaDesmaiado()) return true;
        return false;
    }

    // ------------------------------------------------------------------ menus

    private void mostrarAcoes(boolean podeTrocar) {
        cartaAcoes.removeAll();
        BotaoPoke lutar = new BotaoPoke("LUTAR", new Color(222, 80, 60));
        BotaoPoke trocar = new BotaoPoke("POKÉMON", new Color(70, 170, 90));
        trocar.setEnabled(podeTrocar);
        lutar.addActionListener(e -> aoLutar());
        trocar.addActionListener(e -> mostrarTroca(false));
        cartaAcoes.add(lutar);
        cartaAcoes.add(trocar);
        cartas.show(menu, "acoes");
        menu.revalidate();
        menu.repaint();
    }

    void aoLutar() {
        Pokemon a = ativo[vez];
        // os golpes especiais "esfriam" no início da ação de atacar (como no console)
        if (!recargaAtualizada) {
            for (Movimento m : a.getMovimentos()) m.decrementarCooldown();
            recargaAtualizada = true;
        }
        mostrarGolpes();
    }

    private void mostrarGolpes() {
        Pokemon a = ativo[vez];
        caixa.fixar("Escolha o golpe de " + a.getNome() + ".");
        cartaGolpes.removeAll();
        for (Movimento m : a.getMovimentos()) {
            BotaoPoke b = new BotaoPoke(m.getNome(), Estilo.corTipo(m.getTipo()).darker());
            String info = Estilo.nomeTipo(m.getTipo()) + " | Poder " + m.getPoder();
            if (m.estaEmRecarga()) {
                info = "Recarga: " + m.getCooldownAtual() + (m.getCooldownAtual() == 1 ? " turno" : " turnos");
                b.setEnabled(false);
            } else if (m.isEhEspecial()) {
                info += " | Especial";
            }
            b.setLinha2(info);
            b.addActionListener(e -> aoEscolherGolpe(m));
            cartaGolpes.add(b);
        }
        BotaoPoke voltar = new BotaoPoke("VOLTAR", new Color(110, 120, 150));
        voltar.addActionListener(e -> iniciarTurnoSemResetar());
        cartaGolpes.add(voltar);
        cartas.show(menu, "golpes");
        menu.revalidate();
        menu.repaint();
    }

    private void iniciarTurnoSemResetar() {
        boolean antes = recargaAtualizada;
        iniciarTurno();
        recargaAtualizada = antes;
    }

    void mostrarTroca(boolean obrigatoria) {
        int lado = obrigatoria ? ladoDoSubstituto : vez;
        List<Pokemon> equipe = equipes.get(lado);
        caixa.fixar(obrigatoria
                ? nomes[lado] + ", escolha o próximo Pokémon!"
                : nomes[lado] + ": quem entra em campo?");
        cartaTroca.removeAll();
        for (Pokemon p : equipe) {
            BotaoPoke b = new BotaoPoke(p.getNome(), Estilo.corTipo(p.getTipo()).darker());
            b.setIcone(Sprites.icone(p.getNome()));
            boolean emCampo = p == ativo[lado] && !p.estaDesmaiado();
            if (p.estaDesmaiado()) {
                b.setLinha2("Desmaiado");
                b.setEnabled(false);
            } else if (emCampo) {
                b.setLinha2("Em campo");
                b.setEnabled(false);
            } else {
                b.setLinha2("HP " + p.getHpAtual() + "/" + p.getHpMaximo());
            }
            b.addActionListener(e -> {
                if (obrigatoria) aoEscolherSubstituto(lado, p); else aoTrocar(p);
            });
            cartaTroca.add(b);
        }
        if (!obrigatoria) {
            BotaoPoke voltar = new BotaoPoke("VOLTAR", new Color(110, 120, 150));
            voltar.addActionListener(e -> iniciarTurno());
            cartaTroca.add(voltar);
        }
        cartas.show(menu, "troca");
        menu.revalidate();
        menu.repaint();
    }

    // ------------------------------------------------------------------ ataque

    void aoEscolherGolpe(Movimento m) {
        cartas.show(menu, "vazio");
        final int atq = vez, def = 1 - vez;
        Pokemon atacante = ativo[atq], defensor = ativo[def];

        ResultadoAtaque r = batalha.calcularAtaque(atacante, defensor, m);
        m.aplicarCooldown();
        trocouRecentemente[atq] = false;

        List<Passo> passos = new ArrayList<>();
        passos.add(msg(atacante.getNome() + " usou " + m.getNome() + "!"));
        passos.add(anim(450, t -> cena.avanco[atq] = Math.sin(Math.PI * t)));
        passos.add(acao(() -> cena.avanco[atq] = 0));
        passos.add(anim(520, t -> cena.piscando[def] = ((int) (t * 10)) % 2 == 0 && t < 1));
        passos.add(acao(() -> cena.piscando[def] = false));
        passos.add(proximo -> {
            double de = cena.hpMostrado[def], para = defensor.getHpAtual();
            animar(750, t -> cena.hpMostrado[def] = de + (para - de) * suave(t), proximo);
        });

        double mult = r.getMultiplicadorTipo();
        if (mult > 1.0) passos.add(msg("Foi super eficaz!"));
        else if (mult == 0.0) passos.add(msg("Não teve efeito em " + defensor.getNome() + "..."));
        else if (mult < 1.0) passos.add(msg("Não foi muito eficaz..."));
        if (r.isStab()) passos.add(msg("Bônus de mesmo tipo! (STAB)"));
        if (r.getEfeitoAplicado() != null) {
            passos.add(msg(defensor.getNome() + " ficou " + r.getEfeitoAplicado().toLowerCase(Locale.ROOT) + "!"));
        }

        if (defensor.estaDesmaiado()) {
            passos.add(msg(defensor.getNome() + " desmaiou!"));
            passos.add(anim(700, t -> cena.desmaio[def] = t));
            if (temVivos(def)) {
                sequencia(passos, () -> {
                    ladoDoSubstituto = def;
                    mostrarTroca(true);
                });
            } else {
                passos.add(msg(nomes[atq] + " venceu a batalha e se tornou um Mestre Pokémon!"));
                sequencia(passos, () -> { finalizado = true; janela.mostrarFim(nomes[atq]); });
            }
        } else {
            sequencia(passos, this::passarVez);
        }
    }

    private int ladoDoSubstituto = 0;

    private void passarVez() {
        vez = 1 - vez;
        iniciarTurno();
    }

    // ------------------------------------------------------------------ trocas

    /** Troca escolhida pelo treinador no próprio turno (gasta o turno). */
    void aoTrocar(Pokemon novo) {
        cartas.show(menu, "vazio");
        final int lado = vez;
        Pokemon atual = ativo[lado];
        trocouRecentemente[lado] = true;

        List<Passo> passos = new ArrayList<>();
        passos.add(msg(nomes[lado] + ": " + atual.getNome() + ", volte!"));
        passos.add(anim(380, t -> cena.escala[lado] = 1 - t));
        passos.add(acao(() -> {
            ativo[lado] = novo;
            cena.definirAtivo(lado, novo);
            cena.escala[lado] = 0;
        }));
        passos.add(msg("Vai, " + novo.getNome() + "!"));
        passos.add(anim(500, t -> cena.escala[lado] = t));
        sequencia(passos, this::passarVez);
    }

    /** Troca obrigatória depois que um Pokémon desmaia. */
    void aoEscolherSubstituto(int lado, Pokemon novo) {
        cartas.show(menu, "vazio");
        trocouRecentemente[lado] = true;
        List<Passo> passos = new ArrayList<>();
        passos.add(acao(() -> {
            ativo[lado] = novo;
            cena.definirAtivo(lado, novo);
            cena.escala[lado] = 0;
        }));
        passos.add(msg(nomes[lado] + " enviou " + novo.getNome() + "!"));
        passos.add(anim(500, t -> cena.escala[lado] = t));
        sequencia(passos, this::passarVez);
    }

    // ================================================================== motor de sequências

    private Passo msg(String texto) {
        return proximo -> caixa.digitar(texto, proximo);
    }

    private Passo anim(int ms, DoubleConsumer quadro) {
        return proximo -> animar(ms, quadro, proximo);
    }

    private Passo acao(Runnable r) {
        return proximo -> { r.run(); cena.repaint(); proximo.run(); };
    }

    private void sequencia(List<Passo> passos, Runnable aoFinal) {
        executarPasso(passos, 0, aoFinal);
    }

    private void executarPasso(List<Passo> passos, int i, Runnable aoFinal) {
        if (i >= passos.size()) {
            aoFinal.run();
            return;
        }
        passos.get(i).executar(() -> executarPasso(passos, i + 1, aoFinal));
    }

    private void animar(int ms, DoubleConsumer quadro, Runnable fim) {
        final long inicio = System.currentTimeMillis();
        Timer timer = new Timer(15, null);
        timer.addActionListener(e -> {
            double p = Math.min(1.0, (System.currentTimeMillis() - inicio) / (double) ms);
            quadro.accept(p);
            cena.repaint();
            if (p >= 1.0) {
                timer.stop();
                fim.run();
            }
        });
        timer.setCoalesce(true);
        timer.start();
    }

    private static double suave(double t) { return t * t * (3 - 2 * t); }

    // ================================================================== painéis visuais

    /** Moldura escura do menu de comandos. */
    private static class PainelMenu extends JPanel {
        PainelMenu(LayoutManager lm) { super(lm); setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Estilo.suavizar(g);
            int w = getWidth(), h = getHeight();
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRoundRect(3, 6, w - 6, h - 8, 30, 30);
            g.setColor(new Color(236, 240, 252));
            g.fillRoundRect(0, 0, w - 4, h - 6, 30, 30);
            g.setColor(Estilo.BORDA);
            g.setStroke(new BasicStroke(5f));
            g.drawRoundRect(0, 0, w - 4, h - 6, 30, 30);
            g.dispose();
        }
    }

    /** Caixa de texto com efeito de digitação. */
    private class CaixaTexto extends JPanel {
        private String texto = "";
        private int letras = 0;
        private Runnable fim;
        private Timer digitando, espera;
        private boolean esperando = false;

        CaixaTexto() {
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) { pular(); }
            });
        }

        void fixar(String t) {
            cancelar();
            texto = t;
            letras = t.length();
            fim = null;
            repaint();
        }

        void digitar(String t, Runnable aoTerminar) {
            cancelar();
            texto = t;
            letras = 0;
            fim = aoTerminar;
            digitando = new Timer(20, e -> {
                letras++;
                repaint();
                if (letras >= texto.length()) {
                    digitando.stop();
                    aguardar();
                }
            });
            digitando.start();
        }

        private void aguardar() {
            esperando = true;
            espera = new Timer(Math.min(1800, 600 + texto.length() * 16), e -> concluir());
            espera.setRepeats(false);
            espera.start();
        }

        private void concluir() {
            if (!esperando) return;
            esperando = false;
            if (espera != null) espera.stop();
            Runnable f = fim;
            fim = null;
            if (f != null) f.run();
        }

        void pular() {
            if (digitando != null && digitando.isRunning()) {
                digitando.stop();
                letras = texto.length();
                repaint();
                aguardar();
            } else if (esperando) {
                concluir();
            }
        }

        private void cancelar() {
            if (digitando != null) digitando.stop();
            if (espera != null) espera.stop();
            esperando = false;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Estilo.suavizar(g);
            int w = getWidth(), h = getHeight();
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRoundRect(3, 6, w - 6, h - 8, 30, 30);
            g.setColor(Estilo.AZUL_ESCURO);
            g.fillRoundRect(0, 0, w - 4, h - 6, 30, 30);
            g.setColor(new Color(235, 238, 250));
            g.setStroke(new BasicStroke(5f));
            g.drawRoundRect(4, 4, w - 12, h - 14, 24, 24);
            g.setColor(Estilo.BORDA.darker());
            g.setStroke(new BasicStroke(3f));
            g.drawRoundRect(0, 0, w - 4, h - 6, 30, 30);

            g.setFont(Estilo.fonte(Math.max(15, Math.min(34, h * 0.15f))));
            FontMetrics fm = g.getFontMetrics();
            int pad = (int) (w * 0.045);
            List<String> linhas = Estilo.quebrarLinhas(texto, fm, w - pad * 2 - 10);
            int restante = letras;
            int y = (int) (h * 0.18) + fm.getAscent();
            for (String l : linhas) {
                if (restante <= 0) break;
                String parte = l.length() <= restante ? l : l.substring(0, restante);
                restante -= l.length() + 1;
                g.setColor(new Color(0, 0, 0, 120));
                g.drawString(parte, pad + 1, y + 2);
                g.setColor(Color.WHITE);
                g.drawString(parte, pad, y);
                y += (int) (fm.getHeight() * 1.12);
            }
            if (esperando) {
                int s = (int) (h * 0.07);
                g.setColor(new Color(255, 214, 0));
                g.fillPolygon(new int[]{w - pad - s, w - pad, w - pad - s / 2},
                        new int[]{h - (int) (h * 0.28), h - (int) (h * 0.28), h - (int) (h * 0.18)}, 3);
            }
            g.dispose();
        }
    }

    // ================================================================== cena (o campo de batalha)

    private class Cena extends JPanel {
        final Pokemon[] pk = new Pokemon[2];
        final double[] hpMostrado = new double[2];
        final double[] escala = {1, 1};
        final double[] desmaio = new double[2];
        final double[] avanco = new double[2];
        final boolean[] piscando = new boolean[2];
        int vez = 0;

        private Timer relogio;

        Cena() { setOpaque(true); }

        @Override
        public void addNotify() {
            super.addNotify();
            relogio = new Timer(60, e -> repaint());   // mantém os sprites animados
            relogio.start();
        }

        @Override
        public void removeNotify() {
            if (relogio != null) relogio.stop();
            super.removeNotify();
        }

        void definirAtivo(int lado, Pokemon p) {
            pk[lado] = p;
            hpMostrado[lado] = p.getHpAtual();
            escala[lado] = 1;
            desmaio[lado] = 0;
            avanco[lado] = 0;
            piscando[lado] = false;
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            Estilo.suavizar(g);
            int W = getWidth(), H = getHeight();
            desenharCenario(g, W, H);

            Rectangle plIni = new Rectangle((int) (W * 0.55), (int) (H * 0.33), (int) (W * 0.34), (int) (H * 0.12));
            Rectangle plJog = new Rectangle((int) (W * 0.05), (int) (H * 0.76), (int) (W * 0.44), (int) (H * 0.15));
            desenharPlataforma(g, plIni);
            desenharPlataforma(g, plJog);

            desenharPokemon(g, 1, plIni, W, H);
            desenharPokemon(g, 0, plJog, W, H);

            caixaInfo(g, 1, (int) (W * 0.04), (int) (H * 0.06), (int) (W * 0.43), (int) (H * 0.28));
            caixaInfo(g, 0, (int) (W * 0.53), (int) (H * 0.58), (int) (W * 0.43), (int) (H * 0.31));
            g.dispose();
        }

        private void desenharCenario(Graphics2D g, int W, int H) {
            g.setPaint(new GradientPaint(0, 0, new Color(112, 190, 245), 0, (int) (H * 0.55), new Color(226, 244, 255)));
            g.fillRect(0, 0, W, H);
            // nuvens
            g.setColor(new Color(255, 255, 255, 190));
            int[][] nuvens = {{12, 8, 14, 5}, {46, 4, 18, 6}, {78, 14, 12, 4}};
            for (int[] n : nuvens) {
                int x = (int) (W * n[0] / 100.0), y = (int) (H * n[1] / 100.0);
                int w = (int) (W * n[2] / 100.0), h = (int) (H * n[3] / 100.0);
                g.fillOval(x, y, w, h);
                g.fillOval(x + w / 4, y - h / 3, w / 2, h);
                g.fillOval(x + w / 2, y, (int) (w * 0.6), h);
            }
            // colinas ao fundo
            g.setColor(new Color(120, 190, 120));
            g.fillOval((int) (W * -0.1), (int) (H * 0.30), (int) (W * 0.6), (int) (H * 0.4));
            g.setColor(new Color(100, 175, 105));
            g.fillOval((int) (W * 0.45), (int) (H * 0.26), (int) (W * 0.7), (int) (H * 0.45));
            // chão
            int topoChao = (int) (H * 0.50);
            g.setPaint(new GradientPaint(0, topoChao, new Color(122, 200, 90), 0, H, new Color(70, 150, 62)));
            g.fillRect(0, topoChao, W, H - topoChao);
            g.setColor(new Color(255, 255, 255, 38));
            g.setStroke(new BasicStroke(3f));
            for (int i = 1; i <= 5; i++) {
                int y = topoChao + (int) ((H - topoChao) * Math.pow(i / 5.0, 1.6));
                g.drawLine(0, y, W, y);
            }
        }

        private void desenharPlataforma(Graphics2D g, Rectangle r) {
            g.setColor(new Color(0, 0, 0, 55));
            g.fillOval(r.x + 6, r.y + r.height / 4 + 8, r.width, r.height);
            g.setColor(new Color(86, 150, 60));
            g.fillOval(r.x, r.y + r.height / 6, r.width, r.height);
            g.setPaint(new GradientPaint(0, r.y, new Color(170, 226, 120), 0, r.y + r.height, new Color(120, 190, 84)));
            g.fillOval(r.x, r.y, r.width, r.height);
            g.setColor(new Color(60, 120, 50));
            g.setStroke(new BasicStroke(3f));
            g.draw(new Ellipse2D.Double(r.x, r.y, r.width, r.height));
        }

        private void desenharPokemon(Graphics2D g, int lado, Rectangle plat, int W, int H) {
            Pokemon p = pk[lado];
            if (p == null) return;
            double vis = 1.0 - desmaio[lado];
            if (vis <= 0.01 || escala[lado] <= 0.01 || piscando[lado]) return;

            Sprites.Animacao anim = lado == 1 ? Sprites.frente(p.getNome()) : Sprites.costas(p.getNome());
            int iw = anim.largura(), ih = anim.altura();
            double base = H / 130.0 * (lado == 0 ? 1.5 : 1.0);
            double maxH = H * (lado == 0 ? 0.50 : 0.33);
            double maxW = W * (lado == 0 ? 0.30 : 0.24);
            double s = Math.min(base, Math.min(maxH / ih, maxW / iw));
            int w = Math.max(1, (int) (iw * s * escala[lado]));
            int h = Math.max(1, (int) (ih * s * escala[lado]));

            double dir = lado == 0 ? 1 : -1;
            double cx = plat.getCenterX() + dir * avanco[lado] * W * 0.09;
            double pe = plat.getCenterY() + plat.height * 0.08 - dir * avanco[lado] * H * 0.06 + desmaio[lado] * H * 0.10;
            int x = (int) (cx - w / 2.0), y = (int) (pe - h);

            Composite antigo = g.getComposite();
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) vis));
            Estilo.pixelado(g);
            g.drawImage(anim.quadro(), x, y, w, h, null);
            g.setComposite(antigo);
        }

        private void caixaInfo(Graphics2D g, int lado, int x, int y, int w, int h) {
            Pokemon p = pk[lado];
            if (p == null) return;
            boolean turno = lado == vez && !finalizado;
            int arc = 30;
            g.setColor(new Color(0, 0, 0, 70));
            g.fillRoundRect(x + 4, y + 7, w, h, arc, arc);
            g.setColor(Estilo.CREME);
            g.fillRoundRect(x, y, w, h, arc, arc);
            g.setColor(turno ? new Color(255, 190, 0) : Estilo.BORDA);
            g.setStroke(new BasicStroke(turno ? 7f : 4f));
            g.drawRoundRect(x, y, w, h, arc, arc);

            int pad = (int) (h * 0.09);
            Color corTreinador = lado == 0 ? new Color(205, 52, 52) : new Color(48, 92, 205);

            // linha 1: treinador + pokébolas
            g.setFont(Estilo.fonte(h * 0.13f));
            FontMetrics fm = g.getFontMetrics();
            g.setColor(corTreinador);
            String tr = nomes[lado];
            while (fm.stringWidth(tr) > w * 0.55 && tr.length() > 3) tr = tr.substring(0, tr.length() - 2);
            if (!tr.equals(nomes[lado])) tr += "…";
            g.drawString(tr, x + pad, y + pad + fm.getAscent() - 2);
            int r = (int) (h * 0.055);
            List<Pokemon> eq = equipes.get(lado);
            for (int i = 0; i < eq.size(); i++) {
                int cx = x + w - pad - r - (eq.size() - 1 - i) * (r * 2 + 6);
                Estilo.pokebola(g, cx, y + pad + r, r, !eq.get(i).estaDesmaiado());
            }

            // linha 2: nome e nível
            g.setFont(Estilo.fonte(h * 0.20f));
            fm = g.getFontMetrics();
            g.setColor(Estilo.BORDA);
            int yNome = y + (int) (h * 0.43);
            g.drawString(p.getNome(), x + pad, yNome);
            g.setFont(Estilo.fonte(h * 0.15f));
            String nv = "Nv" + p.getNivel();
            g.drawString(nv, x + w - pad - g.getFontMetrics().stringWidth(nv), yNome);

            // linha 3: tipo + condição
            int chipH = (int) (h * 0.13);
            int cw = Estilo.chipTipo(g, p.getTipo(), x + pad, y + (int) (h * 0.49), chipH);
            if (p.getCondicaoNegativa() != null) {
                desenharStatus(g, p.getCondicaoNegativa(), x + pad + cw + 8, y + (int) (h * 0.49), chipH);
            }

            // linha 4: barra de HP
            int barY = y + (int) (h * 0.68), barH = (int) (h * 0.11);
            g.setFont(Estilo.fonte(barH * 0.9f));
            g.setColor(new Color(220, 130, 40));
            g.drawString("HP", x + pad, barY + barH - 1);
            int bx = x + pad + (int) (h * 0.19);
            Estilo.barraHp(g, bx, barY, x + w - pad - bx, barH, hpMostrado[lado] / p.getHpMaximo());

            // linha 5: números
            g.setFont(Estilo.fonte(h * 0.13f));
            String nums = (int) Math.ceil(hpMostrado[lado]) + " / " + p.getHpMaximo();
            g.setColor(Estilo.BORDA);
            g.drawString(nums, x + w - pad - g.getFontMetrics().stringWidth(nums), y + h - pad + 2);
        }

        private void desenharStatus(Graphics2D g, String condicao, int x, int y, int h) {
            String c = condicao.toLowerCase(Locale.ROOT);
            String sigla;
            Color cor;
            if (c.startsWith("paralis")) { sigla = "PAR"; cor = new Color(220, 170, 20); }
            else if (c.startsWith("envenen")) { sigla = "ENV"; cor = new Color(160, 64, 160); }
            else if (c.startsWith("queim")) { sigla = "QUE"; cor = new Color(240, 110, 40); }
            else if (c.startsWith("dorm")) { sigla = "DOR"; cor = new Color(120, 130, 150); }
            else if (c.startsWith("congel")) { sigla = "CON"; cor = new Color(90, 190, 210); }
            else if (c.startsWith("assust")) { sigla = "ASS"; cor = new Color(100, 80, 150); }
            else { sigla = condicao.substring(0, Math.min(3, condicao.length())).toUpperCase(Locale.ROOT); cor = new Color(120, 120, 130); }
            g.setFont(Estilo.fonte(h * 0.62f));
            FontMetrics fm = g.getFontMetrics();
            int w = fm.stringWidth(sigla) + h;
            g.setColor(cor);
            g.fillRoundRect(x, y, w, h, h, h);
            g.setColor(cor.darker().darker());
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(x, y, w, h, h, h);
            Estilo.textoSombra(g, sigla, x + (w - fm.stringWidth(sigla)) / 2, y + (h + fm.getAscent() - fm.getDescent()) / 2, Color.WHITE);
        }
    }
}
