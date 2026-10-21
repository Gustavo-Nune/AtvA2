import java.time.LocalDate;

public class Raquete extends Equipamento {
    public Raquete(int id, String nome) { super(id, nome); }

    @Override public String getTipo() { return "Raquete"; }
    @Override public LocalDate calcularDataLimite(LocalDate retirada) { return retirada.plusDays(2); }
}
