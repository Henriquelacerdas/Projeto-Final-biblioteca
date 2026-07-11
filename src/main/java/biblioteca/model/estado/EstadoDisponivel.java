package biblioteca.model.estado;

public final class EstadoDisponivel implements EstadoExemplar {

    @Override
    public EstadoExemplar emprestar() {
        return new EstadoEmprestado();
    }

    @Override
    public String nome() {
        return "Disponível";
    }
}
