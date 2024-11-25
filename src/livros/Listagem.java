
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package livros;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel; 
import javax.swing.JOptionPane;
/**
 *
 * @author Math
 */
public class Listagem extends javax.swing.JFrame {

    /**
     * Creates new form Listagem
     */
    public Listagem() {
        initComponents();
        setLocationRelativeTo(null);
        tblLivros.setModel(carregarTabelaLivros());
    }

    public DefaultTableModel carregarTabelaLivros() {
    // Colunas da tabela
    String[] colunas = {"Título", "Gênero", "Editora", "Idioma", "Ano de Publicação", "Autores"};
    DefaultTableModel modelo = new DefaultTableModel(colunas, 0);

    // Consulta SQL
    String sql = 
        "SELECT " +
        "    livro.titulo AS Título, " +
        "    genero.nome AS Gênero, " +
        "    editora.nome AS Editora, " +
        "    idioma.nome AS Idioma, " +
        "    livro.ano_publicacao AS `Ano de Publicação`, " +
        "    GROUP_CONCAT(autor.nome SEPARATOR ', ') AS Autores " +
        "FROM " +
        "    livro " +
        "LEFT JOIN livro_autor ON livro.id = livro_autor.id_livro " +
        "LEFT JOIN autor ON livro_autor.id_autor = autor.id " +
        "LEFT JOIN genero ON livro.genero_id = genero.id " +
        "LEFT JOIN editora ON livro.editora_id = editora.id " +
        "LEFT JOIN idioma ON livro.idioma_id = idioma.id " +
        "GROUP BY " +
        "    livro.titulo, " +
        "    genero.nome, " +
        "    editora.nome, " +
        "    idioma.nome, " +
        "    livro.ano_publicacao ";


    // Conexão com o banco de dados
    try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

        // Percorre os resultados e adiciona ao modelo
        while (rs.next()) {
            String titulo = rs.getString("Título");
            String genero = rs.getString("Gênero");
            String editora = rs.getString("Editora");
            String idioma = rs.getString("Idioma");
            int anoPublicacao = rs.getInt("Ano de Publicação");
            String autores = rs.getString("Autores");

            // Adiciona a linha na tabela
            modelo.addRow(new Object[]{titulo, genero, editora, idioma, anoPublicacao, autores});
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao carregar dados da tabela livros: " + e.getMessage());
    }

    return modelo;
}
    
    private void atualizarTabelaPorPesquisa(String coluna, String valor) {
    // Colunas da tabela
    String[] colunas = {"Título", "Gênero", "Editora", "Idioma", "Ano de Publicação", "Autor"};
    DefaultTableModel modelo = new DefaultTableModel(colunas, 0);

    // Consulta SQL com filtro
    String sql = 
        "SELECT " +
        
        "    livro.titulo AS Título, " +
        "    genero.nome AS Gênero, " +
        "    editora.nome AS Editora, " +
        "    idioma.nome AS Idioma, " +
        "    livro.ano_publicacao AS `Ano de Publicação`, " +
       
        "    GROUP_CONCAT(autor.nome SEPARATOR ', ') AS Autores " +
        "FROM " +
        "    livro " +
        "LEFT JOIN livro_autor ON livro.id = livro_autor.id_livro " +
        "LEFT JOIN autor ON livro_autor.id_autor = autor.id " +
        "LEFT JOIN genero ON livro.genero_id = genero.id " +
        "LEFT JOIN editora ON livro.editora_id = editora.id " +
        "LEFT JOIN idioma ON livro.idioma_id = idioma.id " +
        "WHERE " + coluna + " LIKE ? " + // Filtro pela coluna
        "GROUP BY " +
        
        "    livro.titulo, " +
        "    genero.nome, " +
        "    editora.nome, " +
        "    idioma.nome, " +
        "    livro.ano_publicacao;";


    try (Connection conn = Conexao.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, "%" + valor + "%"); // Substitui o "?" na consulta
        ResultSet rs = stmt.executeQuery();

        // Preenche a tabela com os resultados
        while (rs.next()) {
            
            String titulo = rs.getString("Título");
            String genero = rs.getString("Gênero");
            String editora = rs.getString("Editora");
            String idioma = rs.getString("Idioma");
            int anoPublicacao = rs.getInt("Ano de Publicação");
            String autores = rs.getString("Autores");

            modelo.addRow(new Object[]{titulo, genero, editora, idioma, anoPublicacao, autores});
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao pesquisar dados: " + e.getMessage());
    }

    tblLivros.setModel(modelo); // Atualiza o modelo da tabela
}
    
