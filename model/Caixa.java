package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Caixa {
    private int idCaixa;
    private Usuario usuarioAbertura;
    private Usuario usuarioFechamento;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private BigDecimal valorAbertura;
    private BigDecimal valorFechamento;
    private StatusCaixa status;


    public int getIdCaixa() {
        return idCaixa;
    }
    public void setIdCaixa(int idCaixa) {
        this.idCaixa = idCaixa;
    }
    public Usuario getUsuarioAbertura() {
        return usuarioAbertura;
    }
    public void setUsuarioAbertura(Usuario usuarioAbertura) {
        this.usuarioAbertura = usuarioAbertura;
    }
    public Usuario getUsuarioFechamento() {
        return usuarioFechamento;
    }
    public void setUsuarioFechamento(Usuario usuarioFechamento) {
        this.usuarioFechamento = usuarioFechamento;
    }
    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }
    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }
    public LocalDateTime getDataFechamento() {
        return dataFechamento;
    }
    public void setDataFechamento(LocalDateTime dataFechamento) {
        this.dataFechamento = dataFechamento;
    }
    public BigDecimal getValorAbertura() {
        return valorAbertura;
    }
    public void setValorAbertura(BigDecimal valorAbertura) {
        this.valorAbertura = valorAbertura;
    }
    public BigDecimal getValorFechamento() {
        return valorFechamento;
    }
    public void setValorFechamento(BigDecimal valorFechamento) {
        this.valorFechamento = valorFechamento;
    }
    public StatusCaixa getStatus() {
        return status;
    }
    public void setStatus(StatusCaixa status) {
        this.status = status;
    }

    
}
