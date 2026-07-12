package biblioteca.persistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Implementação in-memory de {@link RepositorioGenerico}, usando um
 * {@link Map} indexado pelo id da entidade.
 *
 * <p>O identificador de cada entidade é obtido por um <b>extrator</b>
 * ({@code Function<T, ID>}) recebido no construtor. Essa escolha permite que o
 * repositório funcione com qualquer entidade — {@code Livro}, {@code Exemplar},
 * {@code Usuario}, {@code Emprestimo} — <b>sem exigir que elas implementem uma
 * interface comum</b>, mantendo baixo acoplamento com o núcleo de domínio.</p>
 *
 * <p>Exemplo de uso:
 * <pre>{@code
 * var repoLivros = new RepositorioEmMemoria<Livro, String>(Livro::isbn);
 * var repoExemplares = new RepositorioEmMemoria<Exemplar, String>(Exemplar::codigo);
 * }</pre></p>
 */
public class RepositorioEmMemoria<T, ID> implements RepositorioGenerico<T, ID> {

    private final Map<ID, T> registros = new ConcurrentHashMap<>();
    private final Function<T, ID> extratorId;

    public RepositorioEmMemoria(Function<T, ID> extratorId) {
        this.extratorId = Objects.requireNonNull(extratorId, "extratorId não pode ser nulo");
    }

    @Override
    public T salvar(T entidade) {
        Objects.requireNonNull(entidade, "entidade não pode ser nula");
        ID id = extratorId.apply(entidade);
        Objects.requireNonNull(id, "id extraído da entidade não pode ser nulo");
        registros.put(id, entidade);
        return entidade;
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        Objects.requireNonNull(id, "id não pode ser nulo");
        return Optional.ofNullable(registros.get(id));
    }

    @Override
    public List<T> listarTodos() {
        return new ArrayList<>(registros.values());
    }

    @Override
    public boolean remover(ID id) {
        Objects.requireNonNull(id, "id não pode ser nulo");
        return registros.remove(id) != null;
    }

    @Override
    public boolean existe(ID id) {
        Objects.requireNonNull(id, "id não pode ser nulo");
        return registros.containsKey(id);
    }

    @Override
    public long quantidade() {
        return registros.size();
    }
}