package biblioteca.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LivroTest {

    @Test
    void deveCriarLivroComDadosValidos() {
        var livro = new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);

        assertEquals("978-8532530786", livro.isbn());
        assertEquals("Clean Code", livro.titulo());
        assertEquals("Robert C. Martin", livro.autor());
        assertEquals("Prentice Hall", livro.editora());
        assertEquals(2008, livro.anoPublicacao());
    }

    @Test
    void deveRejeitar_IsbnNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro(null, "Clean Code", "Robert C. Martin", "Prentice Hall", 2008));
    }

    @Test
    void deveRejeitar_IsbnVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008));
    }

    @Test
    void deveRejeitar_IsbnComApenasEspacos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("   ", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008));
    }

    @Test
    void deveRejeitar_TituloNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", null, "Robert C. Martin", "Prentice Hall", 2008));
    }

    @Test
    void deveRejeitar_TituloVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", "", "Robert C. Martin", "Prentice Hall", 2008));
    }

    @Test
    void deveRejeitar_TituloComApenasEspacos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", "   ", "Robert C. Martin", "Prentice Hall", 2008));
    }

    @Test
    void deveRejeitar_AutorNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", "Clean Code", null, "Prentice Hall", 2008));
    }

    @Test
    void deveRejeitar_AutorVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", "Clean Code", "", "Prentice Hall", 2008));
    }

    @Test
    void deveRejeitar_AutorComApenasEspacos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", "Clean Code", "   ", "Prentice Hall", 2008));
    }

    @Test
    void deveRejeitar_EditoraNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", "Clean Code", "Robert C. Martin", null, 2008));
    }

    @Test
    void deveRejeitar_EditoraVazia() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "", 2008));
    }

    @Test
    void deveRejeitar_EditoraComApenasEspacos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "   ", 2008));
    }

    @Test
    void deveRejeitar_AnoPublicacaoZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 0));
    }

    @Test
    void deveRejeitar_AnoPublicacaoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", -2008));
    }

    @Test
    void doisLivrosComMesmoIsbnSaoIguais_MesmoComAtributosDiferentes() {
        var livro1 = new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);
        var livro2 = new Livro("978-8532530786", "Código Limpo", "Bob Martin", "Editora Diferente", 2020);

        assertEquals(livro1, livro2);
    }

    @Test
    void doisLivrosComIsbnsDiferentesNaoSaoIguais() {
        var livro1 = new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);
        var livro2 = new Livro("978-1491927281", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);

        assertNotEquals(livro1, livro2);
    }

    @Test
    void objetosIguaisPossuemMesmoHashCode() {
        var livro1 = new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);
        var livro2 = new Livro("978-8532530786", "Código Limpo", "Bob Martin", "Editora Diferente", 2020);

        assertEquals(livro1.hashCode(), livro2.hashCode());
    }

    @Test
    void livroNaoEhIgualANull() {
        var livro = new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);

        assertNotEquals(livro, null);
        assertNotEquals(null, livro);
    }

    @Test
    void livroNaoEhIgualAOutroTipo() {
        var livro = new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);
        var string = "978-8532530786";

        assertNotEquals(livro, string);
        assertNotEquals(string, livro);
    }

    @Test
    void toStringContemInformacoesUteis() {
        var livro = new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);
        var toString = livro.toString();

        assertTrue(toString.contains("978-8532530786"), "toString deve conter ISBN");
        assertTrue(toString.contains("Clean Code"), "toString deve conter título");
        assertTrue(toString.contains("Robert C. Martin"), "toString deve conter autor");
        assertTrue(toString.contains("Prentice Hall"), "toString deve conter editora");
        assertTrue(toString.contains("2008"), "toString deve conter ano");
    }

    @Test
    void metodosDeConsultaRetornamValoresCorretos() {
        var isbn = "978-8532530786";
        var titulo = "Clean Code";
        var autor = "Robert C. Martin";
        var editora = "Prentice Hall";
        var ano = 2008;

        var livro = new Livro(isbn, titulo, autor, editora, ano);

        assertAll(
                () -> assertEquals(isbn, livro.isbn()),
                () -> assertEquals(titulo, livro.titulo()),
                () -> assertEquals(autor, livro.autor()),
                () -> assertEquals(editora, livro.editora()),
                () -> assertEquals(ano, livro.anoPublicacao())
        );
    }

    @Test
    void preferencaValoresComEspacosSaoPreservados() {
        var livroComEspacos = new Livro("  ISBN-123  ", "  Título  ", "  Autor  ", "  Editora  ", 2020);

        assertEquals("  ISBN-123  ", livroComEspacos.isbn());
        assertEquals("  Título  ", livroComEspacos.titulo());
        assertEquals("  Autor  ", livroComEspacos.autor());
        assertEquals("  Editora  ", livroComEspacos.editora());
    }
}
