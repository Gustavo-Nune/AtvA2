import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Menu {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Scanner scanner = new Scanner(System.in);
    private final Estoque estoque = new Estoque();

    public Menu() { carregarEstoqueInicial(); }

    private void carregarEstoqueInicial() {
        int id = 1;
        for (int i = 0; i < 2; i++) estoque.adicionarEquipamento(new BolaColete(id++, "Bola de futsal", "Bola"));
        for (int i = 0; i < 2; i++) estoque.adicionarEquipamento(new BolaColete(id++, "Bola de vôlei", "Bola"));
        for (int i = 0; i < 2; i++) estoque.adicionarEquipamento(new BolaColete(id++, "Bola de basquete", "Bola"));
        for (int i = 0; i < 6; i++) estoque.adicionarEquipamento(new BolaColete(id++, "Colete", "Colete"));
        for (int i = 0; i < 4; i++) estoque.adicionarEquipamento(new Raquete(id++, "Raquete de tênis de mesa"));
        for (int i = 0; i < 3; i++) estoque.adicionarEquipamento(new Bicicleta(id++, "Bicicleta"));
    }

    public void executar() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n===== ATLÉTICA - EMPRÉSTIMOS =====");
            System.out.println("1 - Emprestar equipamento");
            System.out.println("2 - Devolver equipamento");
            System.out.println("3 - Ver equipamentos disponíveis");
            System.out.println("4 - Ver equipamentos emprestados");
            System.out.println("5 - Ver histórico de um equipamento");
            System.out.println("0 - Sair");
            try {
                opcao = lerInt("Escolha uma opção: ");
                switch (opcao) {
                    case 1 -> emprestar();
                    case 2 -> devolver();
                    case 3 -> mostrarDisponiveis();
                    case 4 -> mostrarEmprestados();
                    case 5 -> mostrarHistorico();
                    case 0 -> System.out.println("Até logo!");
                    default -> System.out.println("Opção inválida. Digite um número do menu.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida: digite apenas números.");
                opcao = -1;
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato dia/mês/ano, por exemplo 16/10/2026.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Não foi possível concluir: " + e.getMessage());
            }
        }
    }

    // ---------- fluxos ----------
    private void emprestar() {
        Pessoa pessoa = identificarPessoa(true);
        LocalDate data = lerData("Data do empréstimo (dd/mm/aaaa): ");

        Map<String, Integer> disponiveis = estoque.resumoDisponiveis();
        List<String> nomes = new ArrayList<>(disponiveis.keySet());
        System.out.println("Equipamentos:");
        for (int i = 0; i < nomes.size(); i++) {
            System.out.println((i + 1) + " - " + nomes.get(i) + " (" + disponiveis.get(nomes.get(i)) + " disponíveis)");
        }
        int escolha = lerInt("Escolha o equipamento: ");
        if (escolha < 1 || escolha > nomes.size()) throw new IllegalArgumentException("Equipamento inexistente.");
        int quantidade = lerInt("Quantidade: ");

        List<Emprestimo> novos = estoque.emprestar(pessoa, nomes.get(escolha - 1), quantidade, data);

        System.out.println("\n----- TICKET -----");
        System.out.println("Nome: " + pessoa.getNome() + " (" + pessoa.getTipo() + ")");
        System.out.println("Data do empréstimo: " + data.format(FORMATO));
        for (Emprestimo e : novos) {
            System.out.println("Item: " + e.getEquipamento() + " | devolver até: " + e.getDataLimite().format(FORMATO));
        }
        System.out.println("------------------");
    }

    private void devolver() {
        Pessoa pessoa = identificarPessoa(false);
        List<Emprestimo> ativos = estoque.emprestimosAtivosDe(pessoa);
        if (ativos.isEmpty()) {
            System.out.println(pessoa.getNome() + " não possui itens para devolver.");
            return;
        }
        System.out.println("Itens com " + pessoa.getNome() + ":");
        for (int i = 0; i < ativos.size(); i++) {
            Emprestimo e = ativos.get(i);
            System.out.println((i + 1) + " - " + e.getEquipamento() + " (devolver até "
                    + e.getDataLimite().format(FORMATO) + ")");
        }
        int escolha = lerInt("Qual item deseja devolver? ");
        if (escolha < 1 || escolha > ativos.size()) throw new IllegalArgumentException("Item inexistente.");
        Emprestimo emp = ativos.get(escolha - 1);

        Vistoria vistoria = null;
        if (emp.getEquipamento().exigeVistoria()) {
            System.out.println("Vistoria obrigatória:");
            Estado pneu = lerEstado("pneu");
            Estado freio = lerEstado("freio");
            Estado corrente = lerEstado("corrente");
            vistoria = new Vistoria(pneu, freio, corrente);
        }
        LocalDate data = lerData("Data da devolução (dd/mm/aaaa): ");

        AtrasoDevolucao atraso = estoque.devolver(emp, data, vistoria);
        if (vistoria != null) System.out.println(vistoria.getFeedback());
        if (atraso != null) {
            System.out.println("Devolução com " + atraso.getDiasAtraso() + " dia(s) de atraso. "
                    + pessoa.getNome() + " está bloqueado(a) até " + atraso.getBloqueadoAte().format(FORMATO) + ".");
        } else {
            System.out.println("Devolução registrada no prazo. Obrigado!");
        }
    }

    private void mostrarDisponiveis() {
        System.out.println("\n--- Disponíveis ---");
        estoque.resumoDisponiveis().forEach((nome, qtd) -> System.out.println(nome + ": " + qtd));
    }

    private void mostrarEmprestados() {
        System.out.println("\n--- Emprestados ---");
        List<Emprestimo> ativos = estoque.emprestimosAtivos();
        if (ativos.isEmpty()) System.out.println("Nenhum item emprestado.");
        for (Emprestimo e : ativos) {
            System.out.println(e.getEquipamento() + " -> " + e.getPessoa() + " | devolver até "
                    + e.getDataLimite().format(FORMATO));
        }
    }

    private void mostrarHistorico() {
        for (Equipamento e : estoque.getEquipamentos()) System.out.println(e.getId() + " - " + e);
        int id = lerInt("Digite o número do equipamento: ");
        Equipamento alvo = null;
        for (Equipamento e : estoque.getEquipamentos()) if (e.getId() == id) alvo = e;
        if (alvo == null) throw new IllegalArgumentException("Equipamento inexistente.");

        List<Emprestimo> lista = estoque.historicoDe(alvo);
        System.out.println("\n--- Histórico de " + alvo + " ---");
        if (lista.isEmpty()) System.out.println("Nunca foi emprestado.");
        for (Emprestimo e : lista) {
            String devolvido = e.estaAtivo() ? "ainda emprestado" : "devolvido em " + e.getDataDevolucao().format(FORMATO);
            String extra = e.getVistoria() != null ? " | " + e.getVistoria().getFeedback() : "";
            System.out.println(e.getPessoa() + " | retirado em " + e.getDataEmprestimo().format(FORMATO)
                    + " | " + devolvido + extra);
        }
    }

    // ---------- leitura de dados ----------
    private Pessoa identificarPessoa(boolean cadastrarSeNovo) {
        System.out.println("Tipo de pessoa: 1 - Aluno | 2 - Servidor");
        int tipo = lerInt("Escolha: ");
        if (tipo != 1 && tipo != 2) throw new IllegalArgumentException("Tipo de pessoa inválido.");
        String nomeTipo = (tipo == 1) ? "Aluno" : "Servidor";
        String nome = lerTexto("Nome: ");

        Pessoa p = estoque.buscarPessoa(nomeTipo, nome);
        if (p == null) {
            if (!cadastrarSeNovo) throw new IllegalArgumentException("Pessoa não encontrada.");
            p = (tipo == 1) ? new Aluno(nome) : new Servidor(nome);
            estoque.cadastrarPessoa(p);
        }
        return p;
    }

    private Estado lerEstado(String parte) {
        int op = lerInt("Estado do " + parte + " (1 - bom | 2 - regular | 3 - ruim): ");
        return switch (op) {
            case 1 -> Estado.BOM;
            case 2 -> Estado.REGULAR;
            case 3 -> Estado.RUIM;
            default -> throw new IllegalArgumentException("Estado inválido.");
        };
    }

    private int lerInt(String msg) {
        System.out.print(msg);
        return Integer.parseInt(scanner.nextLine().trim());
    }

    private String lerTexto(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }

    private LocalDate lerData(String msg) {
        System.out.print(msg);
        return LocalDate.parse(scanner.nextLine().trim(), FORMATO);
    }
}
