package biblioteca.model;

import java.util.Objects;

public final class Livro {

    private final String isbn;
    private final String titulo;
    private final String autor;
    private final String editora;
    private final int anoPublicacao;

    public Livro(String isbn, String titulo, String autor, String editora, int anoPublicacao) {
        this.isbn = validarTexto(isbn, "isbn");
        this.titulo = validarTexto(titulo, "titulo");
        this.autor = validarTexto(autor, "autor");
        this.editora = validarTexto(editora, "editora");

        if (anoPublicacao <= 0) {
            throw new IllegalArgumentException("anoPublicacao deve ser maior que zero");
        }
        this.anoPublicacao = anoPublicacao;
    }

    private static String validarTexto(String valor, String nomeAtributo) {
        if (valor == null || valor.strip().isEmpty()) {
            throw new IllegalArgumentException(nomeAtributo + " não pode ser nulo ou vazio");
        }
        return valor;
    }

    public String isbn() {
        return isbn;
    }

    public String titulo() {
        return titulo;
    }

    public String autor() {
        return autor;
    }

    public String editora() {
        return editora;
    }

    public int anoPublicacao() {
        return anoPublicacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Livro)) {
            return false;
        }
        Livro outro = (Livro) o;
        return isbn.equals(outro.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Livro{" +
                "isbn='" + isbn + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", editora='" + editora + '\'' +
                ", anoPublicacao=" + anoPublicacao +
                '}';
    }
}
