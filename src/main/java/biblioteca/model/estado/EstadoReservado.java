package biblioteca.model.estado;

public final class EstadoReservado implements EstadoExemplar {

    @Override
    public EstadoExemplar retirarReserva() {
        return new EstadoEmprestado();
    }

    @Override
    public EstadoExemplar cancelarReserva() {
        return new EstadoDisponivel();
    }

    @Override
    public String nome() {
        return "Reservado";
    }
}
