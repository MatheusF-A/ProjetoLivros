/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package livros;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Math
 */

public class LivroDAO {

    // Método para listar todos os livros
    public List<livro> listarLivros() throws SQLException {
        List<livro> livros = new ArrayList<>();
        String query = "SELECT * FROM livro";

        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                livro livro = new livro();
                livro.setId(rs.getInt("id"));
                livro.setTitulo(rs.getString("titulo"));
                livro.setAnoPublicacao(rs.getInt("ano_publicacao"));
                livro.setSinopse(rs.getString("sinopse"));

                livro.setAutor(buscarAutorPorId(rs.getInt("autor_id")));
                livro.setGenero(buscarGeneroPorId(rs.getInt("genero_id")));
                livro.setEditora(buscarEditoraPorId(rs.getInt("editora_id")));
                livro.setIdioma(buscarIdiomaPorId(rs.getInt("idioma_id")));

                livros.add(livro);
            }
        }
        return livros;
    }

     public int salvarLivro(String titulo, int idEditora, int idGenero, int idIdioma, int anoPublicacao, String sinopse) {
    String sql = "INSERT INTO livro (titulo, editora_id, genero_id, idioma_id, ano_publicacao, sinopse) VALUES (?, ?, ?, ?, ?, ?)";
    int idLivro = -1;

    try (Connection conn = Conexao.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

        stmt.setString(1, titulo);
        stmt.setInt(2, idEditora);
        stmt.setInt(3, idGenero);
        stmt.setInt(4, idIdioma);
        stmt.setInt(5, anoPublicacao);
        stmt.setString(6, sinopse);

        stmt.executeUpdate();

        // Recupera o ID do livro recém-criado
        try (ResultSet rs = stmt.getGeneratedKeys()) {
            if (rs.next()) {
                idLivro = rs.getInt(1);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return idLivro; // Retorna o ID do livro para associar com os autores
}

     
    public void associarLivroAutor(int idLivro, int idAutor) {
    String sql = "INSERT INTO livro_autor (id_livro, id_autor) VALUES (?, ?)";

    try (Connection conn = Conexao.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, idLivro);
        stmt.setInt(2, idAutor);

        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    // Métodos auxiliares para buscar detalhes (Autor, Gênero, Editora, Idioma) por ID
    private Autor buscarAutorPorId(int autorId) throws SQLException {
        String query = "SELECT * FROM autor WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, autorId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Autor(rs.getInt("id"), rs.getString("nome"));
            }
        }
        return null;
    }

    private Genero buscarGeneroPorId(int generoId) throws SQLException {
        String query = "SELECT * FROM genero WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, generoId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Genero(rs.getInt("id"), rs.getString("nome"));
            }
        }
        return null;
    }

    private Editora buscarEditoraPorId(int editoraId) throws SQLException {
        String query = "SELECT * FROM editora WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, editoraId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Editora(rs.getInt("id"), rs.getString("nome"));
            }
        }
        return null;
    }

    private Idioma buscarIdiomaPorId(int idiomaId) throws SQLException {
        String query = "SELECT * FROM idioma WHERE id = ?";
        try (Connection conn = Conexao.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, idiomaId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Idioma(rs.getInt("id"), rs.getString("nome"));
            }
        }
        return null;
    }
}

