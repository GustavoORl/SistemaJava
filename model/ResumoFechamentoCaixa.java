package model;

import java.math.BigDecimal;

public class ResumoFechamentoCaixa {

    private int idTabelaCaixa;

    private BigDecimal valorAbertura = BigDecimal.ZERO;
    private BigDecimal totalDinheiro = BigDecimal.ZERO;
    private BigDecimal totalPix = BigDecimal.ZERO;
    private BigDecimal totalDebito = BigDecimal.ZERO;
    private BigDecimal totalCredito = BigDecimal.ZERO;
    private BigDecimal totalPrazo = BigDecimal.ZERO;

    private int quantidadeVendas;

    public ResumoFechamentoCaixa() {
    }

    public int getIdTabelaCaixa() {
        return idTabelaCaixa;
    }

    public void setIdTabelaCaixa(int idTabelaCaixa) {
        this.idTabelaCaixa = idTabelaCaixa;
    }

    public BigDecimal getValorAbertura() {
        return valorAbertura;
    }

    public void setValorAbertura(BigDecimal valorAbertura) {
        this.valorAbertura = valorOuZero(valorAbertura);
    }

    public BigDecimal getTotalDinheiro() {
        return totalDinheiro;
    }

    public void setTotalDinheiro(BigDecimal totalDinheiro) {
        this.totalDinheiro = valorOuZero(totalDinheiro);
    }

    public BigDecimal getTotalPix() {
        return totalPix;
    }

    public void setTotalPix(BigDecimal totalPix) {
        this.totalPix = valorOuZero(totalPix);
    }

    public BigDecimal getTotalDebito() {
        return totalDebito;
    }

    public void setTotalDebito(BigDecimal totalDebito) {
        this.totalDebito = valorOuZero(totalDebito);
    }

    public BigDecimal getTotalCredito() {
        return totalCredito;
    }

    public void setTotalCredito(BigDecimal totalCredito) {
        this.totalCredito = valorOuZero(totalCredito);
    }

    public BigDecimal getTotalPrazo() {
    return totalPrazo;
}

public void setTotalPrazo(BigDecimal totalPrazo) {
    this.totalPrazo = totalPrazo == null
            ? BigDecimal.ZERO
            : totalPrazo;
}

    public int getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public void setQuantidadeVendas(int quantidadeVendas) {
        this.quantidadeVendas = quantidadeVendas;
    }

    public BigDecimal getTotalVendas() {
    return totalDinheiro
            .add(totalPix)
            .add(totalDebito)
            .add(totalCredito)
            .add(totalPrazo);
}

    public BigDecimal getDinheiroEsperado() {
        return valorAbertura.add(totalDinheiro);
    }

    public BigDecimal calcularDiferenca(
            BigDecimal valorContado
    ) {
        if (valorContado == null) {
            throw new IllegalArgumentException(
                    "O valor contado não pode ser nulo."
            );
        }

        return valorContado.subtract(
                getDinheiroEsperado()
        );
    }

    public boolean caixaConferido(
            BigDecimal valorContado
    ) {
        return calcularDiferenca(valorContado)
                .compareTo(BigDecimal.ZERO) == 0;
    }

    private BigDecimal valorOuZero(BigDecimal valor) {
        return valor == null
                ? BigDecimal.ZERO
                : valor;
    }
}
