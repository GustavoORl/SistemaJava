package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoCaixa {
    private int idMovimentacao;
    private Caixa caixa;
    private String tipo;
    private BigDecimal valor;
    private String descricao;
    private LocalDateTime data;

    
    public int getIdMovimentacao() {
        return idMovimentacao;
    }
    public void setIdMovimentacao(int idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
    }
    public Caixa getCaixa() {
        return caixa;
    }
    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public BigDecimal getValor() {
        return valor;
    }
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }

    
}

