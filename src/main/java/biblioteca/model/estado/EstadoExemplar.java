package biblioteca.model.estado;

public interface EstadoExemplar {

    default EstadoExemplar emprestar() {
        // Implementar exceção ExemplarIndisponivelException aqui (exemplar indisponivel para emprestimo)
        throw new UnsupportedOperationException(
            "Operação inválida: emprestar não é permitido no estado " + nome());
    }

    default EstadoExemplar devolver() {
        // Implementar exceção EstadoInvalidoException aqui
        throw new UnsupportedOperationException(
            "Operação inválida: devolver não é permitido no estado " + nome());
    }

    default EstadoExemplar devolverParaReserva() {
        // implementar exceção EstadoInvalidoException aqui
        throw new UnsupportedOperationException(
            "Operação inválida: devolverParaReserva não é permitido no estado " + nome());
    }

    default EstadoExemplar retirarReserva() {
        // implementar exceção EstadoInvalidoException aqui
        throw new UnsupportedOperationException(
            "Operação inválida: retirarReserva não é permitido no estado " + nome());
    }

    default EstadoExemplar cancelarReserva() {
        // implementar exceção EstadoInvalidoException aqui
        throw new UnsupportedOperationException(
            "Operação inválida: cancelarReserva não é permitido no estado " + nome());
    }

    String nome();
}
