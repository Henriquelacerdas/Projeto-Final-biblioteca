package biblioteca.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public final class Emprestimo {

    private final String codigo;
    private final Usuario usuario;
    private final Exemplar exemplar;
    private final LocalDate dataEmprestimo;
    private final LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucao;

    public Emprestimo(String codigo, Usuario usuario, Exemplar exemplar, LocalDate dataEmprestimo) {
        if (codigo == null || codigo.strip().isEmpty()) {
            throw new IllegalArgumentException("codigo não pode ser nulo ou vazio");
        }
        if (usuario == null) {
            throw new IllegalArgumentException("usuario não pode ser nulo");
        }
        if (exemplar == null) {
            throw new IllegalArgumentException("exemplar não pode ser nulo");
        }
        if (dataEmprestimo == null) {
            throw new IllegalArgumentException("dataEmprestimo não pode ser nula");
        }
        this.codigo = codigo;
        this.usuario = usuario;
        this.exemplar = exemplar;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataEmprestimo.plusDays(usuario.prazoEmprestimoEmDias());
    }

    public String codigo() {
        return codigo;
    }

    public Usuario usuario() {
        return usuario;
    }

    public Exemplar exemplar() {
        return exemplar;
    }

    public LocalDate dataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate dataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate dataDevolucao() {
        return dataDevolucao;
    }

    public boolean estaAtivo() {
        return dataDevolucao == null;
    }

    public void registrarDevolucao(LocalDate dataDevolucao) {
        if (dataDevolucao == null) {
            throw new IllegalArgumentException("dataDevolucao não pode ser nula");
        }
        if (dataDevolucao.isBefore(dataEmprestimo)) {
            throw new IllegalArgumentException("dataDevolucao não pode ser anterior à dataEmprestimo");
        }
        if (!estaAtivo()) {
            throw new IllegalStateException("empréstimo já foi devolvido");
        }
        this.dataDevolucao = dataDevolucao;
    }

    public boolean estaAtrasado(LocalDate dataReferencia) {
        return diasDeAtraso(dataReferencia) > 0;
    }

    public int diasDeAtraso(LocalDate dataReferencia) {
        if (dataReferencia == null) {
            throw new IllegalArgumentException("dataReferencia não pode ser nula");
        }
        if (dataReferencia.isBefore(dataEmprestimo)) {
            throw new IllegalArgumentException("dataReferencia não pode ser anterior à dataEmprestimo");
        }
        LocalDate dataBase = estaAtivo() ? dataReferencia : dataDevolucao;
        long dias = ChronoUnit.DAYS.between(dataPrevistaDevolucao, dataBase);
        return dias > 0 ? (int) dias : 0;
    }

    public BigDecimal calcularMulta(LocalDate dataReferencia) {
        return usuario.calcularMulta(diasDeAtraso(dataReferencia));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Emprestimo)) {
            return false;
        }
        Emprestimo outro = (Emprestimo) o;
        return codigo.equals(outro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Emprestimo{" +
                "codigo='" + codigo + '\'' +
                ", usuario=" + usuario.codigo() +
                ", exemplar=" + exemplar.codigo() +
                ", dataEmprestimo=" + dataEmprestimo +
                ", dataPrevistaDevolucao=" + dataPrevistaDevolucao +
                ", dataDevolucao=" + dataDevolucao +
                '}';
    }
}
