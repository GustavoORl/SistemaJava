package model;

import java.util.Set;

public class Cargo {
    int id;
    String nome;
    private Set<Permissao> permissoes;


    public int getId() {
        return id;
    }
    public void setIdCargo(int id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public Set<Permissao> getPermissoes() {
        return permissoes;
    }
    public void setPermissoes(Set<Permissao> permissoes) {
        this.permissoes = permissoes;
    }

    
}
