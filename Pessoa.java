import java.time.LocalDate;

public abstract class Pessoa {
    private final String nome;
    private LocalDate bloqueadoAte; // null = sem bloqueio

    public Pessoa(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ficar vazio.");
        }
        this.nome = nome.trim();
    }

    public String getNome() { return nome; }

    public abstract String getTipo();
    public abstract int getLimiteItens();

    public boolean estaBloqueada(LocalDate data) {
        return bloqueadoAte != null && data.isBefore(bloqueadoAte);
    }

    public LocalDate getBloqueadoAte() { return bloqueadoAte; }

    // Mantém sempre o bloqueio mais longo
    public void bloquearAte(LocalDate data) {
        if (bloqueadoAte == null || data.isAfter(bloqueadoAte)) {
            bloqueadoAte = data;
        }
    }

    @Override
    public String toString() {
        return getTipo() + " " + nome;
    }
}
