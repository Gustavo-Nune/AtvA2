import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Estoque {
    private final List<Equipamento> equipamentos = new ArrayList<>();
    private final List<Pessoa> pessoas = new ArrayList<>();
    private final List<Emprestimo> historico = new ArrayList<>();

    public void adicionarEquipamento(Equipamento e) { equipamentos.add(e); }

    // ----- pessoas -----
    public Pessoa buscarPessoa(String tipo, String nome) {
        for (Pessoa p : pessoas) {
            if (p.getTipo().equalsIgnoreCase(tipo) && p.getNome().equalsIgnoreCase(nome.trim())) return p;
        }
        return null;
    }

    public void cadastrarPessoa(Pessoa p) { pessoas.add(p); }

    // ----- consultas -----
    /** Nome do equipamento -> quantidade disponível */
    public Map<String, Integer> resumoDisponiveis() {
        Map<String, Integer> resumo = new LinkedHashMap<>();
        for (Equipamento e : equipamentos) {
            resumo.putIfAbsent(e.getNome(), 0);
            if (e.estaDisponivel()) resumo.merge(e.getNome(), 1, Integer::sum);
        }
        return resumo;
    }

    public List<Emprestimo> emprestimosAtivos() {
        List<Emprestimo> r = new ArrayList<>();
        for (Emprestimo e : historico) if (e.estaAtivo()) r.add(e);
        return r;
    }

    public List<Emprestimo> emprestimosAtivosDe(Pessoa p) {
        List<Emprestimo> r = new ArrayList<>();
        for (Emprestimo e : emprestimosAtivos()) if (e.getPessoa() == p) r.add(e);
        return r;
    }

    public List<Emprestimo> historicoDe(Equipamento eq) {
        List<Emprestimo> r = new ArrayList<>();
        for (Emprestimo e : historico) if (e.getEquipamento() == eq) r.add(e);
        return r;
    }

    public List<Equipamento> getEquipamentos() { return new ArrayList<>(equipamentos); }

    // ----- operações -----
    public List<Emprestimo> emprestar(Pessoa pessoa, String nomeEquipamento, int quantidade, LocalDate data) {
        if (quantidade <= 0) throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        if (pessoa.estaBloqueada(data)) {
            throw new IllegalStateException(pessoa.getNome() + " está bloqueado(a) até "
                    + pessoa.getBloqueadoAte() + " por atraso em devolução.");
        }
        int emUso = emprestimosAtivosDe(pessoa).size();
        if (emUso + quantidade > pessoa.getLimiteItens()) {
            throw new IllegalStateException(pessoa.getTipo() + " pode ter no máximo " + pessoa.getLimiteItens()
                    + " itens ao mesmo tempo (já possui " + emUso + ").");
        }

        List<Equipamento> escolhidos = new ArrayList<>();
        for (Equipamento e : equipamentos) {
            if (escolhidos.size() == quantidade) break;
            if (e.getNome().equals(nomeEquipamento) && e.estaDisponivel()) escolhidos.add(e);
        }
        if (escolhidos.size() < quantidade) {
            throw new IllegalStateException("Só há " + escolhidos.size() + " unidade(s) disponível(is) de "
                    + nomeEquipamento + ".");
        }

        List<Emprestimo> novos = new ArrayList<>();
        for (Equipamento e : escolhidos) {
            e.emprestar();
            Emprestimo emp = new Emprestimo(pessoa, e, data);
            historico.add(emp);
            novos.add(emp);
        }
        return novos;
    }

    /** Devolve o item. Retorna o atraso (ou null se foi no prazo). */
    public AtrasoDevolucao devolver(Emprestimo emp, LocalDate data, Vistoria vistoria) {
        if (emp.getEquipamento().exigeVistoria() && vistoria == null) {
            throw new IllegalArgumentException("Este equipamento exige vistoria na devolução.");
        }
        emp.finalizar(data, vistoria);
        emp.getEquipamento().devolver();

        AtrasoDevolucao atraso = emp.getAtraso();
        if (atraso != null) emp.getPessoa().bloquearAte(atraso.getBloqueadoAte());
        return atraso;
    }
}
