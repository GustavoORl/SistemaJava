package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pagamento {
    private int idPagamento;
    private Venda venda;
    private BigDecimal valor;
    private LocalDateTime dataPagamento;
    private TipoFormaPagamento tipoFormaPagamento;
    private StatusPagamento status;
    
    public int getIdPagamento() {
        return idPagamento;
    }
    public void setIdPagamento(int idPagamento) {
        this.idPagamento = idPagamento;
    }
    public Venda getVenda() {
        return venda;
    }
    public void setVenda(Venda venda) {
        this.venda = venda;
    }
    public BigDecimal getValor() {
        return valor;
    }
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }
    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
    public TipoFormaPagamento getTipoFormaPagamento() {
        return tipoFormaPagamento;
    }
    public void setTipoFormaPagamento(TipoFormaPagamento tipoFormaPagamento) {
        this.tipoFormaPagamento = tipoFormaPagamento;
    }
    public StatusPagamento getStatus() {
        return status;
    }
    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    
}

