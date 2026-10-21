import java.time.LocalDate;

public class Emprestimo {
    private final Pessoa pessoa;
    private final Equipamento equipamento;
    private final LocalDate dataEmprestimo;
    private final LocalDate dataLimite;
    private LocalDate dataDevolucao;
    private Vistoria vistoria;
    private AtrasoDevolucao atraso;

    public Emprestimo(Pessoa pessoa, Equipamento equipamento, LocalDate dataEmprestimo) {
        this.pessoa = pessoa;
        this.equipamento = equipamento;
        this.dataEmprestimo = dataEmprestimo;
        this.dataLimite = equipamento.calcularDataLimite(dataEmprestimo);
    }

    public void finalizar(LocalDate data, Vistoria vistoria) {
        if (!estaAtivo()) throw new IllegalStateException("Este empréstimo já foi devolvido.");
        if (data.isBefore(dataEmprestimo)) {
            throw new IllegalArgumentException("A devolução não pode ser antes do empréstimo.");
        }
        this.dataDevolucao = data;
        this.vistoria = vistoria;
        if (data.isAfter(dataLimite)) {
            this.atraso = new AtrasoDevolucao(dataLimite, data);
        }
    }

    public boolean estaAtivo() { return dataDevolucao == null; }

    public Pessoa getPessoa() { return pessoa; }
    public Equipamento getEquipamento() { return equipamento; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public LocalDate getDataLimite() { return dataLimite; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
    public Vistoria getVistoria() { return vistoria; }
    public AtrasoDevolucao getAtraso() { return atraso; }
}
