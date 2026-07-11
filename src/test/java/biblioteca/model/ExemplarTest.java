package biblioteca.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExemplarTest {

    private Livro criarLivroValido() {
        return new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);
    }

    @Test
    void deveCreateExemplarValidoCorretamente() {
        var livro = criarLivroValido();
        var exemplar = new Exemplar("EX-001", livro);

        assertNotNull(exemplar);
        assertEquals("EX-001", exemplar.codigo());
        assertEquals(livro, exemplar.livro());
    }

    @Test
    void deveComecarioNoEstadoDisponivel() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());

        assertEquals("Disponível", exemplar.estadoAtual());
    }

    @Test
    void devRetornarMesmoCodigo() {
        var codigo = "EX-001";
        var exemplar = new Exemplar(codigo, criarLivroValido());

        assertEquals(codigo, exemplar.codigo());
    }

    @Test
    void devRetornarMesmaInstanciaDeLivro() {
        var livro = criarLivroValido();
        var exemplar = new Exemplar("EX-001", livro);

        assertSame(livro, exemplar.livro());
    }

    @Test
    void deveRejeitar_CodigoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exemplar(null, criarLivroValido()));
    }

    @Test
    void deveRejeitar_CodigoVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exemplar("", criarLivroValido()));
    }

    @Test
    void deveRejeitar_CodigoComApenasEspacos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exemplar("   ", criarLivroValido()));
    }

    @Test
    void deveRejeitar_LivroNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Exemplar("EX-001", null));
    }

    @Test
    void doisExemplaresiComMesmoCodigo_SaoIguais_MesmoComLivrosDiferentes() {
        var livro1 = criarLivroValido();
        var livro2 = new Livro("978-1491927281", "Design Patterns", "Gang of Four", "Addison-Wesley", 1994);

        var exemplar1 = new Exemplar("EX-001", livro1);
        var exemplar2 = new Exemplar("EX-001", livro2);

        assertEquals(exemplar1, exemplar2);
    }

    @Test
    void doisExemplarComCodigosDiferentes_NaoSaoIguais() {
        var livro = criarLivroValido();
        var exemplar1 = new Exemplar("EX-001", livro);
        var exemplar2 = new Exemplar("EX-002", livro);

        assertNotEquals(exemplar1, exemplar2);
    }

    @Test
    void exemplaresiIguaisPossuemMesmoHashCode() {
        var exemplar1 = new Exemplar("EX-001", criarLivroValido());
        var exemplar2 = new Exemplar("EX-001", new Livro("978-1491927281", "Design Patterns", "Gang of Four", "Addison-Wesley", 1994));

        assertEquals(exemplar1.hashCode(), exemplar2.hashCode());
    }

    @Test
    void exemplarNaoEhIgualANull() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());

        assertNotEquals(exemplar, null);
        assertNotEquals(null, exemplar);
    }

    @Test
    void exemplarNaoEhIgualAOutroTipo() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        var string = "EX-001";

        assertNotEquals(exemplar, string);
        assertNotEquals(string, exemplar);
    }

    @Test
    void toStringContemInformacoesUteis() {
        var livro = criarLivroValido();
        var exemplar = new Exemplar("EX-001", livro);
        var toString = exemplar.toString();

        assertTrue(toString.contains("EX-001"), "toString deve conter código");
        assertTrue(toString.contains("Clean Code"), "toString deve conter título do livro");
        assertTrue(toString.contains("Disponível"), "toString deve conter estado");
    }

    // Transições válidas

    @Test
    void novoExemplarComecaDisponivel() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());

        assertEquals("Disponível", exemplar.estadoAtual());
    }

    @Test
    void emprestarAlteraDeDisponivelParaEmprestado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());

        exemplar.emprestar();

        assertEquals("Emprestado", exemplar.estadoAtual());
    }

    @Test
    void devolverAlteraDeEmprestadoParaDisponivel() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        exemplar.emprestar();

        exemplar.devolver();

        assertEquals("Disponível", exemplar.estadoAtual());
    }

    @Test
    void devolverParaReservaAlteraDeEmprestadoParaReservado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        exemplar.emprestar();

        exemplar.devolverParaReserva();

        assertEquals("Reservado", exemplar.estadoAtual());
    }

    @Test
    void retirarReservaAlteraDeReservadoParaEmprestado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        exemplar.emprestar();
        exemplar.devolverParaReserva();

        exemplar.retirarReserva();

        assertEquals("Emprestado", exemplar.estadoAtual());
    }

    @Test
    void cancelarReservaAlteraDeReservadoParaDisponivel() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        exemplar.emprestar();
        exemplar.devolverParaReserva();

        exemplar.cancelarReserva();

        assertEquals("Disponível", exemplar.estadoAtual());
    }

    @Test
    void sequenciaCompletaDeTransicoesValidasMantemExemplarConsistente() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());

        exemplar.emprestar();
        assertEquals("Emprestado", exemplar.estadoAtual());

        exemplar.devolverParaReserva();
        assertEquals("Reservado", exemplar.estadoAtual());

        exemplar.retirarReserva();
        assertEquals("Emprestado", exemplar.estadoAtual());

        exemplar.devolver();
        assertEquals("Disponível", exemplar.estadoAtual());

        exemplar.emprestar();
        assertEquals("Emprestado", exemplar.estadoAtual());

        exemplar.devolverParaReserva();
        assertEquals("Reservado", exemplar.estadoAtual());

        exemplar.cancelarReserva();
        assertEquals("Disponível", exemplar.estadoAtual());
    }

    // Transições inválidas — estado Disponível
    // As exceções ExemplarIndisponivelException e EstadoInvalidoException pertencem a P0
    // e ainda não existem no projeto; a implementação atual usa UnsupportedOperationException
    // como placeholder (ver TODOs em EstadoExemplar). Estes testes devem ser atualizados
    // para as exceções corretas assim que P0 as implementar.

    @Test
    void disponivel_devolver_lancaExcecaoENaoAlteraEstado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());

        assertThrows(UnsupportedOperationException.class, exemplar::devolver);
        assertEquals("Disponível", exemplar.estadoAtual());
    }

    @Test
    void disponivel_devolverParaReserva_lancaExcecaoENaoAlteraEstado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());

        assertThrows(UnsupportedOperationException.class, exemplar::devolverParaReserva);
        assertEquals("Disponível", exemplar.estadoAtual());
    }

    @Test
    void disponivel_retirarReserva_lancaExcecaoENaoAlteraEstado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());

        assertThrows(UnsupportedOperationException.class, exemplar::retirarReserva);
        assertEquals("Disponível", exemplar.estadoAtual());
    }

    @Test
    void disponivel_cancelarReserva_lancaExcecaoENaoAlteraEstado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());

        assertThrows(UnsupportedOperationException.class, exemplar::cancelarReserva);
        assertEquals("Disponível", exemplar.estadoAtual());
    }

    // Transições inválidas — estado Emprestado

    @Test
    void emprestado_emprestar_lancaExcecaoENaoAlteraEstado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        exemplar.emprestar();

        assertThrows(UnsupportedOperationException.class, exemplar::emprestar);
        assertEquals("Emprestado", exemplar.estadoAtual());
    }

    @Test
    void emprestado_retirarReserva_lancaExcecaoENaoAlteraEstado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        exemplar.emprestar();

        assertThrows(UnsupportedOperationException.class, exemplar::retirarReserva);
        assertEquals("Emprestado", exemplar.estadoAtual());
    }

    @Test
    void emprestado_cancelarReserva_lancaExcecaoENaoAlteraEstado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        exemplar.emprestar();

        assertThrows(UnsupportedOperationException.class, exemplar::cancelarReserva);
        assertEquals("Emprestado", exemplar.estadoAtual());
    }

    // Transições inválidas — estado Reservado

    @Test
    void reservado_emprestar_lancaExcecaoENaoAlteraEstado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        exemplar.emprestar();
        exemplar.devolverParaReserva();

        assertThrows(UnsupportedOperationException.class, exemplar::emprestar);
        assertEquals("Reservado", exemplar.estadoAtual());
    }

    @Test
    void reservado_devolver_lancaExcecaoENaoAlteraEstado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        exemplar.emprestar();
        exemplar.devolverParaReserva();

        assertThrows(UnsupportedOperationException.class, exemplar::devolver);
        assertEquals("Reservado", exemplar.estadoAtual());
    }

    @Test
    void reservado_devolverParaReserva_lancaExcecaoENaoAlteraEstado() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());
        exemplar.emprestar();
        exemplar.devolverParaReserva();

        assertThrows(UnsupportedOperationException.class, exemplar::devolverParaReserva);
        assertEquals("Reservado", exemplar.estadoAtual());
    }

    // Testes adicionais

    @Test
    void codigoNaoMudaDuranteAsTransicoes() {
        var exemplar = new Exemplar("EX-001", criarLivroValido());

        exemplar.emprestar();
        exemplar.devolverParaReserva();
        exemplar.retirarReserva();
        exemplar.devolver();

        assertEquals("EX-001", exemplar.codigo());
    }

    @Test
    void livroNaoMudaDuranteAsTransicoes() {
        var livro = criarLivroValido();
        var exemplar = new Exemplar("EX-001", livro);

        exemplar.emprestar();
        exemplar.devolverParaReserva();
        exemplar.retirarReserva();
        exemplar.devolver();

        assertSame(livro, exemplar.livro());
    }

    @Test
    void doisExemplaresDiferentesMantemEstadosIndependentes() {
        var exemplar1 = new Exemplar("EX-001", criarLivroValido());
        var exemplar2 = new Exemplar("EX-002", criarLivroValido());

        exemplar1.emprestar();

        assertEquals("Emprestado", exemplar1.estadoAtual());
        assertEquals("Disponível", exemplar2.estadoAtual());
    }

    @Test
    void alterarEstadoDeUmExemplarNaoAfetaOutro() {
        var exemplar1 = new Exemplar("EX-001", criarLivroValido());
        var exemplar2 = new Exemplar("EX-002", criarLivroValido());

        exemplar1.emprestar();
        exemplar1.devolverParaReserva();

        assertEquals("Reservado", exemplar1.estadoAtual());
        assertEquals("Disponível", exemplar2.estadoAtual());
    }
}
