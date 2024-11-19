/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package livros;
import javax.swing.JFrame;

/**
 *
 * @author Math
 */
public class Menu {
    
    public static void irHome(JFrame atual){
        atual.dispose();
        new Home().setVisible (true);
    }
    
    public static void irListagem(JFrame atual){
        atual.dispose();
        new Listagem().setVisible(true);
    }
    
    public static void irCadLivro(JFrame atual){
        atual.dispose();
        new frmLivro().setVisible(true);
    }
    
    public static void irCadAutor(JFrame atual){
        atual.dispose();
        new frmAutor().setVisible(true);
    }
    
    public static void irCadEditora(JFrame atual){
        atual.dispose();
        new frmEditora().setVisible(true);
    }
    
    public static void irCadGenero(JFrame atual){
        atual.dispose();
        new frmGenero().setVisible(true);
    }
    
    public static void irCadIdioma(JFrame atual){
        atual.dispose();
        new frmIdioma().setVisible(true);
    }
}
