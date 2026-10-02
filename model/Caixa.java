package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Caixa {
    private int idAbertura;
    private String idCaixa;
    private Usuario usuario;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private BigDecimal valorAbertura;
    private BigDecimal valorFechamento;
    private StatusCaixa status;


    public int getIdAbertura() {
        return idAbertura;
    }
    public void setIdAbertura(int idAbertura) {
        this.idAbertura = idAbertura;
    }
    public String getIdCaixa() {
        return idCaixa;
    }
    public void setIdCaixa(String idCaixa) {
        this.idCaixa = idCaixa;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
