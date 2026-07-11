package biblioteca.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {

    private PoliticaEmprestimo criarPolitica() {
        return new PoliticaEmprestimoPadrao();
    }

    private Usuario criarUsuarioValido() {
        return new Usuario("U-001", "Maria Silva", "maria.silva@exemplo.com", criarPolitica());
    }

    @Test
    void deveCriarUsuarioValidoCorretamente() {
        var usuario = criarUsuarioValido();

        assertNotNull(usuario);
    }

    @Test
    void deveRejeitar_CodigoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario(null, "Maria Silva", "maria.silva@exemplo.com", criarPolitica()));
    }

    @Test
    void deveRejeitar_CodigoVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("", "Maria Silva", "maria.silva@exemplo.com", criarPolitica()));
    }

    @Test
    void deveRejeitar_CodigoComApenasEspacos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("   ", "Maria Silva", "maria.silva@exemplo.com", criarPolitica()));
    }

    @Test
    void deveRejeitar_NomeNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("U-001", null, "maria.silva@exemplo.com", criarPolitica()));
    }

    @Test
    void deveRejeitar_NomeVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("U-001", "", "maria.silva@exemplo.com", criarPolitica()));
    }

    @Test
    void deveRejeitar_NomeComApenasEspacos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("U-001", "   ", "maria.silva@exemplo.com", criarPolitica()));
    }

    @Test
    void deveRejeitar_EmailNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("U-001", "Maria Silva", null, criarPolitica()));
    }

    @Test
    void deveRejeitar_EmailVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("U-001", "Maria Silva", "", criarPolitica()));
    }

    @Test
    void deveRejeitar_EmailComApenasEspacos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("U-001", "Maria Silva", "   ", criarPolitica()));
    }

    @Test
    void deveRejeitar_PoliticaEmprestimoNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Usuario("U-001", "Maria Silva", "maria.silva@exemplo.com", null));
    }

    @Test
    void gettersRetornamCodigoNomeEEmailCorretamente() {
        var usuario = criarUsuarioValido();

        assertEquals("U-001", usuario.codigo());
        assertEquals("Maria Silva", usuario.nome());
        assertEquals("maria.silva@exemplo.com", usuario.email());
    }

    @Test
    void prazoEmprestimoEmDiasRetornaCatorzePorDelegacao() {
        var usuario = criarUsuarioValido();

        assertEquals(14, usuario.prazoEmprestimoEmDias());
    }

    @Test
    void calcularMultaComZeroDiasRetornaZero() {
        var usuario = criarUsuarioValido();

        assertEquals(BigDecimal.ZERO, usuario.calcularMulta(0));
    }

    @Test
    void calcularMultaComCincoDiasRetornaCincoReais() {
        var usuario = criarUsuarioValido();

        assertEquals(0, new BigDecimal("5.00").compareTo(usuario.calcularMulta(5)));
    }

    @Test
    void calcularMultaRejeitaDiasNegativos() {
        var usuario = criarUsuarioValido();

        assertThrows(IllegalArgumentException.class, () -> usuario.calcularMulta(-1));
    }

    @Test
    void atingiuLimiteEmprestimosRetornaFalsoQuandoQuantidadeMenorQueLimite() {
        var usuario = criarUsuarioValido();

        assertFalse(usuario.atingiuLimiteEmprestimos(2));
    }

    @Test
    void atingiuLimiteEmprestimosRetornaVerdadeiroQuandoQuantidadeIgualAoLimite() {
        var usuario = criarUsuarioValido();

        assertTrue(usuario.atingiuLimiteEmprestimos(3));
    }

    @Test
    void atingiuLimiteEmprestimosRetornaVerdadeiroQuandoQuantidadeMaiorQueLimite() {
        var usuario = criarUsuarioValido();

        assertTrue(usuario.atingiuLimiteEmprestimos(4));
    }

    @Test
    void atingiuLimiteEmprestimosRejeitaQuantidadeNegativa() {
        var usuario = criarUsuarioValido();

        assertThrows(IllegalArgumentException.class, () -> usuario.atingiuLimiteEmprestimos(-1));
    }

    @Test
    void doisUsuariosComMesmoCodigo_SaoIguais_MesmoComNomeEmailOuPoliticaDiferentes() {
        var usuario1 = new Usuario("U-001", "Maria Silva", "maria.silva@exemplo.com", criarPolitica());
        var usuario2 = new Usuario("U-001", "Outro Nome", "outro@exemplo.com", criarPolitica());

        assertEquals(usuario1, usuario2);
    }

    @Test
    void doisUsuariosComCodigosDiferentes_NaoSaoIguais() {
        var usuario1 = new Usuario("U-001", "Maria Silva", "maria.silva@exemplo.com", criarPolitica());
        var usuario2 = new Usuario("U-002", "Maria Silva", "maria.silva@exemplo.com", criarPolitica());

        assertNotEquals(usuario1, usuario2);
    }

    @Test
    void usuariosIguaisPossuemMesmoHashCode() {
        var usuario1 = new Usuario("U-001", "Maria Silva", "maria.silva@exemplo.com", criarPolitica());
        var usuario2 = new Usuario("U-001", "Outro Nome", "outro@exemplo.com", criarPolitica());

        assertEquals(usuario1.hashCode(), usuario2.hashCode());
    }

    @Test
    void usuarioNaoEhIgualANull() {
        var usuario = criarUsuarioValido();

        assertNotEquals(usuario, null);
        assertNotEquals(null, usuario);
    }

    @Test
    void usuarioNaoEhIgualAOutroTipo() {
        var usuario = criarUsuarioValido();
        var string = "U-001";

        assertNotEquals(usuario, string);
        assertNotEquals(string, usuario);
    }

    @Test
    void toStringContemInformacoesUteisSemExporPolitica() {
        var usuario = criarUsuarioValido();
        var toString = usuario.toString();

        assertTrue(toString.contains("U-001"), "toString deve conter código");
        assertTrue(toString.contains("Maria Silva"), "toString deve conter nome");
        assertTrue(toString.contains("maria.silva@exemplo.com"), "toString deve conter email");
        assertFalse(toString.contains("Politica"), "toString não deve expor detalhes da política");
    }
}
