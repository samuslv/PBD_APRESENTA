import java.sql.*;
import java.util.Scanner;

public class SistemaAgencia {

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("====================================================");
        System.out.println("   SISTEMA DE AGÊNCIA DE VIAGENS - PUC GOIÁS        ");
        System.out.println("====================================================");

        // Exigência 04: Login antes de acessar o menu
        if (!realizarLogin()) {
            System.out.println("Encerrando o sistema por falha na autenticação.");
            return;
        }

        int opcao = -1;
        while (opcao != 0) {
            exibirMenuPrincipal();
            opcao = lerInteiro("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> menuIncluir();
                case 2 -> menuAlterar();
                case 3 -> menuExcluir();
                case 4 -> menuPesquisarDoisCampos();
                case 5 -> menuPesquisarPorPK();
                case 6 -> pesquisarDuasTabelas();
                case 7 -> pesquisarTresTabelas();
                case 8 -> pesquisarQuatroTabelas();
                case 9 -> listarVendasCompletas();
                case 10 -> Conexao.testarConexao();
                case 0 -> System.out.println("\nFinalizando o sistema. Até logo!");
                default -> System.out.println("\n[AVISO] Opção inválida! Digite um número do menu.");
            }
        }
        sc.close();
    }

    // ==================== LOGIN ====================
    private static boolean realizarLogin() {
        int tentativas = 3;
        while (tentativas > 0) {
            System.out.print("\nUsuário (Login): ");
            String login = sc.nextLine();
            System.out.print("Senha: ");
            String senha = sc.nextLine();

            String sql = "SELECT * FROM USUARIO WHERE login = ? AND senha = ?";
            try (Connection conn = Conexao.conectar();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setString(1, login);
                ps.setString(2, senha);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    System.out.println("\n[LOGIN OK] Bem-vindo(a), " + login + "!");
                    return true;
                } else {
                    tentativas--;
                    System.out.println("[ERRO DE LOGIN] Usuário ou senha incorretos. Tentativas restantes: " + tentativas);
                }
            } catch (SQLException e) {
                System.out.println("[ERRO NO BANCO] Falha ao validar login. Verifique se o MySQL está ativo: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n==================== MENU PRINCIPAL ====================");
        System.out.println("01. Incluir dados (por tabela)");
        System.out.println("02. Alterar dados (por tabela)");
        System.out.println("03. Excluir dados (por tabela)");
        System.out.println("04. Pesquisar dados usando DOIS campos (por tabela)");
        System.out.println("05. Pesquisar dados só com a PK (por tabela)");
        System.out.println("06. Pesquisar dados usando 02 TABELAS (Venda + Cliente)");
        System.out.println("07. Pesquisar dados usando 03 TABELAS (Venda + Cliente + Destino)");
        System.out.println("08. Pesquisar dados usando 04 TABELAS (Relatório Completo)");
        System.out.println("09. Listar dados usando 03 TABELAS (Listagem Geral)");
        System.out.println("10. Verificar/Testar conexão com o Banco de Dados");
        System.out.println("00. Sair / Finalizar programa");
        System.out.println("========================================================");
    }

    private static int escolherTabela(String acao) {
        System.out.println("\n--- " + acao + ": Escolha a Tabela ---");
        System.out.println("1 - CLIENTE (Auxiliar)");
        System.out.println("2 - DESTINO (Auxiliar)");
        System.out.println("3 - PACOTE (Auxiliar)");
        System.out.println("4 - VENDA_VIAGEM (Principal)");
        System.out.println("0 - Voltar ao Menu Principal");
        return lerInteiro("Tabela desejada: ");
    }

    // ==================== 01. INCLUIR DADOS ====================
    private static void menuIncluir() {
        int tab = escolherTabela("INCLUIR DADOS");
        switch (tab) {
            case 1 -> incluirCliente();
            case 2 -> incluirDestino();
            case 3 -> incluirPacote();
            case 4 -> incluirVenda();
            case 0 -> {}
            default -> System.out.println("[ERRO] Tabela inválida.");
        }
    }

    private static void incluirCliente() {
        System.out.print("Nome do cliente: ");
        String nome = sc.nextLine();
        System.out.print("CPF (ex: 000.000.000-00): ");
        String cpf = sc.nextLine();
        System.out.print("Telefone: ");
        String tel = sc.nextLine();
        System.out.print("E-mail: ");
        String email = sc.nextLine();

        String sql = "INSERT INTO CLIENTE (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, cpf);
            ps.setString(3, tel);
            ps.setString(4, email);
            ps.executeUpdate();
            System.out.println("[SUCESSO] Cliente cadastrado com êxito!");
        } catch (SQLException e) {
            System.out.println("[ERRO AO INCLUIR CLIENTE] Não foi possível salvar o cliente (verifique se o CPF já existe): " + e.getMessage());
        }
    }

    private static void incluirDestino() {
        System.out.print("Cidade: ");
        String cidade = sc.nextLine();
        System.out.print("País: ");
        String pais = sc.nextLine();
        System.out.print("Descrição: ");
        String desc = sc.nextLine();

        String sql = "INSERT INTO DESTINO (cidade, pais, descricao) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cidade);
            ps.setString(2, pais);
            ps.setString(3, desc);
            ps.executeUpdate();
            System.out.println("[SUCESSO] Destino cadastrado com êxito!");
        } catch (SQLException e) {
            System.out.println("[ERRO AO INCLUIR DESTINO] Falha ao cadastrar destino: " + e.getMessage());
        }
    }

    private static void incluirPacote() {
        System.out.print("Nome do pacote: ");
        String nome = sc.nextLine();
        System.out.print("Tipo de transporte (Avião, Ônibus, etc.): ");
        String transp = sc.nextLine();
        int dias = lerInteiro("Dias de duração: ");

        String sql = "INSERT INTO PACOTE (nome_pacote, tipo_transporte, dias_duracao) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, transp);
            ps.setInt(3, dias);
            ps.executeUpdate();
            System.out.println("[SUCESSO] Pacote cadastrado com êxito!");
        } catch (SQLException e) {
            System.out.println("[ERRO AO INCLUIR PACOTE] Falha ao cadastrar pacote: " + e.getMessage());
        }
    }

    private static void incluirVenda() {
        // Regra do enunciado: listar tabelas pai antes de informar FKs
        System.out.println("\n--- Selecione o Cliente (FK) ---");
        listarClientesSimples();
        int idCliente = lerInteiro("Informe o ID (PK) do Cliente: ");

        System.out.println("\n--- Selecione o Destino (FK) ---");
        listarDestinosSimples();
        int idDestino = lerInteiro("Informe o ID (PK) do Destino: ");

        System.out.println("\n--- Selecione o Pacote (FK) ---");
        listarPacotesSimples();
        int idPacote = lerInteiro("Informe o ID (PK) do Pacote: ");

        System.out.print("Data da viagem (AAAA-MM-DD): ");
        String data = sc.nextLine();
        double valor = lerDouble("Valor total (ex: 1500.50): ");

        String sql = "INSERT INTO VENDA_VIAGEM (id_cliente, id_destino, id_pacote, data_viagem, valor_total) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idDestino);
            ps.setInt(3, idPacote);
            ps.setDate(4, Date.valueOf(data));
            ps.setDouble(5, valor);
            ps.executeUpdate();
            System.out.println("[SUCESSO] Venda de viagem registrada com êxito!");
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRO DE DATA] Formato de data inválido! Use AAAA-MM-DD (ex: 2026-12-25).");
        } catch (SQLException e) {
            System.out.println("[ERRO AO INCLUIR VENDA] Verifique se os IDs de Cliente, Destino e Pacote existem: " + e.getMessage());
        }
    }

    // ==================== 02. ALTERAR DADOS ====================
    private static void menuAlterar() {
        int tab = escolherTabela("ALTERAR DADOS");
        switch (tab) {
            case 1 -> alterarCliente();
            case 2 -> alterarDestino();
            case 3 -> alterarPacote();
            case 4 -> alterarVenda();
            case 0 -> {}
            default -> System.out.println("[ERRO] Tabela inválida.");
        }
    }

    private static void alterarCliente() {
        System.out.print("Pesquise parte do nome do cliente para listar: ");
        String filtro = sc.nextLine();
        pesquisarClienteParcial(filtro);

        int id = lerInteiro("Informe a PK (id_cliente) que deseja alterar: ");
        System.out.print("Novo nome: ");
        String nome = sc.nextLine();
        System.out.print("Novo CPF: ");
        String cpf = sc.nextLine();
        System.out.print("Novo telefone: ");
        String tel = sc.nextLine();
        System.out.print("Novo e-mail: ");
        String email = sc.nextLine();

        String sql = "UPDATE CLIENTE SET nome = ?, cpf = ?, telefone = ?, email = ? WHERE id_cliente = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, cpf);
            ps.setString(3, tel);
            ps.setString(4, email);
            ps.setInt(5, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) System.out.println("[SUCESSO] Cliente atualizado!");
            else System.out.println("[AVISO] Nenhum cliente encontrado com o ID informado.");
        } catch (SQLException e) {
            System.out.println("[ERRO AO ALTERAR CLIENTE] Falha na atualização: " + e.getMessage());
        }
    }

    private static void alterarDestino() {
        System.out.print("Pesquise parte da cidade do destino para listar: ");
        String filtro = sc.nextLine();
        pesquisarDestinoParcial(filtro);

        int id = lerInteiro("Informe a PK (id_destino) que deseja alterar: ");
        System.out.print("Nova cidade: ");
        String cidade = sc.nextLine();
        System.out.print("Novo país: ");
        String pais = sc.nextLine();
        System.out.print("Nova descrição: ");
        String desc = sc.nextLine();

        String sql = "UPDATE DESTINO SET cidade = ?, pais = ?, descricao = ? WHERE id_destino = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cidade);
            ps.setString(2, pais);
            ps.setString(3, desc);
            ps.setInt(4, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) System.out.println("[SUCESSO] Destino atualizado!");
            else System.out.println("[AVISO] Nenhum destino encontrado com o ID informado.");
        } catch (SQLException e) {
            System.out.println("[ERRO AO ALTERAR DESTINO] Falha na atualização: " + e.getMessage());
        }
    }

    private static void alterarPacote() {
        System.out.print("Pesquise parte do nome do pacote para listar: ");
        String filtro = sc.nextLine();
        pesquisarPacoteParcial(filtro);

        int id = lerInteiro("Informe a PK (id_pacote) que deseja alterar: ");
        System.out.print("Novo nome do pacote: ");
        String nome = sc.nextLine();
        System.out.print("Novo tipo de transporte: ");
        String transp = sc.nextLine();
        int dias = lerInteiro("Nova duração em dias: ");

        String sql = "UPDATE PACOTE SET nome_pacote = ?, tipo_transporte = ?, dias_duracao = ? WHERE id_pacote = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, transp);
            ps.setInt(3, dias);
            ps.setInt(4, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) System.out.println("[SUCESSO] Pacote atualizado!");
            else System.out.println("[AVISO] Nenhum pacote encontrado com o ID informado.");
        } catch (SQLException e) {
            System.out.println("[ERRO AO ALTERAR PACOTE] Falha na atualização: " + e.getMessage());
        }
    }

    private static void alterarVenda() {
        System.out.print("Pesquise parte do nome do cliente da venda para listar: ");
        String filtro = sc.nextLine();
        pesquisarVendaPorNomeCliente(filtro);

        int idVenda = lerInteiro("Informe a PK (id_venda) que deseja alterar: ");

        // Regra do enunciado: listar tabelas pai para atualizar FKs
        System.out.println("\n--- Escolha o novo Cliente (FK) ---");
        listarClientesSimples();
        int idCliente = lerInteiro("Novo ID do Cliente: ");

        System.out.println("\n--- Escolha o novo Destino (FK) ---");
        listarDestinosSimples();
        int idDestino = lerInteiro("Novo ID do Destino: ");

        System.out.println("\n--- Escolha o novo Pacote (FK) ---");
        listarPacotesSimples();
        int idPacote = lerInteiro("Novo ID do Pacote: ");

        System.out.print("Nova data da viagem (AAAA-MM-DD): ");
        String data = sc.nextLine();
        double valor = lerDouble("Novo valor total: ");

        String sql = "UPDATE VENDA_VIAGEM SET id_cliente = ?, id_destino = ?, id_pacote = ?, data_viagem = ?, valor_total = ? WHERE id_venda = ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            ps.setInt(2, idDestino);
            ps.setInt(3, idPacote);
            ps.setDate(4, Date.valueOf(data));
            ps.setDouble(5, valor);
            ps.setInt(6, idVenda);
            int linhas = ps.executeUpdate();
            if (linhas > 0) System.out.println("[SUCESSO] Venda atualizada!");
            else System.out.println("[AVISO] Nenhuma venda encontrada com o ID informado.");
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRO DE DATA] Formato inválido! Use AAAA-MM-DD.");
        } catch (SQLException e) {
            System.out.println("[ERRO AO ALTERAR VENDA] Verifique os IDs estrangeiros informados: " + e.getMessage());
        }
    }

    // ==================== 03. EXCLUIR DADOS ====================
    private static void menuExcluir() {
        int tab = escolherTabela("EXCLUIR DADOS");
        switch (tab) {
            case 1 -> {
                System.out.print("Pesquise parte do nome do cliente para listar: ");
                pesquisarClienteParcial(sc.nextLine());
                int id = lerInteiro("Informe a PK (id_cliente) para excluir: ");
                executarExclusao("DELETE FROM CLIENTE WHERE id_cliente = ?", id, "Cliente");
            }
            case 2 -> {
                System.out.print("Pesquise parte da cidade do destino para listar: ");
                pesquisarDestinoParcial(sc.nextLine());
                int id = lerInteiro("Informe a PK (id_destino) para excluir: ");
                executarExclusao("DELETE FROM DESTINO WHERE id_destino = ?", id, "Destino");
            }
            case 3 -> {
                System.out.print("Pesquise parte do nome do pacote para listar: ");
                pesquisarPacoteParcial(sc.nextLine());
                int id = lerInteiro("Informe a PK (id_pacote) para excluir: ");
                executarExclusao("DELETE FROM PACOTE WHERE id_pacote = ?", id, "Pacote");
            }
            case 4 -> {
                System.out.print("Pesquise parte do nome do cliente da venda para listar: ");
                pesquisarVendaPorNomeCliente(sc.nextLine());
                int id = lerInteiro("Informe a PK (id_venda) para excluir: ");
                executarExclusao("DELETE FROM VENDA_VIAGEM WHERE id_venda = ?", id, "Venda");
            }
            case 0 -> {}
            default -> System.out.println("[ERRO] Tabela inválida.");
        }
    }

    private static void executarExclusao(String sql, int id, String entidade) {
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            if (linhas > 0) {
                System.out.println("[SUCESSO] " + entidade + " excluído(a) com êxito!");
            } else {
                System.out.println("[AVISO] Nenhum registro de " + entidade + " encontrado com essa PK.");
            }
        } catch (SQLException e) {
            System.out.println("[ERRO AO EXCLUIR " + entidade.toUpperCase() + "] Não é possível excluir pois existem vendas vinculadas (Integridade Referencial) ou erro no banco: " + e.getMessage());
        }
    }

    // ==================== 04. PESQUISAR USANDO DOIS CAMPOS ====================
    private static void menuPesquisarDoisCampos() {
        int tab = escolherTabela("PESQUISAR COM 2 CAMPOS DE REFERÊNCIA");
        switch (tab) {
            case 1 -> {
                System.out.print("Parte do Nome do cliente: ");
                String nome = sc.nextLine();
                System.out.print("Parte do CPF do cliente: ");
                String cpf = sc.nextLine();
                String sql = "SELECT * FROM CLIENTE WHERE nome LIKE ? AND cpf LIKE ?";
                try (Connection conn = Conexao.conectar();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + nome + "%");
                    ps.setString(2, "%" + cpf + "%");
                    ResultSet rs = ps.executeQuery();
                    System.out.println("\n--- Resultado CLIENTE ---");
                    while (rs.next()) {
                        System.out.printf("PK: %d | Nome: %s | CPF: %s | Tel: %s | Email: %s%n",
                                rs.getInt("id_cliente"), rs.getString("nome"), rs.getString("cpf"),
                                rs.getString("telefone"), rs.getString("email"));
                    }
                } catch (SQLException e) {
                    System.out.println("[ERRO NA PESQUISA] " + e.getMessage());
                }
            }
            case 2 -> {
                System.out.print("Parte da Cidade: ");
                String cidade = sc.nextLine();
                System.out.print("Parte do País: ");
                String pais = sc.nextLine();
                String sql = "SELECT * FROM DESTINO WHERE cidade LIKE ? AND pais LIKE ?";
                try (Connection conn = Conexao.conectar();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + cidade + "%");
                    ps.setString(2, "%" + pais + "%");
                    ResultSet rs = ps.executeQuery();
                    System.out.println("\n--- Resultado DESTINO ---");
                    while (rs.next()) {
                        System.out.printf("PK: %d | Cidade: %s | País: %s | Descrição: %s%n",
                                rs.getInt("id_destino"), rs.getString("cidade"), rs.getString("pais"),
                                rs.getString("descricao"));
                    }
                } catch (SQLException e) {
                    System.out.println("[ERRO NA PESQUISA] " + e.getMessage());
                }
            }
            case 3 -> {
                System.out.print("Parte do Nome do Pacote: ");
                String nome = sc.nextLine();
                System.out.print("Parte do Tipo de Transporte: ");
                String transp = sc.nextLine();
                String sql = "SELECT * FROM PACOTE WHERE nome_pacote LIKE ? AND tipo_transporte LIKE ?";
                try (Connection conn = Conexao.conectar();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + nome + "%");
                    ps.setString(2, "%" + transp + "%");
                    ResultSet rs = ps.executeQuery();
                    System.out.println("\n--- Resultado PACOTE ---");
                    while (rs.next()) {
                        System.out.printf("PK: %d | Pacote: %s | Transporte: %s | Duração: %d dias%n",
                                rs.getInt("id_pacote"), rs.getString("nome_pacote"), rs.getString("tipo_transporte"),
                                rs.getInt("dias_duracao"));
                    }
                } catch (SQLException e) {
                    System.out.println("[ERRO NA PESQUISA] " + e.getMessage());
                }
            }
            case 4 -> {
                System.out.print("Parte da Data da Viagem (ex: 2026-12): ");
                String data = sc.nextLine();
                double valorMin = lerDouble("Valor mínimo da venda (ex: 500): ");
                String sql = "SELECT * FROM VENDA_VIAGEM WHERE CAST(data_viagem AS CHAR) LIKE ? AND valor_total >= ?";
                try (Connection conn = Conexao.conectar();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + data + "%");
                    ps.setDouble(2, valorMin);
                    ResultSet rs = ps.executeQuery();
                    System.out.println("\n--- Resultado VENDA_VIAGEM ---");
                    while (rs.next()) {
                        System.out.printf("PK: %d | FK Cliente: %d | FK Destino: %d | FK Pacote: %d | Data: %s | Valor: R$ %.2f%n",
                                rs.getInt("id_venda"), rs.getInt("id_cliente"), rs.getInt("id_destino"),
                                rs.getInt("id_pacote"), rs.getDate("data_viagem"), rs.getDouble("valor_total"));
                    }
                } catch (SQLException e) {
                    System.out.println("[ERRO NA PESQUISA] " + e.getMessage());
                }
            }
            case 0 -> {}
            default -> System.out.println("[ERRO] Tabela inválida.");
        }
    }

    // ==================== 05. PESQUISAR SÓ COM A PK ====================
    private static void menuPesquisarPorPK() {
        int tab = escolherTabela("PESQUISAR POR CHAVE PRIMÁRIA (PK)");
        if (tab == 0) return;

        int pk = lerInteiro("Digite o valor da PK: ");
        try (Connection conn = Conexao.conectar()) {
            switch (tab) {
                case 1 -> {
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM CLIENTE WHERE id_cliente = ?");
                    ps.setInt(1, pk);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        System.out.printf("PK: %d | Nome: %s | CPF: %s | Tel: %s | Email: %s%n",
                                rs.getInt("id_cliente"), rs.getString("nome"), rs.getString("cpf"),
                                rs.getString("telefone"), rs.getString("email"));
                    } else System.out.println("[AVISO] Cliente não encontrado.");
                }
                case 2 -> {
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM DESTINO WHERE id_destino = ?");
                    ps.setInt(1, pk);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        System.out.printf("PK: %d | Cidade: %s | País: %s | Descrição: %s%n",
                                rs.getInt("id_destino"), rs.getString("cidade"), rs.getString("pais"),
                                rs.getString("descricao"));
                    } else System.out.println("[AVISO] Destino não encontrado.");
                }
                case 3 -> {
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM PACOTE WHERE id_pacote = ?");
                    ps.setInt(1, pk);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        System.out.printf("PK: %d | Pacote: %s | Transporte: %s | Duração: %d dias%n",
                                rs.getInt("id_pacote"), rs.getString("nome_pacote"), rs.getString("tipo_transporte"),
                                rs.getInt("dias_duracao"));
                    } else System.out.println("[AVISO] Pacote não encontrado.");
                }
                case 4 -> {
                    PreparedStatement ps = conn.prepareStatement("SELECT * FROM VENDA_VIAGEM WHERE id_venda = ?");
                    ps.setInt(1, pk);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        System.out.printf("PK: %d | FK Cliente: %d | FK Destino: %d | FK Pacote: %d | Data: %s | Valor: R$ %.2f%n",
                                rs.getInt("id_venda"), rs.getInt("id_cliente"), rs.getInt("id_destino"),
                                rs.getInt("id_pacote"), rs.getDate("data_viagem"), rs.getDouble("valor_total"));
                    } else System.out.println("[AVISO] Venda não encontrada.");
                }
                default -> System.out.println("[ERRO] Tabela inválida.");
            }
        } catch (SQLException e) {
            System.out.println("[ERRO NA CONSULTA POR PK] " + e.getMessage());
        }
    }

    // ==================== 06. PESQUISAR USANDO 2 TABELAS ====================
    private static void pesquisarDuasTabelas() {
        System.out.print("\nDigite parte do nome do Cliente para buscar suas vendas: ");
        String nome = sc.nextLine();

        String sql = "SELECT v.id_venda, c.id_cliente, c.nome, c.cpf, v.data_viagem, v.valor_total " +
                     "FROM VENDA_VIAGEM v " +
                     "INNER JOIN CLIENTE c ON v.id_cliente = c.id_cliente " +
                     "WHERE c.nome LIKE ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- Pesquisa em 2 Tabelas (VENDA_VIAGEM + CLIENTE) ---");
            while (rs.next()) {
                System.out.printf("PK Venda: %d | PK Cliente: %d (%s - CPF: %s) | Data: %s | Valor: R$ %.2f%n",
                        rs.getInt("id_venda"), rs.getInt("id_cliente"), rs.getString("nome"),
                        rs.getString("cpf"), rs.getDate("data_viagem"), rs.getDouble("valor_total"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO NA PESQUISA DE 2 TABELAS] " + e.getMessage());
        }
    }

    // ==================== 07. PESQUISAR USANDO 3 TABELAS ====================
    private static void pesquisarTresTabelas() {
        System.out.print("\nDigite parte da Cidade de destino para buscar vendas e clientes: ");
        String cidade = sc.nextLine();

        String sql = "SELECT v.id_venda, c.nome AS cliente, d.cidade, d.pais, v.data_viagem, v.valor_total " +
                     "FROM VENDA_VIAGEM v " +
                     "INNER JOIN CLIENTE c ON v.id_cliente = c.id_cliente " +
                     "INNER JOIN DESTINO d ON v.id_destino = d.id_destino " +
                     "WHERE d.cidade LIKE ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + cidade + "%");
            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- Pesquisa em 3 Tabelas (VENDA_VIAGEM + CLIENTE + DESTINO) ---");
            while (rs.next()) {
                System.out.printf("PK Venda: %d | Cliente: %s | Destino: %s (%s) | Data: %s | Valor: R$ %.2f%n",
                        rs.getInt("id_venda"), rs.getString("cliente"), rs.getString("cidade"),
                        rs.getString("pais"), rs.getDate("data_viagem"), rs.getDouble("valor_total"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO NA PESQUISA DE 3 TABELAS] " + e.getMessage());
        }
    }

    // ==================== 08. PESQUISAR USANDO 4 TABELAS ====================
    private static void pesquisarQuatroTabelas() {
        System.out.print("\nDigite parte do nome do Pacote (ou deixe vazio para ver todos): ");
        String pacote = sc.nextLine();

        String sql = "SELECT v.id_venda, c.nome AS cliente, d.cidade, d.pais, p.nome_pacote, p.tipo_transporte, v.data_viagem, v.valor_total " +
                     "FROM VENDA_VIAGEM v " +
                     "INNER JOIN CLIENTE c ON v.id_cliente = c.id_cliente " +
                     "INNER JOIN DESTINO d ON v.id_destino = d.id_destino " +
                     "INNER JOIN PACOTE p ON v.id_pacote = p.id_pacote " +
                     "WHERE p.nome_pacote LIKE ?";

        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + pacote + "%");
            ResultSet rs = ps.executeQuery();
            System.out.println("\n--- Pesquisa em 4 Tabelas (VENDA + CLIENTE + DESTINO + PACOTE) ---");
            while (rs.next()) {
                System.out.printf("PK Venda: %d | Cliente: %s | Destino: %s/%s | Pacote: %s (%s) | Data: %s | Valor: R$ %.2f%n",
                        rs.getInt("id_venda"), rs.getString("cliente"), rs.getString("cidade"),
                        rs.getString("pais"), rs.getString("nome_pacote"), rs.getString("tipo_transporte"),
                        rs.getDate("data_viagem"), rs.getDouble("valor_total"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO NA PESQUISA DE 4 TABELAS] " + e.getMessage());
        }
    }

    // ==================== 09. LISTAR USANDO 3 TABELAS ====================
    private static void listarVendasCompletas() {
        String sql = "SELECT v.id_venda, c.nome AS cliente, d.cidade, p.nome_pacote, v.data_viagem, v.valor_total " +
                     "FROM VENDA_VIAGEM v " +
                     "INNER JOIN CLIENTE c ON v.id_cliente = c.id_cliente " +
                     "INNER JOIN DESTINO d ON v.id_destino = d.id_destino " +
                     "INNER JOIN PACOTE p ON v.id_pacote = p.id_pacote " +
                     "ORDER BY v.id_venda";

        try (Connection conn = Conexao.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            System.out.println("\n--- Listagem Geral de Vendas (Relacionando Tabelas) ---");
            while (rs.next()) {
                System.out.printf("PK Venda: %d | Cliente: %s | Destino: %s | Pacote: %s | Data: %s | Valor: R$ %.2f%n",
                        rs.getInt("id_venda"), rs.getString("cliente"), rs.getString("cidade"),
                        rs.getString("nome_pacote"), rs.getDate("data_viagem"), rs.getDouble("valor_total"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO AO LISTAR DADOS] " + e.getMessage());
        }
    }

    // ==================== MÉTODOS AUXILIARES DE LISTAGEM/PESQUISA ====================
    private static void listarClientesSimples() {
        try (Connection conn = Conexao.conectar();
             ResultSet rs = conn.createStatement().executeQuery("SELECT id_cliente, nome, cpf FROM CLIENTE")) {
            while (rs.next()) {
                System.out.printf("PK: %d | Nome: %s | CPF: %s%n", rs.getInt("id_cliente"), rs.getString("nome"), rs.getString("cpf"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    private static void listarDestinosSimples() {
        try (Connection conn = Conexao.conectar();
             ResultSet rs = conn.createStatement().executeQuery("SELECT id_destino, cidade, pais FROM DESTINO")) {
            while (rs.next()) {
                System.out.printf("PK: %d | Cidade: %s | País: %s%n", rs.getInt("id_destino"), rs.getString("cidade"), rs.getString("pais"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    private static void listarPacotesSimples() {
        try (Connection conn = Conexao.conectar();
             ResultSet rs = conn.createStatement().executeQuery("SELECT id_pacote, nome_pacote, tipo_transporte FROM PACOTE")) {
            while (rs.next()) {
                System.out.printf("PK: %d | Pacote: %s | Transporte: %s%n", rs.getInt("id_pacote"), rs.getString("nome_pacote"), rs.getString("tipo_transporte"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    private static void pesquisarClienteParcial(String nome) {
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM CLIENTE WHERE nome LIKE ?")) {
            ps.setString(1, "%" + nome + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.printf("PK: %d | Nome: %s | CPF: %s%n", rs.getInt("id_cliente"), rs.getString("nome"), rs.getString("cpf"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    private static void pesquisarDestinoParcial(String cidade) {
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM DESTINO WHERE cidade LIKE ?")) {
            ps.setString(1, "%" + cidade + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.printf("PK: %d | Cidade: %s | País: %s%n", rs.getInt("id_destino"), rs.getString("cidade"), rs.getString("pais"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    private static void pesquisarPacoteParcial(String nome) {
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM PACOTE WHERE nome_pacote LIKE ?")) {
            ps.setString(1, "%" + nome + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.printf("PK: %d | Pacote: %s | Transporte: %s%n", rs.getInt("id_pacote"), rs.getString("nome_pacote"), rs.getString("tipo_transporte"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    private static void pesquisarVendaPorNomeCliente(String nomeCliente) {
        String sql = "SELECT v.id_venda, c.nome, d.cidade, v.data_viagem, v.valor_total " +
                     "FROM VENDA_VIAGEM v " +
                     "INNER JOIN CLIENTE c ON v.id_cliente = c.id_cliente " +
                     "INNER JOIN DESTINO d ON v.id_destino = d.id_destino " +
                     "WHERE c.nome LIKE ?";
        try (Connection conn = Conexao.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nomeCliente + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.printf("PK Venda: %d | Cliente: %s | Destino: %s | Data: %s | Valor: R$ %.2f%n",
                        rs.getInt("id_venda"), rs.getString("nome"), rs.getString("cidade"),
                        rs.getDate("data_viagem"), rs.getDouble("valor_total"));
            }
        } catch (SQLException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    // ==================== VALIDAÇÃO DE ENTRADAS NUMÉRICAS ====================
    private static int lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[ERRO DE ENTRADA] Digite apenas números inteiros válidos.");
            }
        }
    }

    private static double lerDouble(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem);
                return Double.parseDouble(sc.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("[ERRO DE ENTRADA] Digite um valor numérico válido (ex: 1200.50).");
            }
        }
    }
}