package biblioteca.model;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public final class Reserva {

    private final Exemplar exemplar;
    private final Deque<Usuario> interessados;

    public Reserva(Exemplar exemplar) {
        if (exemplar == null) {
            throw new IllegalArgumentException("exemplar não pode ser nulo");
        }
        this.exemplar = exemplar;
        this.interessados = new ArrayDeque<>();
    }

    public Exemplar exemplar() {
        return exemplar;
    }

    public void registrarInteresse(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("usuario não pode ser nulo");
        }
        if (interessados.contains(usuario)) {
            throw new IllegalStateException("usuario já registrou interesse nesta reserva");
        }
        interessados.addLast(usuario);
    }

    public Usuario proximoDaFila() {
        if (interessados.isEmpty()) {
            throw new IllegalStateException("não há interessados na fila");
        }
        return interessados.peekFirst();
    }

    public Usuario removerProximo() {
        if (interessados.isEmpty()) {
            throw new IllegalStateException("não há interessados na fila");
        }
        return interessados.removeFirst();
    }

    public boolean possuiInteressados() {
        return !interessados.isEmpty();
    }

    public int quantidadeInteressados() {
        return interessados.size();
    }

    public boolean estaInteressado(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("usuario não pode ser nulo");
        }
        return interessados.contains(usuario);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reserva)) {
            return false;
        }
        Reserva outra = (Reserva) o;
        return exemplar.equals(outra.exemplar);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exemplar);
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "exemplar='" + exemplar.codigo() + '\'' +
                ", interessados=" + interessados.size() +
                '}';
    }
}
