/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package livros;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Math
 */
public class Idioma {
    
    private int id;
    private String nome;

    public Idioma(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    // Getters e Setters

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public static void salvarIdioma(String nome){
        String sql = "INSERT INTO idioma (nome) VALUES (?)";

    try (Connection conn = Conexao.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, nome);  

        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

}
