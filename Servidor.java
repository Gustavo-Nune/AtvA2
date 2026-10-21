public class Servidor extends Pessoa {
    public Servidor(String nome) { super(nome); }

    @Override public String getTipo() { return "Servidor"; }
    @Override public int getLimiteItens() { return 4; }
}
