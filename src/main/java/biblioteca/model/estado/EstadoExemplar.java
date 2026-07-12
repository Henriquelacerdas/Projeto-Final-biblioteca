package biblioteca.model.estado;

import biblioteca.excecao.EstadoInvalidoException;

/**
 * Contrato do estado de um {@link biblioteca.model.Exemplar} (padrão State).
 *
 * <p>Cada transição tem uma implementação padrão que <b>rejeita</b> a operação
 * lançando {@link EstadoInvalidoException} (unchecked). Os estados concretos
 * sobrescrevem apenas as transições que são válidas a partir deles; qualquer
 * transição não sobrescrita cai na versão padrão e é recusada como violação de
 * contrato. Isso elimina cadeias de if/else: o polimorfismo do estado decide o
 * que é permitido.</p>
 */
public interface EstadoExemplar {

    default EstadoExemplar emprestar() {
        throw new EstadoInvalidoException(nome(), "emprestar");
    }

    default EstadoExemplar devolver() {
        throw new EstadoInvalidoException(nome(), "devolver");
    }

    default EstadoExemplar devolverParaReserva() {
        throw new EstadoInvalidoException(nome(), "devolverParaReserva");
    }

    default EstadoExemplar retirarReserva() {
        throw new EstadoInvalidoException(nome(), "retirarReserva");
    }

    default EstadoExemplar cancelarReserva() {
        throw new EstadoInvalidoException(nome(), "cancelarReserva");
    }

    String nome();
}