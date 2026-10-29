
CREATE DATABASE IF NOT EXISTS gestao_vendas;
USE gestao_vendas;

CREATE TABLE IF NOT EXISTS cargos (
    id_cargo INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    PRIMARY KEY (id_cargo)
);

CREATE TABLE IF NOT EXISTS permissoes (
    id_permissao INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    PRIMARY KEY (id_permissao)
);

CREATE TABLE IF NOT EXISTS cargo_permissao (
    id_cargo INT NOT NULL,
    id_permissao INT NOT NULL,
    PRIMARY KEY (id_cargo, id_permissao),
    FOREIGN KEY (id_cargo) REFERENCES cargos(id_cargo),
    FOREIGN KEY (id_permissao) REFERENCES permissoes(id_permissao)
);

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    telefone VARCHAR(20),
    email VARCHAR(100) NOT NULL UNIQUE,
    endereco VARCHAR(255),
    senha VARCHAR(255) NOT NULL,
    id_cargo INT NOT NULL,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (id_usuario),
    FOREIGN KEY (id_cargo) REFERENCES cargos(id_cargo)
);

CREATE TABLE IF NOT EXISTS categorias (
    id_categoria INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    PRIMARY KEY (id_categoria)
);

CREATE TABLE IF NOT EXISTS produtos (
    id_produto INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(150) NOT NULL,
    descricao VARCHAR(255),
    codigo_barras VARCHAR(50) UNIQUE,
    preco_custo DECIMAL(10,2) NOT NULL,
    preco_venda DECIMAL(10,2) NOT NULL,
    estoque_minimo INT NOT NULL DEFAULT 0,
    ativo TINYINT(1) NOT NULL DEFAULT 1,
    id_categoria INT,
    PRIMARY KEY (id_produto),
    FOREIGN KEY (id_categoria) REFERENCES categorias(id_categoria)
);

CREATE TABLE IF NOT EXISTS estoque (
    id_estoque INT NOT NULL AUTO_INCREMENT,
    id_produto INT NOT NULL UNIQUE,
    quantidade INT NOT NULL DEFAULT 0,
    data_atualizacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_estoque),
    FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)
);

CREATE TABLE IF NOT EXISTS movimentacoes_estoque (
    id_movimentacao INT NOT NULL AUTO_INCREMENT,
    id_produto INT NOT NULL,
    id_usuario INT NOT NULL,
    tipo ENUM('ENTRADA','SAIDA','AJUSTE') NOT NULL,
    quantidade INT NOT NULL,
    motivo VARCHAR(255),
    data_movimentacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_movimentacao),
    FOREIGN KEY (id_produto) REFERENCES produtos(id_produto),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS perdas (
    id_perda INT NOT NULL AUTO_INCREMENT,
    id_produto INT NOT NULL,
    id_usuario INT NOT NULL,
    tipo ENUM(
        'VENCIDO',
        'DANIFICADO',
        'FURTO',
        'ERRO_ESTOQUE'
    ) NOT NULL,
    quantidade INT NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    data_perda DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_perda),
    FOREIGN KEY (id_produto) REFERENCES produtos(id_produto),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    telefone VARCHAR(20),
    email VARCHAR(100),
    endereco VARCHAR(200),
    PRIMARY KEY (id_cliente)
);

CREATE TABLE IF NOT EXISTS fornecedores (
    id_fornecedor INT NOT NULL AUTO_INCREMENT,
    razao_social VARCHAR(150) NOT NULL,
    nome_fantasia VARCHAR(150),
    cnpj VARCHAR(18) UNIQUE,
    telefone VARCHAR(20),
    email VARCHAR(100),
    endereco VARCHAR(200),
    PRIMARY KEY (id_fornecedor)
);

CREATE TABLE IF NOT EXISTS entradas_xml (
    id_entrada_xml INT NOT NULL AUTO_INCREMENT,
    id_fornecedor INT,
    id_usuario INT NOT NULL,
    numero_nota VARCHAR(50),
    chave_nfe VARCHAR(44) UNIQUE,
    data_emissao DATE,
    data_importacao DATETIME DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10,2),
    arquivo_xml LONGTEXT,
    PRIMARY KEY (id_entrada_xml),
    FOREIGN KEY (id_fornecedor) REFERENCES fornecedores(id_fornecedor),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS caixas (
    id_abertura INT NOT NULL AUTO_INCREMENT,
    id_caixa INT NOT NULL,
    id_usuario INT NOT NULL,
    data_abertura DATETIME DEFAULT CURRENT_TIMESTAMP,
    data_fechamento DATETIME,
    valor_abertura DECIMAL(10,2) NOT NULL,
    valor_fechamento DECIMAL(10,2),
    status ENUM('ABERTO','FECHADO') DEFAULT 'ABERTO',
    PRIMARY KEY (id_abertura),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS formas_pagamento (
    id_forma_pagamento INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(50) NOT NULL,
    ativo TINYINT(1) DEFAULT 1,
    taxa_percentual DECIMAL(5,2) DEFAULT 0.00,
    PRIMARY KEY (id_forma_pagamento)
);

CREATE TABLE IF NOT EXISTS vendas (
    id_venda INT NOT NULL AUTO_INCREMENT,
    id_cliente INT,
    id_usuario INT NOT NULL,
    data_venda DATETIME DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    status ENUM(
        'ABERTA',
        'FINALIZADA',
        'CANCELADA'
    ) DEFAULT 'ABERTA',
    numero_comprovante VARCHAR(50),
    motivo_cancelamento VARCHAR(255),
    id_usuario_cancelamento INT,
    PRIMARY KEY (id_venda),
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente),
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    FOREIGN KEY (id_usuario_cancelamento)
        REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS itens_venda (
    id_item INT NOT NULL AUTO_INCREMENT,
    id_venda INT NOT NULL,
    id_produto INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    desconto DECIMAL(10,2) DEFAULT 0.00,
    subtotal DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (id_item),
    FOREIGN KEY (id_venda) REFERENCES vendas(id_venda),
    FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)
);

CREATE TABLE IF NOT EXISTS pagamentos (
    id_pagamento INT NOT NULL AUTO_INCREMENT,
    id_venda INT NOT NULL,
    id_caixa INT NOT NULL,
    id_forma_pagamento INT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_pagamento DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_pagamento),
    FOREIGN KEY (id_venda) REFERENCES vendas(id_venda),
    FOREIGN KEY (id_caixa) REFERENCES caixas(id_abertura),
    FOREIGN KEY (id_forma_pagamento)
        REFERENCES formas_pagamento(id_forma_pagamento)
);

CREATE TABLE IF NOT EXISTS promocoes (
    id_promocao INT NOT NULL AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    tipo ENUM('PERCENTUAL','VALOR_FIXO') NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    ativa TINYINT(1) DEFAULT 1,
    PRIMARY KEY (id_promocao)
);

CREATE TABLE IF NOT EXISTS promocao_produtos (
    id_promocao INT NOT NULL,
    id_produto INT NOT NULL,
    PRIMARY KEY (id_promocao, id_produto),
    FOREIGN KEY (id_promocao) REFERENCES promocoes(id_promocao),
    FOREIGN KEY (id_produto) REFERENCES produtos(id_produto)
);