    public void excluirLivroSelecionado() {
    int linhaSelecionada = tblLivros.getSelectedRow();

    if (linhaSelecionada != -1) {
        // Pega o nome do livro da primeira coluna (onde o título está)
        String nomeLivro = tblLivros.getValueAt(linhaSelecionada, 0).toString();

        int resposta = JOptionPane.showConfirmDialog(null, 
            "Tem certeza que deseja excluir o livro \"" + nomeLivro + "\"?", 
            "Confirmar Exclusão", 
            JOptionPane.YES_NO_OPTION);

        if (resposta == JOptionPane.YES_OPTION) {
            excluirLivro(nomeLivro);
        }
    } else {
        JOptionPane.showMessageDialog(null, "Selecione um livro para excluir.");
    }
}

    public void excluirLivro(String nomeLivro) {
    
    String sqlRelacao = "DELETE FROM livro_autor WHERE id_livro = (SELECT id FROM livro WHERE titulo = ?)";
    
    String sqlLivro = "DELETE FROM livro WHERE titulo = ?";

    try (Connection conn = Conexao.getConnection();
         PreparedStatement stmtRelacao = conn.prepareStatement(sqlRelacao);
         PreparedStatement stmtLivro = conn.prepareStatement(sqlLivro)) {

        //Apaga a relação livro-autor
        stmtRelacao.setString(1, nomeLivro);
        stmtRelacao.executeUpdate();

        //Apaga o Livro
        stmtLivro.setString(1, nomeLivro);
        stmtLivro.executeUpdate();

        JOptionPane.showMessageDialog(null, "Livro excluído com sucesso!");
        
        tblLivros.setModel(carregarTabelaLivros());

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Erro ao excluir livro: " + e.getMessage());
    }
}

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLivros = new javax.swing.JTable();
        txtPesqAutor = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtPesqGenero = new javax.swing.JTextField();
        txtPesqIdioma = new javax.swing.JTextField();
        txtPesqEditora = new javax.swing.JTextField();
        btnPeditora = new javax.swing.JButton();
        btnPidioma = new javax.swing.JButton();
        btnPgenero = new javax.swing.JButton();
        btnPautor = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        jScrollPane2.setViewportView(jEditorPane1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Listagem de Livros");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(204, 204, 0));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setText("Listagem");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addContainerGap(931, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                .addContainerGap())
        );

