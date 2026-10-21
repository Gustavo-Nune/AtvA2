import java.time.LocalDate;

public class Bicicleta extends Equipamento {
    public Bicicleta(int id, String nome) { super(id, nome); }

    @Override public String getTipo() { return "Bicicleta"; }
    @Override public LocalDate calcularDataLimite(LocalDate retirada) { return retirada.plusDays(7); }
    @Override public boolean exigeVistoria() { return true; }
}
