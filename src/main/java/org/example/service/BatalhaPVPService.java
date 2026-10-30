package org.example.service;

import org.example.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BatalhaPVPService {
    private final BatalhaService batalhaService;
    private final Scanner scanner;

    public BatalhaPVPService() {
        this.batalhaService = new BatalhaService();
        this.scanner = new Scanner(System.in);
    }

    public void iniciarBatalha() {
        limparTela();
        System.out.println("=== 🔴🔵 BATALHA POKÉMON - LIGA POKÉMON 🔵🔴 ===");

        System.out.print("Nome do Treinador Pokémon 1: ");
        String treinador1 = scanner.nextLine();

        System.out.print("Nome do Treinador Pokémon 2: ");
        String treinador2 = scanner.nextLine();

        System.out.println("\n--- MONTAGEM DE EQUIPE: " + treinador1 + " ---");
        List<Pokemon> equipeP1 = escolherEquipe(treinador1);
        aguardarTrocaDeJogador(treinador2);

        System.out.println("\n--- MONTAGEM DE EQUIPE: " + treinador2 + " ---");
        List<Pokemon> equipeP2 = escolherEquipe(treinador2);
        aguardarTrocaDeJogador("Início da Batalha");

        Pokemon pokemonAtivoP1 = equipeP1.get(0);
        Pokemon pokemonAtivoP2 = equipeP2.get(0);

        boolean trocouRecentementeP1 = false;
        boolean trocouRecentementeP2 = false;

        System.out.println("🔥 BATALHA INICIADA: " + pokemonAtivoP1.getNome() + " (" + treinador1 + ") vs " + pokemonAtivoP2.getNome() + " (" + treinador2 + ")! 🔥\n");

        boolean jogoAtivo = true;
        while (jogoAtivo) {
            // --- Turno do Treinador 1 ---
            Object[] resultadoP1 = executarTurnoCompleto(treinador1, pokemonAtivoP1, pokemonAtivoP2, equipeP1, trocouRecentementeP1);
            pokemonAtivoP1 = (Pokemon) resultadoP1[0];
            trocouRecentementeP1 = (Boolean) resultadoP1[1];

            if (pokemonAtivoP2.estaDesmaiado()) {
                System.out.println("\n💀 O " + pokemonAtivoP2.getNome() + " de " + treinador2 + " desmaiou!");

                if (temPokemonsDisponiveis(equipeP2)) {
                    pokemonAtivoP2 = escolherSubstituto(treinador2, equipeP2);
                    trocouRecentementeP2 = true;
                } else {
                    System.out.println("\n🏆 " + treinador1 + " venceu a batalha e tornou-se um Mestre Pokémon!");
                    break;
                }
            }

            pressionarEnterParaContinuar();

            // --- Turno do Treinador 2 ---
            Object[] resultadoP2 = executarTurnoCompleto(treinador2, pokemonAtivoP2, pokemonAtivoP1, equipeP2, trocouRecentementeP2);
            pokemonAtivoP2 = (Pokemon) resultadoP2[0];
            trocouRecentementeP2 = (Boolean) resultadoP2[1];

            if (pokemonAtivoP1.estaDesmaiado()) {
                System.out.println("\n💀 O " + pokemonAtivoP1.getNome() + " de " + treinador1 + " desmaiou!");

                if (temPokemonsDisponiveis(equipeP1)) {
                    pokemonAtivoP1 = escolherSubstituto(treinador1, equipeP1);
                    trocouRecentementeP1 = true;
                } else {
                    System.out.println("\n🏆 " + treinador2 + " venceu a batalha e tornou-se um Mestre Pokémon!");
                    break;
                }
            }

            pressionarEnterParaContinuar();
        }
    }

    private Object[] executarTurnoCompleto(String nomeTreinador, Pokemon atacante, Pokemon defensor, List<Pokemon> equipe, boolean trocouRecentemente) {
        System.out.println("\n----------------------------------------");
        System.out.println("Turno de " + nomeTreinador + " (" + atacante.getNome() + " - HP: " + atacante.getHpAtual() + "/" + atacante.getHpMaximo() + ")");

        int pokemonsReservaVivos = 0;
        for (Pokemon p : equipe) {
            if (p != atacante && !p.estaDesmaiado()) {
                pokemonsReservaVivos++;
            }
        }

        int opcao = 1;
        boolean podeTrocar = (pokemonsReservaVivos > 0 && !trocouRecentemente);

        if (podeTrocar) {
            System.out.println("O que deseja fazer?");
            System.out.println("1. Atacar");
            System.out.println("2. Trocar de Pokémon");
            System.out.print("Escolha uma opção: ");
            opcao = scanner.nextInt();
            scanner.nextLine();
        } else {
            if (trocouRecentemente) {
                System.out.println("⚠️ O teu " + atacante.getNome() + " acabou de entrar em campo e não pode ser trocado neste turno!");
            } else {
                System.out.println("⚠️ Não tens mais Pokémons de reserva disponíveis!");
            }
        }

        if (podeTrocar && opcao == 2) {
            Pokemon novoPokemon = escolherSubstituto(nomeTreinador, equipe);
            return new Object[]{novoPokemon, true};
        } else {
            for (Movimento m : atacante.getMovimentos()) {
                m.decrementarCooldown();
            }

            System.out.println("Escolha um movimento:");
            List<Movimento> movimentos = atacante.getMovimentos();
            for (int i = 0; i < movimentos.size(); i++) {
                Movimento m = movimentos.get(i);
                String statusRecarga = m.estaEmRecarga() ? " ⏳ [Em Recarga: " + m.getCooldownAtual() + " turnos]" : "";
                System.out.println((i + 1) + ". " + m.getNome() + " (Tipo: " + m.getTipo() + " | Poder: " + m.getPoder() + ")" + statusRecarga);
            }

            System.out.print("Digite o número do movimento: ");
            int escolhaMovimento = scanner.nextInt();
            scanner.nextLine();

            int indice = (escolhaMovimento >= 1 && escolhaMovimento <= movimentos.size()) ? escolhaMovimento - 1 : 0;
            Movimento movimentoEscolhido = movimentos.get(indice);

            if (movimentoEscolhido.estaEmRecarga()) {
                System.out.println("⚠️ Este movimento está em recarga! O Pokémon perdeu a vez.");
            } else {
                batalhaService.executarAtaque(atacante, defensor, movimentoEscolhido);
                movimentoEscolhido.aplicarCooldown();
            }

            return new Object[]{atacante, false};
        }
    }

    private List<Pokemon> escolherEquipe(String nomeTreinador) {
        List<Pokemon> equipe = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            boolean pokemonValido = false;
            Pokemon p = null;

            while (!pokemonValido) {
                System.out.println("\nTreinador Pokémon " + nomeTreinador + ", escolha o " + i + "º Pokémon para a sua equipe:");
                System.out.println("1. Charmander (Fogo)");
                System.out.println("2. Bulbasaur (Planta)");
                System.out.println("3. Squirtle (Água)");
                System.out.println("4. Pikachu (Elétrico)");
                System.out.println("5. Eevee (Normal)");
                System.out.println("6. Psyduck (Água)");
                System.out.println("7. Machop (Lutador)");
                System.out.println("8. Geodude (Pedra)");
                System.out.println("9. Gastly (Fantasma)");
                System.out.println("10. Jigglypuff (Normal)");
                System.out.println("11. Snorlax (Normal)");
                System.out.println("12. Gengar (Fantasma)");
                System.out.println("13. Arcanine (Fogo)");
                System.out.println("14. Gyarados (Água)");
                System.out.println("15. Alakazam (Psíquico)");
                System.out.println("16. Lapras (Água)");
                System.out.println("17. Dragonite (Dragão)");
                System.out.println("18. Electabuzz (Elétrico)");
                System.out.print("Digite o número (Nível 5): ");

                int escolha = scanner.nextInt();
                scanner.nextLine();

                p = switch (escolha) {
                    case 1 -> new Charmander(5);
                    case 2 -> new Bulbasaur(5);
                    case 3 -> new Squirtle(5);
                    case 4 -> new Pikachu(5);
                    case 5 -> new Eevee(5);
                    case 6 -> new Psyduck(5);
                    case 7 -> new Machop(5);
                    case 8 -> new Geodude(5);
                    case 9 -> new Gastly(5);
                    case 10 -> new Jigglypuff(5);
                    case 11 -> new Snorlax(5);
                    case 12 -> new Gengar(5);
                    case 13 -> new Arcanine(5);
                    case 14 -> new Gyarados(5);
                    case 15 -> new Alakazam(5);
                    case 16 -> new Lapras(5);
                    case 17 -> new Dragonite(5);
                    case 18 -> new Electabuzz(5);
                    default -> new Eevee(5);
                };

                boolean jaExiste = false;
                for (Pokemon existente : equipe) {
                    if (existente.getNome().equalsIgnoreCase(p.getNome())) {
                        jaExiste = true;
                        break;
                    }
                }

                if (jaExiste) {
                    System.out.println("⚠️ Já tens um " + p.getNome() + " na tua equipe! Escolhe um Pokémon diferente.");
                } else {
                    pokemonValido = true;
                }
            }

            equipe.add(p);
            System.out.println("✅ " + p.getNome() + " adicionado à equipe!");
        }

        for (int i = 0; i < 40; i++) {
            System.out.println();
        }

        return equipe;
    }

    private Pokemon escolherSubstituto(String nomeTreinador, List<Pokemon> equipe) {
        System.out.println("\nTreinador Pokémon " + nomeTreinador + ", escolha o seu próximo Pokémon para entrar em campo:");

        List<Pokemon> disponiveis = new ArrayList<>();
        int index = 1;
        for (Pokemon p : equipe) {
            if (!p.estaDesmaiado()) {
                System.out.println(index + ". " + p.getNome() + " (HP: " + p.getHpAtual() + "/" + p.getHpMaximo() + ")");
                disponiveis.add(p);
                index++;
            }
        }

        System.out.print("Digite o número do Pokémon: ");
        int escolha = scanner.nextInt();
        scanner.nextLine();

        int indiceReal = (escolha >= 1 && escolha <= disponiveis.size()) ? escolha - 1 : 0;
        Pokemon escolhido = disponiveis.get(indiceReal);

        System.out.println("✨ " + escolhido.getNome() + " entrou em campo!");
        return escolhido;
    }

    private boolean temPokemonsDisponiveis(List<Pokemon> equipe) {
        for (Pokemon p : equipe) {
            if (!p.estaDesmaiado()) return true;
        }
        return false;
    }

    private void aguardarTrocaDeJogador(String proximoTreinador) {
        for (int i = 0; i < 50; i++) System.out.println();
    }

    private void pressionarEnterParaContinuar() {
        System.out.println("\n👉 Pressione ENTER para avançar para o próximo turno...");
        scanner.nextLine();
    }

    private void limparTela() {
        for (int i = 0; i < 50; i++) System.out.println();
    }
}