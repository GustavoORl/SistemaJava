package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovimentacaoEstoque {
    protected int idMovimentacao;
    protected Produto produto;
    protected BigDecimal quantidade;
    protected LocalDateTime dataMovimentacao;
    protected String motivo;
    protected TipoMovimentacao tipoMovimentacao;

}
