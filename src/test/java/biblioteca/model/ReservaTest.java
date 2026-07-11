package biblioteca.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    private Livro criarLivroValido() {
        return new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);
    }

    private Exemplar criarExemplarValido() {
        return new Exemplar("EX-001", criarLivroValido());
    }

    private Usuario criarUsuario(String codigo) {
        return new Usuario(codigo, "Usuario " + codigo, codigo + "@exemplo.com", new PoliticaEmprestimoPadrao());
    }

    // Construção

    @Test
    void deveCriarReservaValidaAPartirDeExemplar() {
        var reserva = new Reserva(criarExemplarValido());

        assertNotNull(reserva);
    }

    @Test
    void reservaRecemCriadaNaoPossuiInteressados() {
        var reserva = new Reserva(criarExemplarValido());

        assertFalse(reserva.possuiInteressados());
    }

    @Test
    void reservaRecemCriadaRetornaQuantidadeInteressadosZero() {
        var reserva = new Reserva(criarExemplarValido());

        assertEquals(0, reserva.quantidadeInteressados());
    }

    @Test
    void consultaDeExemplarRetornaObjetoInformadoNoConstrutor() {
        var exemplar = criarExemplarValido();
        var reserva = new Reserva(exemplar);

        assertSame(exemplar, reserva.exemplar());
    }

    // Validação

    @Test
    void deveRejeitar_ExemplarNuloNoConstrutor() {
        assertThrows(IllegalArgumentException.class, () -> new Reserva(null));
    }

    @Test
    void deveRejeitar_UsuarioNuloEmRegistrarInteresse() {
        var reserva = new Reserva(criarExemplarValido());

        assertThrows(IllegalArgumentException.class, () -> reserva.registrarInteresse(null));
    }

    @Test
    void deveRejeitar_UsuarioNuloEmEstaInteressado() {
        var reserva = new Reserva(criarExemplarValido());

        assertThrows(IllegalArgumentException.class, () -> reserva.estaInteressado(null));
    }

    @Test
    void deveRejeitar_RegistrarInteresseDuplicadoEnquantoNaFila() {
        var reserva = new Reserva(criarExemplarValido());
        var usuario = criarUsuario("U-001");
        reserva.registrarInteresse(usuario);

        assertThrows(IllegalStateException.class, () -> reserva.registrarInteresse(usuario));
    }

    @Test
    void deveRejeitar_ProximoDaFilaComFilaVazia() {
        var reserva = new Reserva(criarExemplarValido());

        assertThrows(IllegalStateException.class, reserva::proximoDaFila);
    }

    @Test
    void deveRejeitar_RemoverProximoComFilaVazia() {
        var reserva = new Reserva(criarExemplarValido());

        assertThrows(IllegalStateException.class, reserva::removerProximo);
    }

    @Test
    void tentativaDeInteresseDuplicadoNaoAlteraQuantidadeNemOrdemDaFila() {
        var reserva = new Reserva(criarExemplarValido());
        var usuario1 = criarUsuario("U-001");
        var usuario2 = criarUsuario("U-002");
        reserva.registrarInteresse(usuario1);
        reserva.registrarInteresse(usuario2);

        assertThrows(IllegalStateException.class, () -> reserva.registrarInteresse(usuario1));

        assertEquals(2, reserva.quantidadeInteressados());
        assertSame(usuario1, reserva.proximoDaFila());
    }

    @Test
    void tentativaDeRegistrarInteresseComUsuarioNuloNaoAlteraQuantidadeDeInteressados() {
        var reserva = new Reserva(criarExemplarValido());
        reserva.registrarInteresse(criarUsuario("U-001"));

        assertThrows(IllegalArgumentException.class, () -> reserva.registrarInteresse(null));

        assertEquals(1, reserva.quantidadeInteressados());
    }

    // Comportamento da fila

    @Test
    void registrarInteresseAdicionaUsuarioEAumentaQuantidadeEmUm() {
        var reserva = new Reserva(criarExemplarValido());

        reserva.registrarInteresse(criarUsuario("U-001"));

        assertEquals(1, reserva.quantidadeInteressados());
    }

    @Test
    void proximoDaFilaRetornaPrimeiroUsuarioRegistradoSemRemoveLo() {
        var reserva = new Reserva(criarExemplarValido());
        var usuario1 = criarUsuario("U-001");
        reserva.registrarInteresse(usuario1);
        reserva.registrarInteresse(criarUsuario("U-002"));

        var primeiro = reserva.proximoDaFila();

        assertSame(usuario1, primeiro);
        assertEquals(2, reserva.quantidadeInteressados());
    }

    @Test
    void chamadasRepetidasAProximoDaFilaRetornamMesmoUsuarioEnquantoNaoForRemovido() {
        var reserva = new Reserva(criarExemplarValido());
        var usuario1 = criarUsuario("U-001");
        reserva.registrarInteresse(usuario1);
        reserva.registrarInteresse(criarUsuario("U-002"));

        assertSame(usuario1, reserva.proximoDaFila());
        assertSame(usuario1, reserva.proximoDaFila());
    }

    @Test
    void removerProximoRetornaUsuariosNaOrdemDeRegistroFIFO() {
        var reserva = new Reserva(criarExemplarValido());
        var usuario1 = criarUsuario("U-001");
        var usuario2 = criarUsuario("U-002");
        var usuario3 = criarUsuario("U-003");
        reserva.registrarInteresse(usuario1);
        reserva.registrarInteresse(usuario2);
        reserva.registrarInteresse(usuario3);

        assertSame(usuario1, reserva.removerProximo());
        assertSame(usuario2, reserva.removerProximo());
        assertSame(usuario3, reserva.removerProximo());
    }

    @Test
    void removerProximoRemoveUsuarioEReduzQuantidadeEmUm() {
        var reserva = new Reserva(criarExemplarValido());
        reserva.registrarInteresse(criarUsuario("U-001"));
        reserva.registrarInteresse(criarUsuario("U-002"));

        reserva.removerProximo();

        assertEquals(1, reserva.quantidadeInteressados());
    }

    @Test
    void aposRemoverTodosOsInteressadosPossuiInteressadosRetornaFalso() {
        var reserva = new Reserva(criarExemplarValido());
        reserva.registrarInteresse(criarUsuario("U-001"));

        reserva.removerProximo();

        assertFalse(reserva.possuiInteressados());
    }

    @Test
    void estaInteressadoRetornaVerdadeiroParaUsuarioAtualmenteNaFila() {
        var reserva = new Reserva(criarExemplarValido());
        var usuario = criarUsuario("U-001");
        reserva.registrarInteresse(usuario);

        assertTrue(reserva.estaInteressado(usuario));
    }

    @Test
    void estaInteressadoRetornaFalsoParaUsuarioNuncaRegistrado() {
        var reserva = new Reserva(criarExemplarValido());
        reserva.registrarInteresse(criarUsuario("U-001"));

        assertFalse(reserva.estaInteressado(criarUsuario("U-002")));
    }

    @Test
    void aposUsuarioSerRemovidoDaFilaEstaInteressadoRetornaFalsoParaEle() {
        var reserva = new Reserva(criarExemplarValido());
        var usuario = criarUsuario("U-001");
        reserva.registrarInteresse(usuario);

        reserva.removerProximo();

        assertFalse(reserva.estaInteressado(usuario));
    }

    @Test
    void registrarNovoInteresseAposRemoverPrimeiroDaFilaFuncionaNormalmente() {
        var reserva = new Reserva(criarExemplarValido());
        reserva.registrarInteresse(criarUsuario("U-001"));
        reserva.removerProximo();

        var usuario2 = criarUsuario("U-002");
        reserva.registrarInteresse(usuario2);

        assertEquals(1, reserva.quantidadeInteressados());
        assertSame(usuario2, reserva.proximoDaFila());
    }

    // Identidade

    @Test
    void duasReservasComMesmoExemplar_SaoIguais_MesmoComFilasDeInteressadosDiferentes() {
        var exemplar = criarExemplarValido();
        var reserva1 = new Reserva(exemplar);
        var reserva2 = new Reserva(exemplar);
        reserva1.registrarInteresse(criarUsuario("U-001"));

        assertEquals(reserva1, reserva2);
    }

    @Test
    void duasReservasComExemplaresDiferentes_NaoSaoIguais() {
        var reserva1 = new Reserva(criarExemplarValido());
        var reserva2 = new Reserva(new Exemplar("EX-002", criarLivroValido()));

        assertNotEquals(reserva1, reserva2);
    }

    @Test
    void reservasIguaisPossuemMesmoHashCode() {
        var exemplar = criarExemplarValido();
        var reserva1 = new Reserva(exemplar);
        var reserva2 = new Reserva(exemplar);

        assertEquals(reserva1.hashCode(), reserva2.hashCode());
    }

    @Test
    void reservaNaoEhIgualANull() {
        var reserva = new Reserva(criarExemplarValido());

        assertNotEquals(reserva, null);
        assertNotEquals(null, reserva);
    }

    @Test
    void reservaNaoEhIgualAOutroTipo() {
        var reserva = new Reserva(criarExemplarValido());
        var string = "EX-001";

        assertNotEquals(reserva, string);
        assertNotEquals(string, reserva);
    }

    @Test
    void toStringContemInformacoesUteisSemExporFilaDiretamente() {
        var reserva = new Reserva(criarExemplarValido());
        reserva.registrarInteresse(criarUsuario("U-001"));
        reserva.registrarInteresse(criarUsuario("U-002"));

        var toString = reserva.toString();

        assertTrue(toString.contains("EX-001"), "toString deve conter código do exemplar");
        assertTrue(toString.contains("2"), "toString deve conter a quantidade de interessados");
        assertFalse(toString.contains("U-001"), "toString não deve expor a fila diretamente");
    }
}
