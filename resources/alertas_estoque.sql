CREATE TABLE IF NOT EXISTS alertas_estoque (
    id_alerta BIGINT NOT NULL AUTO_INCREMENT,
    id_venda INT NOT NULL,
    id_produto INT NOT NULL,
    produto VARCHAR(150) NOT NULL,
    quantidade INT NOT NULL,
    estoque_minimo INT NOT NULL,
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_alerta),
    UNIQUE KEY alerta_por_venda_produto (id_venda, id_produto),
    KEY alertas_por_data (criado_em, id_alerta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
