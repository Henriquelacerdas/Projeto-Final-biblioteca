package biblioteca.aplicacao;

import biblioteca.excecao.EstadoInvalidoException;
import biblioteca.excecao.ExemplarIndisponivelException;
import biblioteca.model.Emprestimo;
import biblioteca.model.Exemplar;
import biblioteca.model.Livro;
import biblioteca.model.PoliticaEmprestimoPadrao;
import biblioteca.model.Usuario;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Console interface for the library system. A pure infrastructure detail:
 * it only talks to {@link BibliotecaServico} and never touches a repository
 * or a domain object's internals directly, so it could be swapped for a
 * GUI/Web front end without changing a single business rule.
 *
 * <p>Validation/business failures are caught here, at the boundary, so a
 * single bad operation reports a friendly message instead of crashing the
 * whole session (fail-fast inside the domain, fail-safe at the UI edge).</p>
 */
public final class MenuCLI {

    private final BibliotecaServico servico;
    private final Scanner entrada;

    public MenuCLI(BibliotecaServico servico) {
        this.servico = servico;
        this.entrada = new Scanner(System.in);
    }

    public void executar() {
        boolean continuar = true;
        while (continuar) {
            exibirMenu();
            String opcao = entrada.nextLine().trim();
            try {
                continuar = processarOpcao(opcao);
            } catch (ExemplarIndisponivelException e) {
                System.out.println("[Emprestimo recusado] " + e.getMessage());
            } catch (EstadoInvalidoException e) {
                System.out.println("[Transicao invalida] " + e.getMessage()
                        + " (estado atual: " + e.estadoAtual() + ", operacao: " + e.operacao() + ")");
            } catch (NoSuchElementException | IllegalArgumentException | IllegalStateException e) {
                System.out.println("[Erro] " + e.getMessage());
            } catch (UnsupportedOperationException e) {
                // Stopgap: EstadoExemplar still throws this as a placeholder for invalid
                // transitions instead of ExemplarIndisponivelException/EstadoInvalidoException
                // (see TODOs in EstadoExemplar). Remove this catch once that wiring lands.
                System.out.println("[Transicao invalida] " + e.getMessage());
            }
        }
        System.out.println("Ate logo!");
    }

    private boolean processarOpcao(String opcao) throws ExemplarIndisponivelException {
        switch (opcao) {
            case "1" -> cadastrarLivro();
            case "2" -> cadastrarExemplar();
            case "3" -> cadastrarUsuario();
            case "4" -> emprestar();
            case "5" -> devolver();
            case "6" -> reservar();
            case "7" -> consultarMulta();
            case "8" -> listarLivros();
            case "9" -> listarExemplares();
            case "10" -> listarUsuarios();
            case "11" -> listarEmprestimosAtivos();
            case "0" -> {
                return false;
            }
            default -> System.out.println("Opcao invalida.");
        }
        return true;
    }

    private void exibirMenu() {
        System.out.println();
        System.out.println("===== Biblioteca =====");
        System.out.println("1  - Cadastrar livro");
        System.out.println("2  - Cadastrar exemplar");
        System.out.println("3  - Cadastrar usuario");
        System.out.println("4  - Emprestar exemplar");
        System.out.println("5  - Devolver exemplar");
        System.out.println("6  - Reservar exemplar");
        System.out.println("7  - Consultar multa");
        System.out.println("8  - Listar livros");
        System.out.println("9  - Listar exemplares");
        System.out.println("10 - Listar usuarios");
        System.out.println("11 - Listar emprestimos ativos");
        System.out.println("0  - Sair");
        System.out.print("Escolha: ");
    }

    private void cadastrarLivro() {
        String isbn = ler("ISBN");
        String titulo = ler("Titulo");
        String autor = ler("Autor");
        String editora = ler("Editora");
        int ano = Integer.parseInt(ler("Ano de publicacao"));
        Livro livro = servico.cadastrarLivro(isbn, titulo, autor, editora, ano);
        System.out.println("Livro cadastrado: " + livro);
    }

    private void cadastrarExemplar() {
        String codigo = ler("Codigo do exemplar");
        String isbn = ler("ISBN do livro");
        Exemplar exemplar = servico.cadastrarExemplar(codigo, isbn);
        System.out.println("Exemplar cadastrado: " + exemplar);
    }

    private void cadastrarUsuario() {
        String codigo = ler("Codigo do usuario");
        String nome = ler("Nome");
        String email = ler("Email");
        // Only PoliticaEmprestimoPadrao exists today; swap/pick a policy per
        // user type here once more Strategy implementations are added.
        Usuario usuario = servico.cadastrarUsuario(codigo, nome, email, new PoliticaEmprestimoPadrao());
        System.out.println("Usuario cadastrado: " + usuario);
    }

    private void emprestar() throws ExemplarIndisponivelException {
        String codigoUsuario = ler("Codigo do usuario");
        String codigoExemplar = ler("Codigo do exemplar");
        Emprestimo emprestimo = servico.emprestar(codigoUsuario, codigoExemplar, LocalDate.now());
        System.out.println("Emprestimo registrado: " + emprestimo);
    }

    private void devolver() {
        String codigoEmprestimo = ler("Codigo do emprestimo");
        servico.devolver(codigoEmprestimo, LocalDate.now());
        System.out.println("Devolucao registrada.");
    }

    private void reservar() {
        String codigoUsuario = ler("Codigo do usuario");
        String codigoExemplar = ler("Codigo do exemplar");
        servico.reservar(codigoUsuario, codigoExemplar);
        System.out.println("Reserva registrada.");
    }

    private void consultarMulta() {
        String codigoEmprestimo = ler("Codigo do emprestimo");
        BigDecimal multa = servico.consultarMulta(codigoEmprestimo, LocalDate.now());
        System.out.println("Multa atual: R$ " + multa);
    }

    private void listarLivros() {
        List<Livro> livros = servico.listarLivros();
        livros.forEach(System.out::println);
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
        }
    }

    private void listarExemplares() {
        List<Exemplar> exemplares = servico.listarExemplares();
        exemplares.forEach(System.out::println);
        if (exemplares.isEmpty()) {
            System.out.println("Nenhum exemplar cadastrado.");
        }
    }

    private void listarUsuarios() {
        List<Usuario> usuarios = servico.listarUsuarios();
        usuarios.forEach(System.out::println);
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuario cadastrado.");
        }
    }

    private void listarEmprestimosAtivos() {
        List<Emprestimo> emprestimos = servico.listarEmprestimosAtivos();
        emprestimos.forEach(System.out::println);
        if (emprestimos.isEmpty()) {
            System.out.println("Nenhum emprestimo ativo.");
        }
    }

    private String ler(String rotulo) {
        System.out.print(rotulo + ": ");
        return entrada.nextLine().trim();
    }
}
