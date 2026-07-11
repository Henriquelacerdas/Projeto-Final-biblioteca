package biblioteca.model;

import java.math.BigDecimal;

public final class PoliticaEmprestimoPadrao implements PoliticaEmprestimo {

    private static final int LIMITE_EMPRESTIMOS = 3;
    private static final int PRAZO_EMPRESTIMO_EM_DIAS = 14;
    private static final BigDecimal MULTA_DIARIA = new BigDecimal("1.00");

    @Override
    public int limiteEmprestimos() {
        return LIMITE_EMPRESTIMOS;
    }

    @Override
    public int prazoEmprestimoEmDias() {
        return PRAZO_EMPRESTIMO_EM_DIAS;
    }

    @Override
    public BigDecimal calcularMulta(int diasAtraso) {
        if (diasAtraso < 0) {
            throw new IllegalArgumentException("diasAtraso não pode ser negativo");
        }
        if (diasAtraso == 0) {
            return BigDecimal.ZERO;
        }
        return MULTA_DIARIA.multiply(BigDecimal.valueOf(diasAtraso));
    }
}
