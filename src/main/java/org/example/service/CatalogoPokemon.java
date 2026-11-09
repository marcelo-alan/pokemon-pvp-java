package org.example.service;

import org.example.model.*;

import java.util.List;
import java.util.function.Supplier;

/**
 * Lista de todos os Pokémon disponíveis no jogo (na mesma ordem do menu do console).
 * A interface gráfica usa esta lista para montar a tela de escolha de equipe.
 */
public final class CatalogoPokemon {

    /** Um item do catálogo: nome, nome do arquivo de sprite e a fábrica do Pokémon. */
    public record Entrada(String nome, Supplier<Pokemon> fabrica) {
        public Pokemon criar() { return fabrica.get(); }
    }

    private static final int NIVEL_INICIAL = 5;

    private static final List<Entrada> ENTRADAS = List.of(
            new Entrada("Charmander", () -> new Charmander(NIVEL_INICIAL)),
            new Entrada("Bulbasaur", () -> new Bulbasaur(NIVEL_INICIAL)),
            new Entrada("Squirtle", () -> new Squirtle(NIVEL_INICIAL)),
            new Entrada("Pikachu", () -> new Pikachu(NIVEL_INICIAL)),
            new Entrada("Eevee", () -> new Eevee(NIVEL_INICIAL)),
            new Entrada("Psyduck", () -> new Psyduck(NIVEL_INICIAL)),
            new Entrada("Machop", () -> new Machop(NIVEL_INICIAL)),
            new Entrada("Geodude", () -> new Geodude(NIVEL_INICIAL)),
            new Entrada("Gastly", () -> new Gastly(NIVEL_INICIAL)),
            new Entrada("Jigglypuff", () -> new Jigglypuff(NIVEL_INICIAL)),
            new Entrada("Snorlax", () -> new Snorlax(NIVEL_INICIAL)),
            new Entrada("Gengar", () -> new Gengar(NIVEL_INICIAL)),
            new Entrada("Arcanine", () -> new Arcanine(NIVEL_INICIAL)),
            new Entrada("Gyarados", () -> new Gyarados(NIVEL_INICIAL)),
            new Entrada("Alakazam", () -> new Alakazam(NIVEL_INICIAL)),
            new Entrada("Lapras", () -> new Lapras(NIVEL_INICIAL)),
            new Entrada("Dragonite", () -> new Dragonite(NIVEL_INICIAL)),
            new Entrada("Electabuzz", () -> new Electabuzz(NIVEL_INICIAL))
    );

    private CatalogoPokemon() { }

    public static List<Entrada> todos() { return ENTRADAS; }

    public static Pokemon criar(int indice) { return ENTRADAS.get(indice).criar(); }
}
