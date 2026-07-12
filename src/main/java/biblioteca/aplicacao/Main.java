package biblioteca.aplicacao;

import biblioteca.model.Emprestimo;
import biblioteca.model.Exemplar;
import biblioteca.model.Livro;
import biblioteca.model.Reserva;
import biblioteca.model.Usuario;
import biblioteca.persistencia.RepositorioEmMemoria;
import biblioteca.persistencia.RepositorioGenerico;

/**
 * Composition root: the only place in the codebase where concrete
 * infrastructure classes ({@link RepositorioEmMemoria}) are instantiated and
 * wired into the domain-facing abstractions ({@link RepositorioGenerico}).
 * Swapping in-memory storage for file/DB persistence later means changing
 * only this method.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        RepositorioGenerico<Livro, String> livros = new RepositorioEmMemoria<>(Livro::isbn);
        RepositorioGenerico<Exemplar, String> exemplares = new RepositorioEmMemoria<>(Exemplar::codigo);
        RepositorioGenerico<Usuario, String> usuarios = new RepositorioEmMemoria<>(Usuario::codigo);
        RepositorioGenerico<Emprestimo, String> emprestimos = new RepositorioEmMemoria<>(Emprestimo::codigo);
        RepositorioGenerico<Reserva, String> reservas = new RepositorioEmMemoria<>(reserva -> reserva.exemplar().codigo());

        BibliotecaServico servico = new BibliotecaServico(livros, exemplares, usuarios, emprestimos, reservas);
        new MenuCLI(servico).executar();
    }
}
