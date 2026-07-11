package biblioteca.model.estado;

public final class EstadoEmprestado implements EstadoExemplar {

    @Override
    public EstadoExemplar devolver() {
        return new EstadoDisponivel();
    }

    @Override
    public EstadoExemplar devolverParaReserva() {
        return new EstadoReservado();
    }

    @Override
    public String nome() {
        return "Emprestado";
    }
}
