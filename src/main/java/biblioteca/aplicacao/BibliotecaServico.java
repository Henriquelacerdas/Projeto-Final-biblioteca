package biblioteca.aplicacao;

import biblioteca.excecao.ExemplarIndisponivelException;
import biblioteca.model.Emprestimo;
import biblioteca.model.Exemplar;
import biblioteca.model.Livro;
import biblioteca.model.PoliticaEmprestimo;
import biblioteca.model.Reserva;
import biblioteca.model.Usuario;
import biblioteca.persistencia.RepositorioGenerico;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Application/use-case layer for the library system.
 *
 * <p>This class is the only place that coordinates multiple domain
 * collaborators (Livro, Exemplar, Usuario, Emprestimo, Reserva) to fulfill a
 * business use case. It depends solely on the {@link RepositorioGenerico}
 * abstraction, never on a concrete storage mechanism (in-memory, file, DB) —
 * this is the Dependency Inversion boundary between the domain core and
 * infrastructure. Any UI (CLI, GUI, Web) is expected to depend only on this
 * class, never on the repositories or persistence details directly.</p>
 *
 * <p>Declares {@link ExemplarIndisponivelException} on the loan use case in
 * anticipation of the state-transition wiring in {@code EstadoExemplar}
 * (owned by another module); a checked throws clause is a valid API contract
 * even before every call site inside actually throws it.</p>
 */
public final class BibliotecaServico {

    private final RepositorioGenerico<Livro, String> livros;
    private final RepositorioGenerico<Exemplar, String> exemplares;
    private final RepositorioGenerico<Usuario, String> usuarios;
    private final RepositorioGenerico<Emprestimo, String> emprestimos;
    private final RepositorioGenerico<Reserva, String> reservas;
    private final AtomicLong sequenciaEmprestimo = new AtomicLong();

    public BibliotecaServico(
            RepositorioGenerico<Livro, String> livros,
            RepositorioGenerico<Exemplar, String> exemplares,
            RepositorioGenerico<Usuario, String> usuarios,
            RepositorioGenerico<Emprestimo, String> emprestimos,
            RepositorioGenerico<Reserva, String> reservas) {
        this.livros = livros;
        this.exemplares = exemplares;
        this.usuarios = usuarios;
        this.emprestimos = emprestimos;
        this.reservas = reservas;
    }

    public Livro cadastrarLivro(String isbn, String titulo, String autor, String editora, int anoPublicacao) {
        return livros.salvar(new Livro(isbn, titulo, autor, editora, anoPublicacao));
    }

    public Exemplar cadastrarExemplar(String codigoExemplar, String isbnLivro) {
        Livro livro = buscarLivro(isbnLivro);
        return exemplares.salvar(new Exemplar(codigoExemplar, livro));
    }

    public Usuario cadastrarUsuario(String codigo, String nome, String email, PoliticaEmprestimo politica) {
        return usuarios.salvar(new Usuario(codigo, nome, email, politica));
    }

    /**
     * Registers a loan. Fails fast when the user already reached their loan
     * limit; delegates the exemplar's own availability check to its state
     * machine, which is expected to throw {@link ExemplarIndisponivelException}
     * once the state classes are wired to it.
     */
    public Emprestimo emprestar(String codigoUsuario, String codigoExemplar, LocalDate dataEmprestimo)
            throws ExemplarIndisponivelException {
        Usuario usuario = buscarUsuario(codigoUsuario);
        Exemplar exemplar = buscarExemplar(codigoExemplar);

        if (usuario.atingiuLimiteEmprestimos(Math.toIntExact(contarEmprestimosAtivos(usuario)))) {
            throw new IllegalStateException(
                    "Usuario " + usuario.codigo() + " atingiu o limite de emprestimos permitido");
        }

        exemplar.emprestar();

        Emprestimo emprestimo = new Emprestimo(gerarCodigoEmprestimo(), usuario, exemplar, dataEmprestimo);
        return emprestimos.salvar(emprestimo);
    }

    /**
     * Registers a return. If there is a reservation queue with interested
     * users for this exemplar, hands it to the next user in line (Observer)
     * instead of making it generally available again.
     */
    public void devolver(String codigoEmprestimo, LocalDate dataDevolucao) {
        Emprestimo emprestimo = buscarEmprestimo(codigoEmprestimo);
        emprestimo.registrarDevolucao(dataDevolucao);

        Exemplar exemplar = emprestimo.exemplar();
        Reserva reserva = reservas.buscarPorId(exemplar.codigo()).orElse(null);

        if (reserva != null && reserva.possuiInteressados()) {
            exemplar.devolverParaReserva();
            reserva.notificarProximoDaFila();
        } else {
            exemplar.devolver();
        }
    }

    /** Registers a user's interest in an exemplar, creating its reservation queue on first use. */
    public void reservar(String codigoUsuario, String codigoExemplar) {
        Usuario usuario = buscarUsuario(codigoUsuario);
        Exemplar exemplar = buscarExemplar(codigoExemplar);

        Reserva reserva = reservas.buscarPorId(exemplar.codigo())
                .orElseGet(() -> reservas.salvar(new Reserva(exemplar)));
        reserva.registrarInteresse(usuario);
    }

    public BigDecimal consultarMulta(String codigoEmprestimo, LocalDate dataReferencia) {
        return buscarEmprestimo(codigoEmprestimo).calcularMulta(dataReferencia);
    }

    public List<Livro> listarLivros() {
        return livros.listarTodos();
    }

    public List<Exemplar> listarExemplares() {
        return exemplares.listarTodos();
    }

    public List<Usuario> listarUsuarios() {
        return usuarios.listarTodos();
    }

    public List<Emprestimo> listarEmprestimosAtivos() {
        return emprestimos.listarTodos().stream().filter(Emprestimo::estaAtivo).toList();
    }

    private long contarEmprestimosAtivos(Usuario usuario) {
        return emprestimos.listarTodos().stream()
                .filter(e -> e.usuario().equals(usuario) && e.estaAtivo())
                .count();
    }

    private String gerarCodigoEmprestimo() {
        return "EMP-" + String.format("%04d", sequenciaEmprestimo.incrementAndGet());
    }

    private Livro buscarLivro(String isbn) {
        return livros.buscarPorId(isbn)
                .orElseThrow(() -> new NoSuchElementException("Livro nao encontrado: " + isbn));
    }

    private Exemplar buscarExemplar(String codigo) {
        return exemplares.buscarPorId(codigo)
                .orElseThrow(() -> new NoSuchElementException("Exemplar nao encontrado: " + codigo));
    }

    private Usuario buscarUsuario(String codigo) {
        return usuarios.buscarPorId(codigo)
                .orElseThrow(() -> new NoSuchElementException("Usuario nao encontrado: " + codigo));
    }

    private Emprestimo buscarEmprestimo(String codigo) {
        return emprestimos.buscarPorId(codigo)
                .orElseThrow(() -> new NoSuchElementException("Emprestimo nao encontrado: " + codigo));
    }
}