        tblLivros.setAutoCreateRowSorter(true);
        tblLivros.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tblLivros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblLivros.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblLivros);
        if (tblLivros.getColumnModel().getColumnCount() > 0) {
            tblLivros.getColumnModel().getColumn(0).setMinWidth(300);
            tblLivros.getColumnModel().getColumn(1).setMinWidth(150);
            tblLivros.getColumnModel().getColumn(2).setPreferredWidth(200);
            tblLivros.getColumnModel().getColumn(3).setPreferredWidth(50);
        }

        txtPesqAutor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesqAutorActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("PESQUISAR POR:");

        jLabel2.setText("Autor:");

        jLabel3.setText("Gênero: ");

        jLabel4.setText("Idioma: ");

        jLabel5.setText("Editora:");

        txtPesqEditora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPesqEditoraActionPerformed(evt);
            }
        });

        btnPeditora.setText("Pesquisar por Editora");
        btnPeditora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPeditoraActionPerformed(evt);
            }
        });

        btnPidioma.setText("Pesquisar por Idioma");
        btnPidioma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPidiomaActionPerformed(evt);
            }
        });

        btnPgenero.setText("Pesquisar por Gênero");
        btnPgenero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPgeneroActionPerformed(evt);
            }
        });

        btnPautor.setText("Pesquisar por Autor");
        btnPautor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPautorActionPerformed(evt);
            }
        });

        btnExcluir.setBackground(new java.awt.Color(255, 51, 51));
        btnExcluir.setForeground(new java.awt.Color(255, 255, 255));
        btnExcluir.setText("Excluir Livro Selecionado");
        btnExcluir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcluirActionPerformed(evt);
            }
        });

        jMenu1.setText("Ir Para");
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });

        jMenuItem7.setText("Pagina Inicial");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenuItem8.setText("Listagem");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem8);

        jMenuBar1.add(jMenu1);

        jMenu3.setText("Cadastros");

        jMenuItem2.setText("Livro");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2);

        jMenuItem6.setText("Autores");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem6);

        jMenuItem3.setText("Gênero");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem3);

        jMenuItem5.setText("Editora");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuItem4.setText("Idioma");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem4);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel2))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtPesqAutor, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnPautor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(txtPesqGenero, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnPgenero)))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtPesqIdioma, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnPidioma, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtPesqEditora, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnPeditora)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExcluir)
                        .addGap(28, 28, 28))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPesqAutor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(btnPautor))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtPesqGenero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPgenero))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtPesqIdioma, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPidioma))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtPesqEditora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPeditora)
                    .addComponent(btnExcluir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        Menu.irHome(this);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        Menu.irListagem(this);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed

    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        Menu.irCadLivro(this);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        Menu.irCadAutor(this);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        Menu.irCadGenero(this);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        Menu.irCadEditora(this);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        Menu.irCadIdioma(this);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void txtPesqAutorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesqAutorActionPerformed
        
    }//GEN-LAST:event_txtPesqAutorActionPerformed

    private void txtPesqEditoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPesqEditoraActionPerformed

    }//GEN-LAST:event_txtPesqEditoraActionPerformed

    private void btnPautorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPautorActionPerformed
    String autor = txtPesqAutor.getText();
    atualizarTabelaPorPesquisa("autor.nome", autor);
    txtPesqAutor.setText("");
    }//GEN-LAST:event_btnPautorActionPerformed

    private void btnPidiomaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPidiomaActionPerformed
    String idioma = txtPesqIdioma.getText();
    atualizarTabelaPorPesquisa("idioma.nome", idioma);
txtPesqIdioma.setText("");    // TODO add your handling code here:
    }//GEN-LAST:event_btnPidiomaActionPerformed

    private void btnPgeneroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPgeneroActionPerformed
    String genero = txtPesqGenero.getText();
    atualizarTabelaPorPesquisa("genero.nome", genero);
    txtPesqGenero.setText("");
    }//GEN-LAST:event_btnPgeneroActionPerformed

    private void btnPeditoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPeditoraActionPerformed
    String editora = txtPesqEditora.getText();
    atualizarTabelaPorPesquisa("editora.nome", editora);
    txtPesqEditora.setText("");// TODO add your handling code here:
    }//GEN-LAST:event_btnPeditoraActionPerformed

    private void btnExcluirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExcluirActionPerformed
    excluirLivroSelecionado();
    }//GEN-LAST:event_btnExcluirActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Listagem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Listagem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Listagem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Listagem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Listagem().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnPautor;
    private javax.swing.JButton btnPeditora;
    private javax.swing.JButton btnPgenero;
    private javax.swing.JButton btnPidioma;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblLivros;
    private javax.swing.JTextField txtPesqAutor;
    private javax.swing.JTextField txtPesqEditora;
    private javax.swing.JTextField txtPesqGenero;
    private javax.swing.JTextField txtPesqIdioma;
    // End of variables declaration//GEN-END:variables
}
