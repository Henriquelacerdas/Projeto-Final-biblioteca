package biblioteca.model;

import biblioteca.model.estado.EstadoDisponivel;
import biblioteca.model.estado.EstadoExemplar;

import java.util.Objects;

public final class Exemplar {

    private final String codigo;
    private final Livro livro;
    private EstadoExemplar estado;

    public Exemplar(String codigo, Livro livro) {
        if (codigo == null || codigo.strip().isEmpty()) {
            throw new IllegalArgumentException("codigo não pode ser nulo ou vazio");
        }
        if (livro == null) {
            throw new IllegalArgumentException("livro não pode ser nulo");
        }
        this.codigo = codigo;
        this.livro = livro;
        this.estado = new EstadoDisponivel();
    }

    public String codigo() {
        return codigo;
    }

    public Livro livro() {
        return livro;
    }

    public String estadoAtual() {
        return estado.nome();
    }

    public void emprestar() {
        estado = estado.emprestar();
    }

    public void devolver() {
        estado = estado.devolver();
    }

    public void devolverParaReserva() {
        estado = estado.devolverParaReserva();
    }

    public void retirarReserva() {
        estado = estado.retirarReserva();
    }

    public void cancelarReserva() {
        estado = estado.cancelarReserva();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Exemplar)) {
            return false;
        }
        Exemplar outro = (Exemplar) o;
        return codigo.equals(outro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Exemplar{" +
                "codigo='" + codigo + '\'' +
                ", livro=" + livro +
                ", estado=" + estado.nome() +
                '}';
    }
}
