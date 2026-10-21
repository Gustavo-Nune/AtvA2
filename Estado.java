public enum Estado {
    BOM("bom"), REGULAR("regular"), RUIM("ruim");

    private final String descricao;

    Estado(String descricao) { this.descricao = descricao; }

    public String getDescricao() { return descricao; }
}
