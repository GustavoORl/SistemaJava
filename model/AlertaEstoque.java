package model;

public record AlertaEstoque(String produto, int quantidade, int minimo, Tipo tipo) {
    public enum Tipo {
        ENTRADA("Entrada de estoque"),
        SAIDA("Saída de estoque"),
        AJUSTE("Ajuste de estoque"),
        ESTOQUE_BAIXO("Estoque baixo");

        private final String rotulo;

        Tipo(String rotulo) {
            this.rotulo = rotulo;
        }

        public String rotulo() {
            return rotulo;
        }
    }

    public AlertaEstoque {
        if (produto == null || produto.length() > 300 || quantidade < 0 || tipo == null
                || (tipo == Tipo.ESTOQUE_BAIXO && quantidade >= minimo)) {
            throw new IllegalArgumentException("Alerta de estoque inválido.");
        }
    }
}
