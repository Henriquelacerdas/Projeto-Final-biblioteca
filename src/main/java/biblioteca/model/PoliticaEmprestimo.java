package biblioteca.model;

import java.math.BigDecimal;

public interface PoliticaEmprestimo {

    int limiteEmprestimos();

    int prazoEmprestimoEmDias();

    BigDecimal calcularMulta(int diasAtraso);
}
