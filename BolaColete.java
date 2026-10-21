import java.time.LocalDate;

public class BolaColete extends Equipamento {
    private final String tipo;

    public BolaColete(int id, String nome, String tipo) {
        super(id, nome);
        this.tipo = tipo;
    }

    @Override public String getTipo() { return tipo; }

    // Até o fim do dia
    @Override public LocalDate calcularDataLimite(LocalDate retirada) { return retirada; }
}
