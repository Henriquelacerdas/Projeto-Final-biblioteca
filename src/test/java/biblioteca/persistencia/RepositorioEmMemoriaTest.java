package biblioteca.persistencia;

import biblioteca.model.Livro;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioEmMemoriaTest {

    private RepositorioGenerico<Livro, String> novoRepositorio() {
        return new RepositorioEmMemoria<>(Livro::isbn);
    }

    private Livro livro(String isbn, String titulo) {
        return new Livro(isbn, titulo, "Autor", "Editora", 2020);
    }

    @Test
    void deveSalvarERecuperarPorId() {
        var repo = novoRepositorio();
        var livro = livro("111", "Clean Code");

        repo.salvar(livro);

        Optional<Livro> encontrado = repo.buscarPorId("111");
        assertTrue(encontrado.isPresent());
        assertEquals(livro, encontrado.get());
    }

    @Test
    void salvarDeveDevolverAPropriaEntidade() {
        var repo = novoRepositorio();
        var livro = livro("111", "Clean Code");

        assertSame(livro, repo.salvar(livro));
    }

    @Test
    void buscarPorIdInexistenteDeveRetornarOptionalVazio() {
        var repo = novoRepositorio();

        assertTrue(repo.buscarPorId("000").isEmpty());
    }

    @Test
    void salvarComMesmoIdDeveSubstituirNaoDuplicar() {
        var repo = novoRepositorio();
        repo.salvar(livro("111", "Primeira Edicao"));
        repo.salvar(livro("111", "Segunda Edicao"));

        assertEquals(1, repo.quantidade());
        assertEquals("Segunda Edicao", repo.buscarPorId("111").get().titulo());
    }

    @Test
    void listarTodosDeveRetornarTodasAsEntidades() {
        var repo = novoRepositorio();
        repo.salvar(livro("111", "A"));
        repo.salvar(livro("222", "B"));

        List<Livro> todos = repo.listarTodos();
        assertEquals(2, todos.size());
    }

    @Test
    void listarTodosDeveRetornarColecaoIndependente() {
        var repo = novoRepositorio();
        repo.salvar(livro("111", "A"));

        List<Livro> todos = repo.listarTodos();
        todos.clear(); // alterar a copia nao pode afetar o repositorio

        assertEquals(1, repo.quantidade());
    }

    @Test
    void listarTodosEmRepositorioVazioDeveRetornarListaVazia() {
        var repo = novoRepositorio();

        assertTrue(repo.listarTodos().isEmpty());
    }

    @Test
    void removerEntidadeExistenteDeveRetornarTrueERemover() {
        var repo = novoRepositorio();
        repo.salvar(livro("111", "A"));

        assertTrue(repo.remover("111"));
        assertFalse(repo.existe("111"));
        assertEquals(0, repo.quantidade());
    }

    @Test
    void removerEntidadeInexistenteDeveRetornarFalse() {
        var repo = novoRepositorio();

        assertFalse(repo.remover("000"));
    }

    @Test
    void existeDeveRefletirPresencaDaEntidade() {
        var repo = novoRepositorio();
        assertFalse(repo.existe("111"));

        repo.salvar(livro("111", "A"));
        assertTrue(repo.existe("111"));
    }

    @Test
    void quantidadeDeveRefletirNumeroDeEntidades() {
        var repo = novoRepositorio();
        assertEquals(0, repo.quantidade());

        repo.salvar(livro("111", "A"));
        repo.salvar(livro("222", "B"));
        assertEquals(2, repo.quantidade());
    }

    @Test
    void deveFuncionarComTipoDeEntidadeEIdDiferentes() {
        // generics: mesmo repositorio serve para outra entidade/chave, sem cast
        RepositorioGenerico<String, Integer> repo =
                new RepositorioEmMemoria<>(String::length);
        repo.salvar("abc");   // id = 3
        repo.salvar("abcde"); // id = 5

        assertEquals("abc", repo.buscarPorId(3).get());
        assertEquals("abcde", repo.buscarPorId(5).get());
    }

    @Test
    void construtorComExtratorNuloDeveLancar() {
        assertThrows(NullPointerException.class,
                () -> new RepositorioEmMemoria<Livro, String>(null));
    }

    @Test
    void salvarEntidadeNulaDeveLancar() {
        var repo = novoRepositorio();
        assertThrows(NullPointerException.class, () -> repo.salvar(null));
    }

    @Test
    void buscarPorIdNuloDeveLancar() {
        var repo = novoRepositorio();
        assertThrows(NullPointerException.class, () -> repo.buscarPorId(null));
    }

    @Test
    void removerIdNuloDeveLancar() {
        var repo = novoRepositorio();
        assertThrows(NullPointerException.class, () -> repo.remover(null));
    }

    @Test
    void existeIdNuloDeveLancar() {
        var repo = novoRepositorio();
        assertThrows(NullPointerException.class, () -> repo.existe(null));
    }

    @Test
    void salvarEntidadeCujoIdExtraidoEhNuloDeveLancar() {
        RepositorioGenerico<Livro, String> repo =
                new RepositorioEmMemoria<>(l -> null); // extrator devolve id nulo
        assertThrows(NullPointerException.class,
                () -> repo.salvar(livro("111", "A")));
    }
}
