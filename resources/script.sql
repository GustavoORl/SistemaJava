-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: gestao_vendas
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `caixas`
--
create database if not exists `gestao_vendas` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */;
use `gestao_vendas`;

DROP TABLE IF EXISTS `caixas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `caixas` (
  `id_abertura` int NOT NULL AUTO_INCREMENT,
  `id_caixa` int NOT NULL,
  `id_usuario` int NOT NULL,
  `data_abertura` datetime DEFAULT CURRENT_TIMESTAMP,
  `data_fechamento` datetime DEFAULT NULL,
  `valor_abertura` decimal(10,2) NOT NULL,
  `valor_fechamento` decimal(10,2) DEFAULT NULL,
  `status` enum('ABERTO','FECHADO') DEFAULT 'ABERTO',
  PRIMARY KEY (`id_abertura`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `caixas_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `caixas`
--

LOCK TABLES `caixas` WRITE;
/*!40000 ALTER TABLE `caixas` DISABLE KEYS */;
INSERT INTO `caixas` VALUES (1,6,2,'2026-10-27 19:46:02','2026-10-27 19:47:24',250.00,250.00,'FECHADO'),(2,3,2,'2026-10-29 19:35:04',NULL,200.00,NULL,'ABERTO');
/*!40000 ALTER TABLE `caixas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cargo_permissao`
--

DROP TABLE IF EXISTS `cargo_permissao`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cargo_permissao` (
  `id_cargo` int NOT NULL,
  `id_permissao` int NOT NULL,
  PRIMARY KEY (`id_cargo`,`id_permissao`),
  KEY `fk_cargo_permissao_permissao` (`id_permissao`),
  CONSTRAINT `fk_cargo_permissao_cargo` FOREIGN KEY (`id_cargo`) REFERENCES `cargos` (`id_cargo`),
  CONSTRAINT `fk_cargo_permissao_permissao` FOREIGN KEY (`id_permissao`) REFERENCES `permissoes` (`id_permissao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cargo_permissao`
--

LOCK TABLES `cargo_permissao` WRITE;
/*!40000 ALTER TABLE `cargo_permissao` DISABLE KEYS */;
/*!40000 ALTER TABLE `cargo_permissao` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cargos`
--

DROP TABLE IF EXISTS `cargos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cargos` (
  `id_cargo` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  PRIMARY KEY (`id_cargo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cargos`
--

LOCK TABLES `cargos` WRITE;
/*!40000 ALTER TABLE `cargos` DISABLE KEYS */;
INSERT INTO `cargos` VALUES (1,'Administrador'),(2,'Caixa'),(3,'Gerente'),(4,'Operador de Caixa');
/*!40000 ALTER TABLE `cargos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` VALUES (1,'Alimentos','Produtos alimentícios'),(2,'Bebidas','Bebidas em geral'),(3,'Higiene','Produtos de higiene pessoal');
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `cpf` varchar(14) DEFAULT NULL,
  `telefone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `endereco` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `cpf` (`cpf`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entradas_xml`
--

DROP TABLE IF EXISTS `entradas_xml`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `entradas_xml` (
  `id_entrada_xml` int NOT NULL AUTO_INCREMENT,
  `id_fornecedor` int DEFAULT NULL,
  `id_usuario` int NOT NULL,
  `numero_nota` varchar(50) DEFAULT NULL,
  `chave_nfe` varchar(44) DEFAULT NULL,
  `data_emissao` date DEFAULT NULL,
  `data_importacao` datetime DEFAULT CURRENT_TIMESTAMP,
  `valor_total` decimal(10,2) DEFAULT NULL,
  `arquivo_xml` longtext,
  PRIMARY KEY (`id_entrada_xml`),
  UNIQUE KEY `chave_nfe` (`chave_nfe`),
  KEY `id_fornecedor` (`id_fornecedor`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `entradas_xml_ibfk_1` FOREIGN KEY (`id_fornecedor`) REFERENCES `fornecedores` (`id_fornecedor`),
  CONSTRAINT `entradas_xml_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entradas_xml`
--

LOCK TABLES `entradas_xml` WRITE;
/*!40000 ALTER TABLE `entradas_xml` DISABLE KEYS */;
/*!40000 ALTER TABLE `entradas_xml` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estoque`
--

DROP TABLE IF EXISTS `estoque`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estoque` (
  `id_estoque` int NOT NULL AUTO_INCREMENT,
  `id_produto` int NOT NULL,
  `quantidade` int NOT NULL DEFAULT '0',
  `data_atualizacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_estoque`),
  UNIQUE KEY `id_produto` (`id_produto`),
  CONSTRAINT `estoque_ibfk_1` FOREIGN KEY (`id_produto`) REFERENCES `produtos` (`id_produto`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estoque`
--

LOCK TABLES `estoque` WRITE;
/*!40000 ALTER TABLE `estoque` DISABLE KEYS */;
INSERT INTO `estoque` VALUES (1,14,8,'2026-10-29 20:06:30'),(2,15,18,'2026-10-29 20:06:30'),(3,16,40,'2026-10-29 20:06:30'),(4,17,40,'2026-10-29 20:06:30'),(5,18,18,'2026-10-29 20:06:30'),(6,19,40,'2026-10-29 20:06:30'),(7,20,5,'2026-10-29 20:06:30'),(8,21,8,'2026-10-29 20:06:30'),(9,22,40,'2026-10-29 20:06:30'),(10,23,40,'2026-10-29 20:06:30'),(11,24,18,'2026-10-29 20:06:30'),(12,25,40,'2026-10-29 20:06:30'),(13,26,40,'2026-10-29 20:06:30'),(14,27,18,'2026-10-29 20:06:30'),(15,28,8,'2026-10-29 20:06:30'),(16,29,40,'2026-10-29 20:06:30'),(17,30,5,'2026-10-29 20:06:30'),(18,31,40,'2026-10-29 20:06:30'),(19,32,40,'2026-10-29 20:06:30'),(20,33,18,'2026-10-29 20:06:30'),(21,34,40,'2026-10-29 20:06:30'),(22,35,8,'2026-10-29 20:06:30'),(23,36,18,'2026-10-29 20:06:30'),(24,37,40,'2026-10-29 20:06:30'),(25,38,40,'2026-10-29 20:06:30'),(26,39,18,'2026-10-29 20:06:30'),(27,40,5,'2026-10-29 20:06:30'),(28,41,40,'2026-10-29 20:06:30'),(29,42,8,'2026-10-29 20:06:30'),(30,43,40,'2026-10-29 20:06:30'),(31,44,40,'2026-10-29 20:06:30'),(32,45,18,'2026-10-29 20:06:30'),(33,46,40,'2026-10-29 20:06:30'),(34,47,40,'2026-10-29 20:06:30'),(35,48,18,'2026-10-29 20:06:30'),(36,49,8,'2026-10-29 20:06:30'),(37,50,5,'2026-10-29 20:06:30'),(38,51,18,'2026-10-29 20:06:30'),(39,52,40,'2026-10-29 20:06:30'),(40,53,40,'2026-10-29 20:06:30'),(41,54,18,'2026-10-29 20:06:30'),(42,55,40,'2026-10-29 20:06:30'),(43,56,8,'2026-10-29 20:06:30'),(44,57,18,'2026-10-29 20:06:30'),(45,58,40,'2026-10-29 20:06:30'),(46,59,40,'2026-10-29 20:06:30'),(47,60,5,'2026-10-29 20:06:30'),(48,61,40,'2026-10-29 20:06:30'),(49,62,40,'2026-10-29 20:06:30'),(50,63,8,'2026-10-29 20:06:30');
/*!40000 ALTER TABLE `estoque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `formas_pagamento`
--

DROP TABLE IF EXISTS `formas_pagamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `formas_pagamento` (
  `id_forma_pagamento` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  `ativo` tinyint(1) DEFAULT '1',
  `taxa_percentual` decimal(5,2) DEFAULT '0.00',
  PRIMARY KEY (`id_forma_pagamento`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `formas_pagamento`
--

LOCK TABLES `formas_pagamento` WRITE;
/*!40000 ALTER TABLE `formas_pagamento` DISABLE KEYS */;
/*!40000 ALTER TABLE `formas_pagamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fornecedores`
--

DROP TABLE IF EXISTS `fornecedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fornecedores` (
  `id_fornecedor` int NOT NULL AUTO_INCREMENT,
  `razao_social` varchar(150) NOT NULL,
  `nome_fantasia` varchar(150) DEFAULT NULL,
  `cnpj` varchar(18) DEFAULT NULL,
  `telefone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `endereco` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id_fornecedor`),
  UNIQUE KEY `cnpj` (`cnpj`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fornecedores`
--

LOCK TABLES `fornecedores` WRITE;
/*!40000 ALTER TABLE `fornecedores` DISABLE KEYS */;
/*!40000 ALTER TABLE `fornecedores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `itens_venda`
--

DROP TABLE IF EXISTS `itens_venda`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `itens_venda` (
  `id_item` int NOT NULL AUTO_INCREMENT,
  `id_venda` int NOT NULL,
  `id_produto` int NOT NULL,
  `quantidade` int NOT NULL,
  `preco_unitario` decimal(10,2) NOT NULL,
  `desconto` decimal(10,2) DEFAULT '0.00',
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_item`),
  KEY `id_venda` (`id_venda`),
  KEY `id_produto` (`id_produto`),
  CONSTRAINT `itens_venda_ibfk_1` FOREIGN KEY (`id_venda`) REFERENCES `vendas` (`id_venda`),
  CONSTRAINT `itens_venda_ibfk_2` FOREIGN KEY (`id_produto`) REFERENCES `produtos` (`id_produto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `itens_venda`
--

LOCK TABLES `itens_venda` WRITE;
/*!40000 ALTER TABLE `itens_venda` DISABLE KEYS */;
/*!40000 ALTER TABLE `itens_venda` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimentacoes_estoque`
--

DROP TABLE IF EXISTS `movimentacoes_estoque`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimentacoes_estoque` (
  `id_movimentacao` int NOT NULL AUTO_INCREMENT,
  `id_produto` int NOT NULL,
  `id_usuario` int NOT NULL,
  `tipo` enum('ENTRADA','SAIDA','AJUSTE') NOT NULL,
  `quantidade` int NOT NULL,
  `motivo` varchar(255) DEFAULT NULL,
  `data_movimentacao` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_movimentacao`),
  KEY `id_produto` (`id_produto`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `movimentacoes_estoque_ibfk_1` FOREIGN KEY (`id_produto`) REFERENCES `produtos` (`id_produto`),
  CONSTRAINT `movimentacoes_estoque_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimentacoes_estoque`
--

LOCK TABLES `movimentacoes_estoque` WRITE;
/*!40000 ALTER TABLE `movimentacoes_estoque` DISABLE KEYS */;
/*!40000 ALTER TABLE `movimentacoes_estoque` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pagamentos`
--

DROP TABLE IF EXISTS `pagamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagamentos` (
  `id_pagamento` int NOT NULL AUTO_INCREMENT,
  `id_venda` int NOT NULL,
  `id_caixa` int NOT NULL,
  `id_forma_pagamento` int NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `data_pagamento` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_pagamento`),
  KEY `id_venda` (`id_venda`),
  KEY `id_caixa` (`id_caixa`),
  KEY `id_forma_pagamento` (`id_forma_pagamento`),
  CONSTRAINT `pagamentos_ibfk_1` FOREIGN KEY (`id_venda`) REFERENCES `vendas` (`id_venda`),
  CONSTRAINT `pagamentos_ibfk_2` FOREIGN KEY (`id_caixa`) REFERENCES `caixas` (`id_abertura`),
  CONSTRAINT `pagamentos_ibfk_3` FOREIGN KEY (`id_forma_pagamento`) REFERENCES `formas_pagamento` (`id_forma_pagamento`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pagamentos`
--

LOCK TABLES `pagamentos` WRITE;
/*!40000 ALTER TABLE `pagamentos` DISABLE KEYS */;
/*!40000 ALTER TABLE `pagamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `perdas`
--

DROP TABLE IF EXISTS `perdas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perdas` (
  `id_perda` int NOT NULL AUTO_INCREMENT,
  `id_produto` int NOT NULL,
  `id_usuario` int NOT NULL,
  `tipo` enum('VENCIDO','DANIFICADO','FURTO','ERRO_ESTOQUE') NOT NULL,
  `quantidade` int NOT NULL,
  `motivo` varchar(255) NOT NULL,
  `data_perda` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_perda`),
  KEY `id_produto` (`id_produto`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `perdas_ibfk_1` FOREIGN KEY (`id_produto`) REFERENCES `produtos` (`id_produto`),
  CONSTRAINT `perdas_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `perdas`
--

LOCK TABLES `perdas` WRITE;
/*!40000 ALTER TABLE `perdas` DISABLE KEYS */;
/*!40000 ALTER TABLE `perdas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissoes`
--

DROP TABLE IF EXISTS `permissoes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissoes` (
  `id_permissao` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  PRIMARY KEY (`id_permissao`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissoes`
--

LOCK TABLES `permissoes` WRITE;
/*!40000 ALTER TABLE `permissoes` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissoes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produtos`
--

DROP TABLE IF EXISTS `produtos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `produtos` (
  `id_produto` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(150) NOT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `codigo_barras` varchar(50) DEFAULT NULL,
  `preco_custo` decimal(10,2) NOT NULL,
  `preco_venda` decimal(10,2) NOT NULL,
  `estoque_minimo` int NOT NULL DEFAULT '0',
  `ativo` tinyint(1) NOT NULL DEFAULT '1',
  `id_categoria` int DEFAULT NULL,
  PRIMARY KEY (`id_produto`),
  UNIQUE KEY `codigo_barras` (`codigo_barras`),
  KEY `fk_produto_categoria` (`id_categoria`),
  CONSTRAINT `fk_produto_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos`
--

LOCK TABLES `produtos` WRITE;
/*!40000 ALTER TABLE `produtos` DISABLE KEYS */;
INSERT INTO `produtos` VALUES (1,'Arroz Branco 5kg','Arroz branco tipo 1','7891000000001',25.90,32.90,10,1,1),(2,'Feijão Carioca 1kg','Feijão carioca tipo 1','7891000000002',7.50,10.90,15,1,1),(3,'Macarrão Espaguete 500g','Macarrão de trigo','7891000000003',3.50,5.99,20,1,1),(4,'Açúcar Cristal 1kg','Açúcar cristal refinado','7891000000004',3.20,4.99,15,1,1),(5,'Óleo de Soja 900ml','Óleo de soja vegetal','7891000000005',5.20,7.49,10,1,1),(6,'Café Torrado 500g','Café torrado e moído','7891000000006',12.50,18.90,10,1,1),(7,'Leite Integral 1L','Leite integral UHT','7891000000007',4.20,6.49,20,1,1),(8,'Refrigerante Cola 2L','Refrigerante sabor cola','7891000000008',6.50,9.99,15,1,2),(9,'Suco de Laranja 1L','Suco de laranja integral','7891000000009',5.50,8.99,10,1,2),(10,'Água Mineral 500ml','Água mineral sem gás','7891000000010',1.00,2.49,30,1,2),(11,'Sabonete 90g','Sabonete perfumado','7891000000011',1.80,3.49,20,1,3),(12,'Shampoo 350ml','Shampoo para cabelos','7891000000012',8.50,14.90,10,1,3),(13,'Pasta de Dente 90g','Creme dental com flúor','7891000000013',4.50,7.99,15,1,3),(14,'Arroz Branco 5kg','Arroz tipo 1 pacote 5kg','TESTE000014',18.50,25.90,10,1,NULL),(15,'Feijao Carioca 1kg','Feijao carioca pacote 1kg','TESTE000015',5.80,8.99,15,1,NULL),(16,'Acucar Refinado 1kg','Acucar refinado pacote 1kg','TESTE000016',3.20,4.99,15,1,NULL),(17,'Cafe Torrado 500g','Cafe moido pacote 500g','TESTE000017',16.90,23.90,12,1,NULL),(18,'Macarrao Espaguete 500g','Massa espaguete','TESTE000018',2.50,4.49,20,1,NULL),(19,'Oleo de Soja 900ml','Oleo vegetal de soja','TESTE000019',5.40,7.99,15,1,NULL),(20,'Sal Refinado 1kg','Sal iodado','TESTE000020',1.50,2.99,15,1,NULL),(21,'Farinha de Trigo 1kg','Farinha tradicional','TESTE000021',3.20,5.49,12,1,NULL),(22,'Farinha de Mandioca 500g','Farinha torrada','TESTE000022',3.80,6.49,10,1,NULL),(23,'Molho de Tomate 300g','Molho tradicional','TESTE000023',1.80,3.49,20,1,NULL),(24,'Leite Integral 1L','Leite longa vida integral','TESTE000024',3.90,5.99,25,1,NULL),(25,'Leite Desnatado 1L','Leite longa vida desnatado','TESTE000025',4.10,6.29,12,1,NULL),(26,'Iogurte Natural 170g','Iogurte natural','TESTE000026',2.10,3.99,10,1,NULL),(27,'Queijo Mussarela 200g','Queijo fatiado','TESTE000027',8.90,13.99,10,1,NULL),(28,'Presunto Cozido 200g','Presunto fatiado','TESTE000028',6.90,10.99,10,1,NULL),(29,'Manteiga 200g','Manteiga com sal','TESTE000029',8.50,12.99,10,1,NULL),(30,'Margarina 500g','Margarina cremosa','TESTE000030',4.20,7.49,12,1,NULL),(31,'Requeijao 200g','Requeijao cremoso','TESTE000031',5.90,9.49,10,1,NULL),(32,'Creme de Leite 200g','Creme de leite em caixa','TESTE000032',2.40,4.29,15,1,NULL),(33,'Leite Condensado 395g','Leite condensado em caixa','TESTE000033',4.20,6.99,15,1,NULL),(34,'Refrigerante Cola 2L','Refrigerante sabor cola','TESTE000034',5.50,9.99,20,1,NULL),(35,'Refrigerante Guarana 2L','Refrigerante sabor guarana','TESTE000035',4.90,8.99,20,1,NULL),(36,'Suco de Uva 1L','Suco integral de uva','TESTE000036',7.50,12.99,10,1,NULL),(37,'Suco de Laranja 1L','Suco de laranja pronto','TESTE000037',5.20,8.99,10,1,NULL),(38,'Agua Mineral 500ml','Agua mineral sem gas','TESTE000038',1.00,2.49,30,1,NULL),(39,'Agua Mineral 1.5L','Agua mineral sem gas','TESTE000039',1.80,3.99,20,1,NULL),(40,'Agua com Gas 500ml','Agua mineral gaseificada','TESTE000040',1.40,2.99,15,1,NULL),(41,'Cha Gelado 1L','Cha pronto para beber','TESTE000041',4.10,6.99,10,1,NULL),(42,'Biscoito Recheado 140g','Biscoito sabor chocolate','TESTE000042',2.00,3.99,20,1,NULL),(43,'Bolacha Agua e Sal 200g','Biscoito salgado','TESTE000043',2.80,4.99,15,1,NULL),(44,'Chocolate ao Leite 90g','Barra de chocolate','TESTE000044',4.50,7.99,15,1,NULL),(45,'Bala de Goma 100g','Balas sortidas','TESTE000045',2.20,4.49,15,1,NULL),(46,'Salgadinho 100g','Salgadinho sabor queijo','TESTE000046',3.50,6.99,15,1,NULL),(47,'Pipoca Microondas 100g','Pipoca sabor manteiga','TESTE000047',2.80,5.49,12,1,NULL),(48,'Pao de Forma 500g','Pao de forma tradicional','TESTE000048',5.20,8.99,10,1,NULL),(49,'Aveia em Flocos 200g','Aveia integral','TESTE000049',3.10,5.99,10,1,NULL),(50,'Sabonete 90g','Sabonete perfumado','TESTE000050',1.50,3.49,20,1,NULL),(51,'Shampoo 350ml','Shampoo uso diario','TESTE000051',8.90,15.99,10,1,NULL),(52,'Condicionador 350ml','Condicionador hidratante','TESTE000052',9.20,16.99,10,1,NULL),(53,'Creme Dental 90g','Creme dental com fluor','TESTE000053',3.20,6.49,15,1,NULL),(54,'Escova Dental','Escova dental macia','TESTE000054',3.00,5.99,15,1,NULL),(55,'Papel Higienico 12 Rolos','Papel folha dupla','TESTE000055',11.90,19.99,12,1,NULL),(56,'Desodorante Aerosol 150ml','Desodorante antitranspirante','TESTE000056',7.90,13.99,10,1,NULL),(57,'Detergente Neutro 500ml','Detergente liquido','TESTE000057',1.60,2.99,20,1,NULL),(58,'Sabao em Po 800g','Sabao para roupas','TESTE000058',7.50,12.99,12,1,NULL),(59,'Amaciante 2L','Amaciante concentrado','TESTE000059',8.90,15.99,10,1,NULL),(60,'Agua Sanitaria 1L','Alvejante clorado','TESTE000060',2.50,4.99,15,1,NULL),(61,'Desinfetante 2L','Desinfetante floral','TESTE000061',5.90,9.99,12,1,NULL),(62,'Esponja de Limpeza','Esponja dupla face','TESTE000062',1.20,2.99,20,1,NULL),(63,'Saco de Lixo 30L','Pacote com 20 unidades','TESTE000063',5.20,8.99,12,1,NULL);
/*!40000 ALTER TABLE `produtos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promocao_produtos`
--

DROP TABLE IF EXISTS `promocao_produtos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promocao_produtos` (
  `id_promocao` int NOT NULL,
  `id_produto` int NOT NULL,
  PRIMARY KEY (`id_promocao`,`id_produto`),
  KEY `id_produto` (`id_produto`),
  CONSTRAINT `promocao_produtos_ibfk_1` FOREIGN KEY (`id_promocao`) REFERENCES `promocoes` (`id_promocao`),
  CONSTRAINT `promocao_produtos_ibfk_2` FOREIGN KEY (`id_produto`) REFERENCES `produtos` (`id_produto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promocao_produtos`
--

LOCK TABLES `promocao_produtos` WRITE;
/*!40000 ALTER TABLE `promocao_produtos` DISABLE KEYS */;
/*!40000 ALTER TABLE `promocao_produtos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promocoes`
--

DROP TABLE IF EXISTS `promocoes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promocoes` (
  `id_promocao` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `descricao` varchar(255) DEFAULT NULL,
  `tipo` enum('PERCENTUAL','VALOR_FIXO') NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `data_inicio` date NOT NULL,
  `data_fim` date NOT NULL,
  `ativa` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_promocao`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promocoes`
--

LOCK TABLES `promocoes` WRITE;
/*!40000 ALTER TABLE `promocoes` DISABLE KEYS */;
/*!40000 ALTER TABLE `promocoes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nome` varchar(100) NOT NULL,
  `telefone` varchar(20) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `endereco` varchar(255) DEFAULT NULL,
  `senha` varchar(255) NOT NULL,
  `id_cargo` int NOT NULL,
  `ativo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `email` (`email`),
  KEY `fk_usuario_cargo` (`id_cargo`),
  CONSTRAINT `fk_usuario_cargo` FOREIGN KEY (`id_cargo`) REFERENCES `cargos` (`id_cargo`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'João Silva','(12) 99999-1111','admin@costock.com','Rua Principal, 100','123456',1,1),(2,'Carlos Oliveira','(12) 99999-2222','caixa@costock.com','Rua das Flores, 200','123456',2,1),(3,'Mariana Oliveira','(12) 98811-1001','mariana@costock.exemplo','Caraguatatuba - SP','LOGIN_PENDENTE',1,1),(4,'Ricardo Almeida','(12) 98811-1002','ricardo@costock.exemplo','Caraguatatuba - SP','LOGIN_PENDENTE',1,1),(5,'Fernanda Costa','(12) 98811-1003','fernanda@costock.exemplo','Caraguatatuba - SP','LOGIN_PENDENTE',3,1),(6,'Carlos Mendes','(12) 98811-1004','carlos@costock.exemplo','Caraguatatuba - SP','LOGIN_PENDENTE',3,1),(7,'Juliana Ferreira','(12) 98811-1005','juliana@costock.exemplo','Caraguatatuba - SP','LOGIN_PENDENTE',4,1),(8,'Lucas Ribeiro','(12) 98811-1006','lucas@costock.exemplo','Caraguatatuba - SP','LOGIN_PENDENTE',4,1),(9,'Beatriz Martins','(12) 98811-1007','beatriz@costock.exemplo','Caraguatatuba - SP','LOGIN_PENDENTE',4,1),(10,'Gabriel Santos','(12) 98811-1008','gabriel@costock.exemplo','Caraguatatuba - SP','LOGIN_PENDENTE',4,1),(11,'Camila Rodrigues','(12) 98811-1009','camila@costock.exemplo','Caraguatatuba - SP','LOGIN_PENDENTE',4,1),(12,'Pedro Henrique','(12) 98811-1010','pedro@costock.exemplo','Caraguatatuba - SP','LOGIN_PENDENTE',4,1);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendas`
--

DROP TABLE IF EXISTS `vendas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendas` (
  `id_venda` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int DEFAULT NULL,
  `id_usuario` int NOT NULL,
  `data_venda` datetime DEFAULT CURRENT_TIMESTAMP,
  `valor_total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` enum('ABERTA','FINALIZADA','CANCELADA') DEFAULT 'ABERTA',
  `numero_comprovante` varchar(50) DEFAULT NULL,
  `motivo_cancelamento` varchar(255) DEFAULT NULL,
  `id_usuario_cancelamento` int DEFAULT NULL,
  PRIMARY KEY (`id_venda`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_usuario` (`id_usuario`),
  KEY `id_usuario_cancelamento` (`id_usuario_cancelamento`),
  CONSTRAINT `vendas_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `vendas_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `vendas_ibfk_3` FOREIGN KEY (`id_usuario_cancelamento`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendas`
--

LOCK TABLES `vendas` WRITE;
/*!40000 ALTER TABLE `vendas` DISABLE KEYS */;
/*!40000 ALTER TABLE `vendas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-29 20:09:41
