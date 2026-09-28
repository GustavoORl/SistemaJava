package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Estoque {
    int id;
    Produto produto;
    BigDecimal quantidade;
    LocalDateTime dataAtualizacao;


    public int getId() {
        return id;
    }
    public void setIdEstoque(int id) {
        this.id = id;
    }
    public Produto getProduto() {
        return produto;
    }
    public void setProduto(Produto produto) {
        this.produto = produto;
    }
    public BigDecimal getQuantidade() {
        return quantidade;
    }
    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }
    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
 
}
