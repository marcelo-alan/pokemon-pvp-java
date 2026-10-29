package org.example.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Pokemon {
    private String nome;
    private int nivel;
    private int hpMaximo;
    private int hpAtual;
    private int ataque;
    private int defesa;
    private TipoEnum tipo;
    private List<Movimento> movimentos;
    private boolean capturado;

    public Pokemon(String nome, int nivel, int hpMaximo, int ataque, int defesa, TipoEnum tipo) {
        this.nome = nome;
        this.nivel = nivel;
        this.hpMaximo = hpMaximo;
        this.hpAtual = hpMaximo; // O pokémon começa com HP cheio
        this.ataque = ataque;
        this.defesa = defesa;
        this.tipo = tipo;
        this.movimentos = new ArrayList<>();
        this.capturado = false;
    }

    public void receberDano(int dano) {
        this.hpAtual -= dano;
        if (this.hpAtual < 0) {
            this.hpAtual = 0;
        }
    }

    public void curar(int quantidade) {
        this.hpAtual += quantidade;
        if (this.hpAtual > this.hpMaximo) {
            this.hpAtual = this.hpMaximo;
        }
    }

    public boolean estaDesmaiado() {
        return this.hpAtual == 0;
    }

    public void adicionarMovimento(Movimento movimento) {
        this.movimentos.add(movimento);
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public int getNivel() { return nivel; }
    public int getHpMaximo() { return hpMaximo; }
    public int getHpAtual() { return hpAtual; }
    public int getAtaque() { return ataque; }
    public int getDefesa() { return defesa; }
    public TipoEnum getTipo() { return tipo; }
    public List<Movimento> getMovimentos() { return movimentos; }
    public boolean isCapturado() { return capturado; }

    public void setCapturado(boolean capturado) {
        this.capturado = capturado;
    }

    protected String condicaoNegativa = null; // Ex: "QUEIMADO", "PARALISADO", etc.

    public String getCondicaoNegativa() { return condicaoNegativa; }
    public void setCondicaoNegativa(String condicaoNegativa) { this.condicaoNegativa = condicaoNegativa; }

}

