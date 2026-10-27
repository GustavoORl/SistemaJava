package model;

import java.time.LocalDateTime;

public class Perda {

        private int idPerda;
        private Produto produto;
        private Usuario usuario;
        private String tipo;
        private int quantidade;
        private String motivo;
        private LocalDateTime dataPerda;
        
        public int getIdPerda() {
            return idPerda;
        }
        public void setIdPerda(int idPerda) {
            this.idPerda = idPerda;
        }
        public Produto getProduto() {
            return produto;
        }
        public void setProduto(Produto produto) {
            this.produto = produto;
        }
        public Usuario getUsuario() {
            return usuario;
        }
        public void setUsuario(Usuario usuario) {
            this.usuario = usuario;
        }
        public String getTipo() {
            return tipo;
        }
        public void setTipo(String tipo) {
            this.tipo = tipo;
        }
        public int getQuantidade() {
            return quantidade;
        }
        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }
        public String getMotivo() {
            return motivo;
        }
        public void setMotivo(String motivo) {
            this.motivo = motivo;
        }
        public LocalDateTime getDataPerda() {
            return dataPerda;
        }
        public void setDataPerda(LocalDateTime dataPerda) {
            this.dataPerda = dataPerda;
        }

        
}
