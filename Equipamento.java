import java.time.LocalDate;

public abstract class Equipamento {
    private final int id;
    private final String nome;
    private boolean disponivel = true;

    public Equipamento(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public boolean estaDisponivel() { return disponivel; }

    public abstract String getTipo();
    // Cada tipo define o seu prazo (polimorfismo)
    public abstract LocalDate calcularDataLimite(LocalDate retirada);

    public boolean exigeVistoria() { return false; }

    public void emprestar() {
        if (!disponivel) throw new IllegalStateException(nome + " #" + id + " já está emprestado.");
        disponivel = false;
    }

    public void devolver() { disponivel = true; }

    @Override
    public String toString() { return nome + " #" + id; }
}
