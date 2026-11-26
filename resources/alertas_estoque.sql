CREATE TABLE IF NOT EXISTS alertas_estoque (
    id_alerta BIGINT NOT NULL AUTO_INCREMENT,
    id_venda INT NULL,
    id_produto INT NOT NULL,
    produto VARCHAR(150) NOT NULL,
    quantidade INT NOT NULL,
    estoque_minimo INT NOT NULL,
    tipo VARCHAR(20) NOT NULL DEFAULT 'ESTOQUE_BAIXO',
    criado_em DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_alerta),
    UNIQUE KEY alerta_por_venda_produto (id_venda, id_produto),
    KEY alertas_por_data (criado_em, id_alerta)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET @atualizar_alertas_estoque = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE alertas_estoque MODIFY COLUMN id_venda INT NULL, ADD COLUMN tipo VARCHAR(20) NOT NULL DEFAULT ''ESTOQUE_BAIXO''',
        'DO 0')
    FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = 'alertas_estoque' AND column_name = 'tipo'
);
PREPARE atualizar_alertas_estoque FROM @atualizar_alertas_estoque;
EXECUTE atualizar_alertas_estoque;
DEALLOCATE PREPARE atualizar_alertas_estoque;
