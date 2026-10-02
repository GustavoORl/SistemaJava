package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import  model.Estoque;

public class EstoqueDAO {
    /*  cadastrar(Estoque estoque)
        listar()
        buscarPorProduto(int idProduto)
        adicionarQuantidade(int idProduto, int quantidade)
        retirarQuantidade(int idProduto, int quantidade)
        
            EstoqueDAO
            movimentacao_estoqueDAO
            PerdaDAO*/

    public void cadastrar(Estoque estoque){
        String sql = "INSERT INTO estoque (id_estoque, id_produto, quantidade, data_atualização"
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            
            stmt.setInt(1, estoque.getId());
            stmt.setInt(2, estoque.getProduto().getIdProduto());
            stmt.setBigDecimal(3, estoque.getQuantidade());
            stmt.setTimestamp(4, Timestamp.valueOf(estoque.getDataAtualizacao()));
            
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
