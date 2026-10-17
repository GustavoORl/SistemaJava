package model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

public class Promocao {
    private static final BigDecimal CEM = new BigDecimal("100");
    private static final int CASAS_DECIMAIS = 2;
    private static final RoundingMode MODO_ARREDONDAMENTO = RoundingMode.HALF_UP;

    private String palavraChave;
    private BigDecimal percentual;

    public Promocao(String palavraChave, BigDecimal percentual) {
        setPalavraChave(palavraChave);
        setPercentual(percentual);
    }

    public String getPalavraChave() { return palavraChave;}

    public void setPalavraChave(String palavraChave) {
        if(palavraChave == null || palavraChave.trim().isEmpty()) {
            throw new IllegalArgumentException("A palavra-chave da promoção não pode ser nula ou vazia.");
        } else {
            this.palavraChave = palavraChave.trim().toLowerCase(Locale.ROOT);
        }
    }

    public BigDecimal getPercentual() { return percentual;}

    public void setPercentual(BigDecimal percentual) {
        if(percentual == null || percentual.compareTo(BigDecimal.ZERO) < 0 || percentual.compareTo(CEM) > 0) {
            throw new IllegalArgumentException("O percentual da promoção deve ser um valor entre 0 e 100.");
        } else {
            this.percentual = percentual.setScale(CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
        }
    }

    public boolean produtoElegivel(Produto produto) {
        if(produto == null || produto.getDescricao() == null) {
            return false;
        }

        String descricao = produto.getDescricao().toLowerCase(Locale.ROOT);

        return descricao.contains(palavraChave);
    }

    private BigDecimal calcularDescontoUnitario(BigDecimal precoOriginal) {
        return precoOriginal
                .multiply(percentual)
                .divide(CEM, CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
    }

    public BigDecimal calcularPrecoPromocional(Produto produto) {
        if(produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }

        BigDecimal precoOriginal = produto.getPrecoVenda();

        if(precoOriginal == null || precoOriginal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço original não pode ser nulo nem negativo.");
        }

        BigDecimal descontoUnitario =calcularDescontoUnitario(precoOriginal);

        return precoOriginal
                .subtract(descontoUnitario)
                .setScale(CASAS_DECIMAIS, MODO_ARREDONDAMENTO);
    }

    public boolean aplicar(itemVenda item) {
        if(item == null) {
            throw new IllegalArgumentException("O item não pode ser nulo.");
        }
        
        Produto produto = item.getProduto();
        if(produto == null) {
            throw new IllegalArgumentException("O produto do item não pode ser nulo.");
        }

        if(!produtoElegivel(produto)) {
            return false;
        }

        BigDecimal quantidade = item.getQuantidade();
        if(quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0){
            throw new IllegalArgumentException("A quantidade do item deve ser maior que zero.");
        }

        BigDecimal precoOriginal = produto.getPrecoVenda();
        BigDecimal precoPromocional = calcularPrecoPromocional(produto);

        BigDecimal descontoTotal = precoOriginal.subtract(precoPromocional)
                                                .multiply(quantidade)
                                                .setScale(CASAS_DECIMAIS, MODO_ARREDONDAMENTO);

        BigDecimal subtotal = precoPromocional.multiply(quantidade)
                                              .setScale(CASAS_DECIMAIS, MODO_ARREDONDAMENTO);

        item.setPrecoUnitario(precoPromocional);
        item.setDesconto(descontoTotal);
        item.setSubtotal(subtotal);

        return true;
    }

    public int aplicar (List<itemVenda> itens) {
        if(itens == null) {
            throw new IllegalArgumentException("A lista de itens não pode ser nula.");
        }

        int aplicados = 0;

        for(itemVenda item : itens) {
            if(item != null && aplicar(item)) {
                aplicados++;
            }
        }
        return aplicados;
    }
}