import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AtrasoDevolucao {
    private final long diasAtraso;
    private final LocalDate bloqueadoAte;

    public AtrasoDevolucao(LocalDate dataLimite, LocalDate dataDevolucao) {
        this.diasAtraso = ChronoUnit.DAYS.between(dataLimite, dataDevolucao);
        // Regra provisória: 1 dia de bloqueio para cada dia de atraso
        this.bloqueadoAte = dataDevolucao.plusDays(diasAtraso);
    }

    public long getDiasAtraso() { return diasAtraso; }
    public LocalDate getBloqueadoAte() { return bloqueadoAte; }
}
