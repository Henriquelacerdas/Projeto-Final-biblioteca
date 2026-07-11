package biblioteca.model.estado;

public final class EstadoDisponivel implements EstadoExemplar {

    @Override
    public String nome() {
        return "Disponível";
    }
}
