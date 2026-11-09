package org.example.model;

public class Movimento {
    private String nome;
    private TipoEnum tipo;
    private int poder;
    private boolean ehEspecial;
    private int cooldownMaximo;
    private int cooldownAtual;

    // Novos campos para efeitos negativos
    private boolean aplicaEfeito;
    private String nomeEfeito; // Ex: "Queimado", "Envenenado", "Paralisado"
    private double chanceEfeito; // Ex: 0.5 para 50%

    // Construtor para ataques normais ou especiais sem efeito
    public Movimento(String nome, TipoEnum tipo, int poder, boolean ehEspecial, int cooldownMaximo) {
        this.nome = nome;
        this.tipo = tipo;
        this.poder = poder;
        this.ehEspecial = ehEspecial;
        this.cooldownMaximo = cooldownMaximo;
        this.cooldownAtual = 0;
        this.aplicaEfeito = false;
    }

    // Construtor avançado para ataques especiais com efeitos negativos
    public Movimento(String nome, TipoEnum tipo, int poder, boolean ehEspecial, int cooldownMaximo, String nomeEfeito, double chanceEfeito) {
        this.nome = nome;
        this.tipo = tipo;
        this.poder = poder;
        this.ehEspecial = ehEspecial;
        this.cooldownMaximo = cooldownMaximo;
        this.cooldownAtual = 0;
        this.aplicaEfeito = true;
        this.nomeEfeito = nomeEfeito;
        this.chanceEfeito = chanceEfeito;
    }

    public boolean estaEmRecarga() {
        return this.cooldownAtual > 0;
    }

    public void aplicarCooldown() {
        if (ehEspecial) {
            this.cooldownAtual = 1 + (int)(Math.random() * cooldownMaximo);
        }
    }

    public void decrementarCooldown() {
        if (this.cooldownAtual > 0) {
            this.cooldownAtual--;
        }
    }

    // Getters
    public String getNome() { return nome; }
    public TipoEnum getTipo() { return tipo; }
    public int getPoder() { return poder; }
    public boolean isEhEspecial() { return ehEspecial; }
    public int getCooldownAtual() { return cooldownAtual; }
    public int getCooldownMaximo() { return cooldownMaximo; }
    public boolean isAplicaEfeito() { return aplicaEfeito; }
    public String getNomeEfeito() { return nomeEfeito; }
    public double getChanceEfeito() { return chanceEfeito; }
}