package model;

public record AlertaEstoque(String produto, int quantidade, int minimo) {
    public AlertaEstoque {
        if (produto == null || produto.length() > 300 || quantidade < 0 || quantidade >= minimo) {
            throw new IllegalArgumentException("Alerta de estoque inválido.");
        }
    }
}
