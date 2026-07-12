package biblioteca.persistencia;

import java.util.List;
import java.util.Optional;

/**
 * Abstração genérica de persistência (espaço da solução), parametrizada pelo
 * tipo da entidade {@code T} e pelo tipo do seu identificador {@code ID}.
 *
 * <p>Esta interface é o ponto de INVERSÃO DE DEPENDÊNCIA do sistema: o núcleo de
 * negócio e a camada de aplicação dependem apenas deste contrato, nunca de uma
 * implementação concreta (in-memory, arquivo, banco...). Trocar o mecanismo de
 * armazenamento não afeta ninguém que programe contra esta interface.</p>
 *
 * <p>O uso de generics ({@code <T, ID>}) garante segurança de tipo em tempo de
 * compilação, eliminando casts e o risco de {@link ClassCastException}.</p>
 */
public interface RepositorioGenerico<T, ID> {

    /** Salva (insere ou substitui) a entidade e a devolve. */
    T salvar(T entidade);

    /** Busca por id; {@link Optional#empty()} quando não existe (evita null). */
    Optional<T> buscarPorId(ID id);

    /** Lista todas as entidades armazenadas (cópia defensiva). */
    List<T> listarTodos();

    /** Remove pelo id; devolve {@code true} se havia algo para remover. */
    boolean remover(ID id);

    /** Indica se existe entidade com o id informado. */
    boolean existe(ID id);

    /** Quantidade de entidades armazenadas. */
    long quantidade();
}