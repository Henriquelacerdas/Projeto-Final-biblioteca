package biblioteca.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Usuario implements ObservadorReserva {

    private final String codigo;
    private final String nome;
    private final String email;
    private final PoliticaEmprestimo politicaEmprestimo;
    private final List<String> notificacoes;

    public Usuario(String codigo, String nome, String email, PoliticaEmprestimo politicaEmprestimo) {
        this.codigo = validarTexto(codigo, "codigo");
        this.nome = validarTexto(nome, "nome");
        this.email = validarTexto(email, "email");

        if (politicaEmprestimo == null) {
            throw new IllegalArgumentException("politicaEmprestimo não pode ser nula");
        }
        this.politicaEmprestimo = politicaEmprestimo;
        this.notificacoes = new ArrayList<>();
    }

    private static String validarTexto(String valor, String nomeAtributo) {
        if (valor == null || valor.strip().isEmpty()) {
            throw new IllegalArgumentException(nomeAtributo + " não pode ser nulo ou vazio");
        }
        return valor;
    }

    public String codigo() {
        return codigo;
    }

    public String nome() {
        return nome;
    }

    public String email() {
        return email;
    }

    public boolean atingiuLimiteEmprestimos(int quantidadeEmprestimosAtivos) {
        if (quantidadeEmprestimosAtivos < 0) {
            throw new IllegalArgumentException("quantidadeEmprestimosAtivos não pode ser negativa");
        }
        return quantidadeEmprestimosAtivos >= politicaEmprestimo.limiteEmprestimos();
    }

    public int prazoEmprestimoEmDias() {
        return politicaEmprestimo.prazoEmprestimoEmDias();
    }

    public BigDecimal calcularMulta(int diasAtraso) {
        if (diasAtraso < 0) {
            throw new IllegalArgumentException("diasAtraso não pode ser negativo");
        }
        return politicaEmprestimo.calcularMulta(diasAtraso);
    }

    @Override
    public void notificarDisponibilidade(Exemplar exemplar) {
        if (exemplar == null) {
            throw new IllegalArgumentException("exemplar não pode ser nulo");
        }
        notificacoes.add(exemplar.codigo());
    }

    public boolean foiNotificadoSobre(Exemplar exemplar) {
        if (exemplar == null) {
            throw new IllegalArgumentException("exemplar não pode ser nulo");
        }
        return notificacoes.contains(exemplar.codigo());
    }

    public int quantidadeNotificacoes() {
        return notificacoes.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario)) {
            return false;
        }
        Usuario outro = (Usuario) o;
        return codigo.equals(outro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "codigo='" + codigo + '\'' +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
