import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    // Configurações padrão do MySQL no XAMPP
    private static final String URL = "jdbc:mysql://localhost:3306/agencia_viagens?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // No XAMPP a senha padrão é vazia

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void testarConexao() {
        try (Connection conn = conectar()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("\n[SUCESSO] Conexão com o banco 'agencia_viagens' estabelecida com êxito!");
            }
        } catch (SQLException e) {
            System.out.println("\n[ERRO DE CONEXÃO] Não foi possível conectar ao MySQL. Verifique se o XAMPP está ligado.");
            System.out.println("Detalhes técnicos: " + e.getMessage());
        }
    }
}