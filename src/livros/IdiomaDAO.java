/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package livros;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Math
 */
public class IdiomaDAO {
    public List<String> obterNomesIdiomas() {
        List<String> idiomas = new ArrayList<>();
        String sql = "SELECT nome FROM idioma";

        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String nome = rs.getString("nome");
                idiomas.add(nome);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idiomas;
    }
    
    public int obterIdIdioma(String nome) {
    String sql = "SELECT id FROM idioma WHERE nome = ?";
    int id = -1;

    try (Connection conn = Conexao.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, nome);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                id = rs.getInt("id");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return id;
}

}
