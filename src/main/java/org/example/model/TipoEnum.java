package org.example.model;

public enum TipoEnum {
    FOGO, AGUA, PLANTA, ELETRICO, NORMAL,
    ACO, DRAGAO, FADA, FANTASMA, GELO,
    INSETO, LUTADOR, PEDRA, PSIQUICO, SOMBRIO,
    TERRA, VENENOSO, VOADOR;

    // Tabela de multiplicadores baseada na eficácia oficial de tipos
    public double getMultiplicadorContra(TipoEnum defensor) {

        // --- FOGO ---
        if (this == FOGO) {
            if (defensor == ACO || defensor == GELO || defensor == INSETO || defensor == PLANTA) return 2.0;
            if (defensor == AGUA || defensor == DRAGAO || defensor == FOGO || defensor == PEDRA) return 0.5;
        }

        // --- AGUA ---
        if (this == AGUA) {
            if (defensor == FOGO || defensor == PEDRA || defensor == TERRA) return 2.0;
            if (defensor == AGUA || defensor == DRAGAO || defensor == PLANTA) return 0.5;
        }

        // --- PLANTA ---
        if (this == PLANTA) {
            if (defensor == AGUA || defensor == PEDRA || defensor == TERRA) return 2.0;
            if (defensor == ACO || defensor == DRAGAO || defensor == FOGO || defensor == INSETO || defensor == PLANTA || defensor == VENENOSO || defensor == VOADOR) return 0.5;
        }

        // --- ELETRICO ---
        if (this == ELETRICO) {
            if (defensor == AGUA || defensor == VOADOR) return 2.0;
            if (defensor == DRAGAO || defensor == ELETRICO || defensor == PLANTA) return 0.5;
            if (defensor == TERRA) return 0.0; // Imune
        }

        // --- NORMAL ---
        if (this == NORMAL) {
            if (defensor == PEDRA || defensor == ACO) return 0.5;
            if (defensor == FANTASMA) return 0.0; // Imune
        }

        // --- LUTADOR ---
        if (this == LUTADOR) {
            if (defensor == ACO || defensor == GELO || defensor == NORMAL || defensor == PEDRA || defensor == SOMBRIO) return 2.0;
            if (defensor == FADA || defensor == INSETO || defensor == PSIQUICO || defensor == VENENOSO || defensor == VOADOR) return 0.5;
            if (defensor == FANTASMA) return 0.0; // Imune
        }

        // --- PEDRA ---
        if (this == PEDRA) {
            if (defensor == FOGO || defensor == GELO || defensor == INSETO || defensor == VOADOR) return 2.0;
            if (defensor == ACO || defensor == LUTADOR || defensor == TERRA) return 0.5;
        }

        // --- FANTASMA ---
        if (this == FANTASMA) {
            if (defensor == FANTASMA || defensor == PSIQUICO) return 2.0;
            if (defensor == SOMBRIO) return 0.5;
            if (defensor == NORMAL) return 0.0; // Imune
        }

        // --- FADA ---
        if (this == FADA) {
            if (defensor == DRAGAO || defensor == LUTADOR || defensor == SOMBRIO) return 2.0;
            if (defensor == ACO || defensor == FOGO || defensor == VENENOSO) return 0.5;
        }

        // --- DRAGAO ---
        if (this == DRAGAO) {
            if (defensor == DRAGAO) return 2.0;
            if (defensor == ACO) return 0.5;
            if (defensor == FADA) return 0.0; // Imune
        }

        // --- GELO ---
        if (this == GELO) {
            if (defensor == DRAGAO || defensor == PLANTA || defensor == TERRA || defensor == VOADOR) return 2.0;
            if (defensor == ACO || defensor == AGUA || defensor == FOGO || defensor == GELO) return 0.5;
        }

        // --- INSETO ---
        if (this == INSETO) {
            if (defensor == PLANTA || defensor == PSIQUICO || defensor == SOMBRIO) return 2.0;
            if (defensor == ACO || defensor == FADA || defensor == FANTASMA || defensor == FOGO || defensor == LUTADOR || defensor == VENENOSO || defensor == VOADOR) return 0.5;
        }

        // --- PSIQUICO ---
        if (this == PSIQUICO) {
            if (defensor == LUTADOR || defensor == VENENOSO) return 2.0;
            if (defensor == ACO || defensor == PSIQUICO) return 0.5;
            if (defensor == SOMBRIO) return 0.0; // Imune
        }

        // --- SOMBRIO ---
        if (this == SOMBRIO) {
            if (defensor == FANTASMA || defensor == PSIQUICO) return 2.0;
            if (defensor == FADA || defensor == LUTADOR || defensor == SOMBRIO) return 0.5;
        }

        // --- TERRA ---
        if (this == TERRA) {
            if (defensor == ACO || defensor == ELETRICO || defensor == FOGO || defensor == PEDRA || defensor == VENENOSO) return 2.0;
            if (defensor == INSETO || defensor == PLANTA) return 0.5;
            if (defensor == VOADOR) return 0.0; // Imune
        }

        // --- VENENOSO ---
        if (this == VENENOSO) {
            if (defensor == FADA || defensor == PLANTA) return 2.0;
            if (defensor == FANTASMA || defensor == PEDRA || defensor == TERRA || defensor == VENENOSO) return 0.5;
            if (defensor == ACO) return 0.0; // Imune
        }

        // --- VOADOR ---
        if (this == VOADOR) {
            if (defensor == INSETO || defensor == LUTADOR || defensor == PLANTA) return 2.0;
            if (defensor == ACO || defensor == ELETRICO || defensor == PEDRA) return 0.5;
        }

        // Interações neutras por defeito
        return 1.0;
    }
}