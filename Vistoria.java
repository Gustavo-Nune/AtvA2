public class Vistoria {
    private final Estado pneu;
    private final Estado freio;
    private final Estado corrente;

    public Vistoria(Estado pneu, Estado freio, Estado corrente) {
        this.pneu = pneu;
        this.freio = freio;
        this.corrente = corrente;
    }

    public boolean temDefeito() {
        return pneu != Estado.BOM || freio != Estado.BOM || corrente != Estado.BOM;
    }

    public String getFeedback() {
        if (!temDefeito()) return "Bicicleta devolvida em perfeito estado.";
        StringBuilder sb = new StringBuilder("Atenção, problemas encontrados:");
        if (pneu != Estado.BOM) sb.append(" pneu ").append(pneu.getDescricao()).append(";");
        if (freio != Estado.BOM) sb.append(" freio ").append(freio.getDescricao()).append(";");
        if (corrente != Estado.BOM) sb.append(" corrente ").append(corrente.getDescricao()).append(";");
        return sb.toString();
    }
}
