package biblioteca.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PoliticaEmprestimoPadraoTest {

    private PoliticaEmprestimoPadrao criarPolitica() {
        return new PoliticaEmprestimoPadrao();
    }

    @Test
    void limiteEmprestimosRetornaTres() {
        var politica = criarPolitica();

        assertEquals(3, politica.limiteEmprestimos());
    }

    @Test
    void prazoEmprestimoEmDiasRetornaCatorze() {
        var politica = criarPolitica();

        assertEquals(14, politica.prazoEmprestimoEmDias());
    }

    @Test
    void calcularMultaComZeroDiasDeAtrasoRetornaZero() {
        var politica = criarPolitica();

        assertEquals(BigDecimal.ZERO, politica.calcularMulta(0));
    }

    @Test
    void calcularMultaComUmDiaDeAtrasoRetornaUmReal() {
        var politica = criarPolitica();

        assertEquals(0, new BigDecimal("1.00").compareTo(politica.calcularMulta(1)));
    }

    @Test
    void calcularMultaComCincoDiasDeAtrasoRetornaCincoReais() {
        var politica = criarPolitica();

        assertEquals(0, new BigDecimal("5.00").compareTo(politica.calcularMulta(5)));
    }

    @Test
    void calcularMultaComQuantidadeMaiorDeDiasMantemCalculoCorreto() {
        var politica = criarPolitica();

        assertEquals(0, new BigDecimal("30.00").compareTo(politica.calcularMulta(30)));
    }

    @Test
    void calcularMultaComDiasNegativosLancaIllegalArgumentException() {
        var politica = criarPolitica();

        assertThrows(IllegalArgumentException.class, () -> politica.calcularMulta(-1));
    }

    @Test
    void chamadasRepetidasProduzemMesmoResultado() {
        var politica = criarPolitica();

        var primeiraChamada = politica.calcularMulta(5);
        var segundaChamada = politica.calcularMulta(5);

        assertEquals(0, primeiraChamada.compareTo(segundaChamada));
    }
}
