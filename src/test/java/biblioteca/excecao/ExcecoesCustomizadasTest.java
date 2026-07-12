package biblioteca.excecao;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExcecoesCustomizadasTest {

    // ---------- EstadoInvalidoException (unchecked, contrato) ----------

    @Test
    void estadoInvalidoDeveSerUnchecked() {
        assertTrue(RuntimeException.class.isAssignableFrom(EstadoInvalidoException.class),
                "EstadoInvalidoException deve estender RuntimeException (unchecked)");
    }

    @Test
    void estadoInvalidoDeveGuardarEstadoEOperacao() {
        var e = new EstadoInvalidoException("Disponível", "devolver");

        assertEquals("Disponível", e.estadoAtual());
        assertEquals("devolver", e.operacao());
    }

    @Test
    void estadoInvalidoDeveTerMensagemComEstadoEOperacao() {
        var e = new EstadoInvalidoException("Disponível", "devolver");

        assertTrue(e.getMessage().contains("Disponível"));
        assertTrue(e.getMessage().contains("devolver"));
    }

    // ---------- ExemplarIndisponivelException (checked, recuperavel) ----------

    @Test
    void exemplarIndisponivelDeveSerChecked() {
        assertTrue(Exception.class.isAssignableFrom(ExemplarIndisponivelException.class),
                "deve estender Exception");
        assertFalse(RuntimeException.class.isAssignableFrom(ExemplarIndisponivelException.class),
                "nao pode ser subclasse de RuntimeException (precisa ser checked)");
    }

    @Test
    void exemplarIndisponivelDeveGuardarCodigo() {
        var e = new ExemplarIndisponivelException("EX-001");

        assertEquals("EX-001", e.codigoExemplar());
    }

    @Test
    void exemplarIndisponivelDeveTerMensagemComCodigo() {
        var e = new ExemplarIndisponivelException("EX-001");

        assertTrue(e.getMessage().contains("EX-001"));
    }

    @Test
    void exemplarIndisponivelDevePreservarCausaEncadeada() {
        var causa = new EstadoInvalidoException("Emprestado", "emprestar");
        var e = new ExemplarIndisponivelException("EX-001", causa);

        assertSame(causa, e.getCause());
        assertEquals("EX-001", e.codigoExemplar());
    }
}