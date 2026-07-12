package biblioteca.excecao;

/**
 * Exceção VERIFICADA (checked) que sinaliza uma situação de NEGÓCIO esperada e
 * RECUPERÁVEL: o usuário tentou emprestar um exemplar que não está disponível
 * (já emprestado ou reservado por outra pessoa).
 *
 * <p>É checked (estende {@link Exception}) justamente porque não é um bug: é um
 * cenário normal do domínio que a camada de aplicação DEVE tratar — por exemplo,
 * oferecendo ao usuário a opção de entrar na fila de reserva. Ao ser checked, o
 * compilador obriga quem chama a operação de empréstimo a decidir conscientemente
 * o que fazer com essa possibilidade, em vez de ignorá-la silenciosamente.</p>
 *
 * <p>Contraste com {@link EstadoInvalidoException}, que é unchecked porque
 * representa violação de contrato (erro de programação), não fluxo de negócio.</p>
 */
public class ExemplarIndisponivelException extends Exception {

    private final String codigoExemplar;

    public ExemplarIndisponivelException(String codigoExemplar) {
        super("Exemplar '" + codigoExemplar + "' está indisponível para empréstimo");
        this.codigoExemplar = codigoExemplar;
    }

    public ExemplarIndisponivelException(String codigoExemplar, Throwable causa) {
        super("Exemplar '" + codigoExemplar + "' está indisponível para empréstimo", causa);
        this.codigoExemplar = codigoExemplar;
    }

    /** Código do exemplar que estava indisponível, útil para a mensagem ao usuário. */
    public String codigoExemplar() {
        return codigoExemplar;
    }
}