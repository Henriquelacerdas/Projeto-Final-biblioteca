package biblioteca.excecao;

/**
 * Exceção NÃO verificada (unchecked) que sinaliza uma VIOLAÇÃO DE CONTRATO:
 * a tentativa de executar uma transição de estado que o estado atual do
 * exemplar não permite (por exemplo, devolver um exemplar que está Disponível).
 *
 * <p>É unchecked (estende {@link RuntimeException}) porque representa um erro
 * de programação — o código chamou uma operação em um estado onde ela nunca
 * deveria ser chamada. Não é uma situação de negócio recuperável; é um bug a
 * ser corrigido. Por isso não obrigamos o chamador a declarar {@code throws}.</p>
 *
 * <p>Contraste com {@link ExemplarIndisponivelException}, que é checked porque
 * modela uma situação de negócio esperada e recuperável.</p>
 */
public class EstadoInvalidoException extends RuntimeException {

    private final String estadoAtual;
    private final String operacao;

    public EstadoInvalidoException(String estadoAtual, String operacao) {
        super("Operação '" + operacao + "' não é permitida no estado '" + estadoAtual + "'");
        this.estadoAtual = estadoAtual;
        this.operacao = operacao;
    }

    /** Nome do estado em que a operação inválida foi tentada. */
    public String estadoAtual() {
        return estadoAtual;
    }

    /** Nome da operação que foi indevidamente solicitada. */
    public String operacao() {
        return operacao;
    }
}