package biblioteca.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {

    private static final LocalDate DATA_EMPRESTIMO = LocalDate.of(2026, 1, 1);
    private static final LocalDate DATA_PREVISTA_DEVOLUCAO = LocalDate.of(2026, 1, 15);

    private Livro criarLivroValido() {
        return new Livro("978-8532530786", "Clean Code", "Robert C. Martin", "Prentice Hall", 2008);
    }

    private Exemplar criarExemplarValido() {
        return new Exemplar("EX-001", criarLivroValido());
    }

    private Usuario criarUsuarioValido() {
        return new Usuario("U-001", "Maria Silva", "maria.silva@exemplo.com", new PoliticaEmprestimoPadrao());
    }

    private Emprestimo criarEmprestimoValido() {
        return new Emprestimo("EMP-001", criarUsuarioValido(), criarExemplarValido(), DATA_EMPRESTIMO);
    }

    // Construção

    @Test
    void deveCriarEmprestimoValidoCorretamente() {
        var emprestimo = criarEmprestimoValido();

        assertNotNull(emprestimo);
    }

    @Test
    void dataPrevistaDevolucaoEhCatorzeDiasAposDataEmprestimo() {
        var emprestimo = criarEmprestimoValido();

        assertEquals(DATA_PREVISTA_DEVOLUCAO, emprestimo.dataPrevistaDevolucao());
    }

    @Test
    void novoEmprestimoEstaAtivo() {
        var emprestimo = criarEmprestimoValido();

        assertTrue(emprestimo.estaAtivo());
    }

    @Test
    void dataDevolucaoComecaNula() {
        var emprestimo = criarEmprestimoValido();

        assertNull(emprestimo.dataDevolucao());
    }

    @Test
    void metodosDeConsultaRetornamObjetosEValoresRecebidosNoConstrutor() {
        var usuario = criarUsuarioValido();
        var exemplar = criarExemplarValido();
        var emprestimo = new Emprestimo("EMP-001", usuario, exemplar, DATA_EMPRESTIMO);

        assertEquals("EMP-001", emprestimo.codigo());
        assertSame(usuario, emprestimo.usuario());
        assertSame(exemplar, emprestimo.exemplar());
        assertEquals(DATA_EMPRESTIMO, emprestimo.dataEmprestimo());
    }

    // Validação de construção

    @Test
    void deveRejeitar_CodigoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Emprestimo(null, criarUsuarioValido(), criarExemplarValido(), DATA_EMPRESTIMO));
    }

    @Test
    void deveRejeitar_CodigoVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> new Emprestimo("", criarUsuarioValido(), criarExemplarValido(), DATA_EMPRESTIMO));
    }

    @Test
    void deveRejeitar_CodigoComApenasEspacos() {
        assertThrows(IllegalArgumentException.class,
                () -> new Emprestimo("   ", criarUsuarioValido(), criarExemplarValido(), DATA_EMPRESTIMO));
    }

    @Test
    void deveRejeitar_UsuarioNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Emprestimo("EMP-001", null, criarExemplarValido(), DATA_EMPRESTIMO));
    }

    @Test
    void deveRejeitar_ExemplarNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Emprestimo("EMP-001", criarUsuarioValido(), null, DATA_EMPRESTIMO));
    }

    @Test
    void deveRejeitar_DataEmprestimoNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Emprestimo("EMP-001", criarUsuarioValido(), criarExemplarValido(), null));
    }

    @Test
    void deveRejeitar_DataReferenciaNulaEmDiasDeAtraso() {
        var emprestimo = criarEmprestimoValido();

        assertThrows(IllegalArgumentException.class, () -> emprestimo.diasDeAtraso(null));
    }

    @Test
    void deveRejeitar_DataReferenciaAnteriorADataEmprestimo() {
        var emprestimo = criarEmprestimoValido();

        assertThrows(IllegalArgumentException.class,
                () -> emprestimo.diasDeAtraso(DATA_EMPRESTIMO.minusDays(1)));
    }

    @Test
    void deveRejeitar_DataDevolucaoNula() {
        var emprestimo = criarEmprestimoValido();

        assertThrows(IllegalArgumentException.class, () -> emprestimo.registrarDevolucao(null));
    }

    @Test
    void deveRejeitar_DataDevolucaoAnteriorADataEmprestimo() {
        var emprestimo = criarEmprestimoValido();

        assertThrows(IllegalArgumentException.class,
                () -> emprestimo.registrarDevolucao(DATA_EMPRESTIMO.minusDays(1)));
    }

    // Devolução

    @Test
    void registrarDevolucaoTornaEmprestimoInativo() {
        var emprestimo = criarEmprestimoValido();

        emprestimo.registrarDevolucao(DATA_PREVISTA_DEVOLUCAO);

        assertFalse(emprestimo.estaAtivo());
    }

    @Test
    void dataDevolucaoRegistradaPodeSerConsultada() {
        var emprestimo = criarEmprestimoValido();
        var dataDevolucao = DATA_PREVISTA_DEVOLUCAO;

        emprestimo.registrarDevolucao(dataDevolucao);

        assertEquals(dataDevolucao, emprestimo.dataDevolucao());
    }

    @Test
    void registrarSegundaDevolucaoLancaIllegalStateException() {
        var emprestimo = criarEmprestimoValido();
        emprestimo.registrarDevolucao(DATA_PREVISTA_DEVOLUCAO);

        assertThrows(IllegalStateException.class,
                () -> emprestimo.registrarDevolucao(DATA_PREVISTA_DEVOLUCAO.plusDays(1)));
    }

    @Test
    void tentativaInvalidaDeDevolucaoNaoAlteraEstadoAnterior() {
        var emprestimo = criarEmprestimoValido();

        assertThrows(IllegalArgumentException.class,
                () -> emprestimo.registrarDevolucao(DATA_EMPRESTIMO.minusDays(1)));

        assertTrue(emprestimo.estaAtivo());
        assertNull(emprestimo.dataDevolucao());
    }

    // Atraso: empréstimo ativo

    @Test
    void ativo_referenciaAnteriorADataPrevista_zeroDiasDeAtraso() {
        var emprestimo = criarEmprestimoValido();

        assertEquals(0, emprestimo.diasDeAtraso(DATA_PREVISTA_DEVOLUCAO.minusDays(1)));
    }

    @Test
    void ativo_referenciaNaDataPrevista_zeroDiasDeAtraso() {
        var emprestimo = criarEmprestimoValido();

        assertEquals(0, emprestimo.diasDeAtraso(DATA_PREVISTA_DEVOLUCAO));
    }

    @Test
    void ativo_referenciaUmDiaAposDataPrevista_umDiaDeAtraso() {
        var emprestimo = criarEmprestimoValido();

        assertEquals(1, emprestimo.diasDeAtraso(DATA_PREVISTA_DEVOLUCAO.plusDays(1)));
    }

    @Test
    void ativo_referenciaCincoDiasAposDataPrevista_cincoDiasDeAtraso() {
        var emprestimo = criarEmprestimoValido();

        assertEquals(5, emprestimo.diasDeAtraso(DATA_PREVISTA_DEVOLUCAO.plusDays(5)));
    }

    @Test
    void ativo_estaAtrasadoRetornaFalsoQuandoNaoHaAtraso() {
        var emprestimo = criarEmprestimoValido();

        assertFalse(emprestimo.estaAtrasado(DATA_PREVISTA_DEVOLUCAO));
    }

    @Test
    void ativo_estaAtrasadoRetornaVerdadeiroQuandoHaAtraso() {
        var emprestimo = criarEmprestimoValido();

        assertTrue(emprestimo.estaAtrasado(DATA_PREVISTA_DEVOLUCAO.plusDays(1)));
    }

    @Test
    void ativo_calcularMultaRetornaZeroQuandoNaoHaAtraso() {
        var emprestimo = criarEmprestimoValido();

        assertEquals(BigDecimal.ZERO, emprestimo.calcularMulta(DATA_PREVISTA_DEVOLUCAO));
    }

    @Test
    void ativo_calcularMultaRetornaUmRealParaUmDiaDeAtraso() {
        var emprestimo = criarEmprestimoValido();

        assertEquals(0, new BigDecimal("1.00")
                .compareTo(emprestimo.calcularMulta(DATA_PREVISTA_DEVOLUCAO.plusDays(1))));
    }

    @Test
    void ativo_calcularMultaRetornaCincoReaisParaCincoDiasDeAtraso() {
        var emprestimo = criarEmprestimoValido();

        assertEquals(0, new BigDecimal("5.00")
                .compareTo(emprestimo.calcularMulta(DATA_PREVISTA_DEVOLUCAO.plusDays(5))));
    }

    // Atraso:  empréstimo devolvido

    @Test
    void devolvido_devolucaoAntesDaDataPrevista_naoGeraAtraso() {
        var emprestimo = criarEmprestimoValido();
        emprestimo.registrarDevolucao(DATA_PREVISTA_DEVOLUCAO.minusDays(1));

        assertEquals(0, emprestimo.diasDeAtraso(DATA_PREVISTA_DEVOLUCAO.plusDays(10)));
    }

    @Test
    void devolvido_devolucaoNaDataPrevista_naoGeraAtraso() {
        var emprestimo = criarEmprestimoValido();
        emprestimo.registrarDevolucao(DATA_PREVISTA_DEVOLUCAO);

        assertEquals(0, emprestimo.diasDeAtraso(DATA_PREVISTA_DEVOLUCAO.plusDays(10)));
    }

    @Test
    void devolvido_devolucaoAposDataPrevista_calculaDiasDeAtrasoCorretamente() {
        var emprestimo = criarEmprestimoValido();
        emprestimo.registrarDevolucao(DATA_PREVISTA_DEVOLUCAO.plusDays(3));

        assertEquals(3, emprestimo.diasDeAtraso(DATA_PREVISTA_DEVOLUCAO.plusDays(10)));
    }

    @Test
    void devolvido_dataReferenciaPosteriorADevolucao_naoAumentaDiasDeAtraso() {
        var emprestimo = criarEmprestimoValido();
        emprestimo.registrarDevolucao(DATA_PREVISTA_DEVOLUCAO.plusDays(3));

        var diasComReferenciaProxima = emprestimo.diasDeAtraso(DATA_PREVISTA_DEVOLUCAO.plusDays(4));
        var diasComReferenciaDistante = emprestimo.diasDeAtraso(DATA_PREVISTA_DEVOLUCAO.plusDays(100));

        assertEquals(diasComReferenciaProxima, diasComReferenciaDistante);
    }

    @Test
    void devolvido_multaPermaneceBaseadaNaDataEfetivaDaDevolucao() {
        var emprestimo = criarEmprestimoValido();
        emprestimo.registrarDevolucao(DATA_PREVISTA_DEVOLUCAO.plusDays(3));

        assertEquals(0, new BigDecimal("3.00")
                .compareTo(emprestimo.calcularMulta(DATA_PREVISTA_DEVOLUCAO.plusDays(100))));
    }

    // Identidade

    @Test
    void doisEmprestimosComMesmoCodigo_SaoIguais_MesmoComUsuarioOuExemplarDiferentes() {
        var emprestimo1 = new Emprestimo("EMP-001", criarUsuarioValido(), criarExemplarValido(), DATA_EMPRESTIMO);
        var outroUsuario = new Usuario("U-002", "Outro Usuario", "outro@exemplo.com", new PoliticaEmprestimoPadrao());
        var outroExemplar = new Exemplar("EX-002", criarLivroValido());
        var emprestimo2 = new Emprestimo("EMP-001", outroUsuario, outroExemplar, DATA_EMPRESTIMO);

        assertEquals(emprestimo1, emprestimo2);
    }

    @Test
    void doisEmprestimosComCodigosDiferentes_NaoSaoIguais() {
        var emprestimo1 = new Emprestimo("EMP-001", criarUsuarioValido(), criarExemplarValido(), DATA_EMPRESTIMO);
        var emprestimo2 = new Emprestimo("EMP-002", criarUsuarioValido(), criarExemplarValido(), DATA_EMPRESTIMO);

        assertNotEquals(emprestimo1, emprestimo2);
    }

    @Test
    void emprestimosIguaisPossuemMesmoHashCode() {
        var emprestimo1 = new Emprestimo("EMP-001", criarUsuarioValido(), criarExemplarValido(), DATA_EMPRESTIMO);
        var emprestimo2 = new Emprestimo("EMP-001", criarUsuarioValido(), criarExemplarValido(), DATA_EMPRESTIMO);

        assertEquals(emprestimo1.hashCode(), emprestimo2.hashCode());
    }

    @Test
    void emprestimoNaoEhIgualANull() {
        var emprestimo = criarEmprestimoValido();

        assertNotEquals(emprestimo, null);
        assertNotEquals(null, emprestimo);
    }

    @Test
    void emprestimoNaoEhIgualAOutroTipo() {
        var emprestimo = criarEmprestimoValido();
        var string = "EMP-001";

        assertNotEquals(emprestimo, string);
        assertNotEquals(string, emprestimo);
    }

    @Test
    void toStringContemInformacoesUteisParaIdentificarEmprestimo() {
        var emprestimo = criarEmprestimoValido();
        var toString = emprestimo.toString();

        assertTrue(toString.contains("EMP-001"), "toString deve conter código do empréstimo");
        assertTrue(toString.contains("U-001"), "toString deve conter código do usuário");
        assertTrue(toString.contains("EX-001"), "toString deve conter código do exemplar");
    }
}
