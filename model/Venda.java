package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Venda {
    private int idVenda;
    private LocalDateTime dataVenda;
    private BigDecimal valorTotal;
    private StatusVenda status;
    //private Cliente cliente;
    private Usuario usuario;
    private Caixa caixa;
    private List<itemVenda> itens;
    private List<Pagamento> pagamentos;


    public int getIdVenda() {
        return idVenda;
    }
    public void setIdVenda(int idVenda) {
        this.idVenda = idVenda;
    }
    public LocalDateTime getDataVenda() {
        return dataVenda;
    }
    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }
    public BigDecimal getValorTotal() {
        return valorTotal;
    }
    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
    public StatusVenda getStatus() {
        return status;
    }
    public void setStatus(StatusVenda status) {
        this.status = status;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public Caixa getCaixa() {
        return caixa;
    }
    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }
    public List<itemVenda> getItens() {
        return itens;
    }
    public void setItens(List<itemVenda> itens) {
        this.itens = itens;
    }
    public List<Pagamento> getPagamentos() {
        return pagamentos;
    }
    public void setPagamentos(List<Pagamento> pagamentos) {
        this.pagamentos = pagamentos;
    }

    
}
