package model;

import java.math.BigDecimal;

public class itemVenda {
    private int idItem;
    private Venda venda;
    private Produto produto;
    private BigDecimal quantidade;
    private BigDecimal precoUnitario;
    private BigDecimal desconto;
    private BigDecimal subtotal;


    public int getIdItem() {
        return idItem;
    }
    public void setIdItem(int idItem) {
        this.idItem = idItem;
    }
    public Venda getVenda() {
        return venda;
    }
    public void setVenda(Venda venda) {
        this.venda = venda;
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
    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        this.precoUnitario = precoUnitario;
    }
    public BigDecimal getDesconto() {
        return desconto;
    }
    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    
}
