# Diagrama de Classes UML — Sistema de Biblioteca

Fonte de verdade: gerado a partir do código em `origin/main` em 2026-07-12
(commit `d488a59`, após integração da persistência/exceções do Matheus e da
camada de aplicação/interface do P3).

Pendência conhecida de integração: `EstadoExemplar` (P1) ainda lança
`UnsupportedOperationException` como placeholder nas transições inválidas —
`ExemplarIndisponivelException`/`EstadoInvalidoException` ainda não estão
efetivamente conectadas ali (ver TODOs no arquivo). `BibliotecaServico` já foi
escrito assumindo essa conexão (declara `throws ExemplarIndisponivelException`),
então nenhuma mudança será necessária neste pacote quando isso for corrigido.

```mermaid
classDiagram
    %% ===== Núcleo do domínio (P1) =====
    class Livro {
        -isbn: String
        -titulo: String
        -autor: String
        -editora: String
        -anoPublicacao: int
        +isbn() String
        +titulo() String
        +autor() String
        +editora() String
        +anoPublicacao() int
    }

    class Exemplar {
        -codigo: String
        -livro: Livro
        -estado: EstadoExemplar
        +estadoAtual() String
        +emprestar() void
        +devolver() void
        +devolverParaReserva() void
        +retirarReserva() void
        +cancelarReserva() void
    }

    class EstadoExemplar {
        <<interface>>
        +emprestar() EstadoExemplar
        +devolver() EstadoExemplar
        +devolverParaReserva() EstadoExemplar
        +retirarReserva() EstadoExemplar
        +cancelarReserva() EstadoExemplar
        +nome() String
    }

    class EstadoDisponivel {
        +emprestar() EstadoExemplar
        +nome() String
    }

    class EstadoEmprestado {
        +devolver() EstadoExemplar
        +devolverParaReserva() EstadoExemplar
        +nome() String
    }

    class EstadoReservado {
        +retirarReserva() EstadoExemplar
        +cancelarReserva() EstadoExemplar
        +nome() String
    }

    Exemplar "many" --> "1" Livro : associação
    Exemplar *-- EstadoExemplar : composição (estado não existe sem o exemplar)
    EstadoDisponivel ..|> EstadoExemplar : realização
    EstadoEmprestado ..|> EstadoExemplar : realização
    EstadoReservado ..|> EstadoExemplar : realização

    %% ===== Políticas de negócio e usuários (P2) =====
    class Usuario {
        -codigo: String
        -nome: String
        -email: String
        -politicaEmprestimo: PoliticaEmprestimo
        -notificacoes: List~String~
        +atingiuLimiteEmprestimos(int) boolean
        +prazoEmprestimoEmDias() int
        +calcularMulta(int) BigDecimal
        +notificarDisponibilidade(Exemplar) void
    }

    class PoliticaEmprestimo {
        <<interface>>
        +limiteEmprestimos() int
        +prazoEmprestimoEmDias() int
        +calcularMulta(int) BigDecimal
    }

    class PoliticaEmprestimoPadrao {
        +limiteEmprestimos() int
        +prazoEmprestimoEmDias() int
        +calcularMulta(int) BigDecimal
    }

    class ObservadorReserva {
        <<interface>>
        +notificarDisponibilidade(Exemplar) void
    }

    class Emprestimo {
        -codigo: String
        -usuario: Usuario
        -exemplar: Exemplar
        -dataEmprestimo: LocalDate
        -dataPrevistaDevolucao: LocalDate
        -dataDevolucao: LocalDate
        +estaAtivo() boolean
        +registrarDevolucao(LocalDate) void
        +estaAtrasado(LocalDate) boolean
        +diasDeAtraso(LocalDate) int
        +calcularMulta(LocalDate) BigDecimal
    }

    class Reserva {
        -exemplar: Exemplar
        -interessados: Deque~Usuario~
        +registrarInteresse(Usuario) void
        +proximoDaFila() Usuario
        +removerProximo() Usuario
        +notificarProximoDaFila() void
        +possuiInteressados() boolean
    }

    Usuario ..|> ObservadorReserva : realização
    Usuario o-- PoliticaEmprestimo : agregação (política pode ser compartilhada entre usuários do mesmo tipo)
    PoliticaEmprestimoPadrao ..|> PoliticaEmprestimo : realização
    Emprestimo --> Usuario : associação
    Emprestimo --> Exemplar : associação
    Reserva --> Exemplar : associação
    Reserva o-- Usuario : agregação (fila de interessados)
    Reserva ..> ObservadorReserva : notifica (Observer)

    %% ===== Persistência e exceções (Matheus) =====
    class RepositorioGenerico~T,ID~ {
        <<interface>>
        +salvar(T) T
        +buscarPorId(ID) Optional~T~
        +listarTodos() List~T~
        +remover(ID) boolean
        +existe(ID) boolean
        +quantidade() long
    }

    class RepositorioEmMemoria~T,ID~ {
        -registros: Map~ID,T~
        -extratorId: Function~T,ID~
    }

    class ExemplarIndisponivelException {
        <<checked>>
        -codigoExemplar: String
    }

    class EstadoInvalidoException {
        <<unchecked>>
        -estadoAtual: String
        -operacao: String
    }

    RepositorioEmMemoria ..|> RepositorioGenerico : realização

    %% ===== Camada de aplicação/interface (P3 — este pacote) =====
    class BibliotecaServico {
        -livros: RepositorioGenerico~Livro,String~
        -exemplares: RepositorioGenerico~Exemplar,String~
        -usuarios: RepositorioGenerico~Usuario,String~
        -emprestimos: RepositorioGenerico~Emprestimo,String~
        -reservas: RepositorioGenerico~Reserva,String~
        +cadastrarLivro(...) Livro
        +cadastrarExemplar(String, String) Exemplar
        +cadastrarUsuario(...) Usuario
        +emprestar(String, String, LocalDate) Emprestimo
        +devolver(String, LocalDate) void
        +reservar(String, String) void
        +consultarMulta(String, LocalDate) BigDecimal
    }

    class MenuCLI {
        -servico: BibliotecaServico
        +executar() void
    }

    class Main {
        <<composition root>>
        +main(String[]) void
    }

    BibliotecaServico o-- RepositorioGenerico : agregação (injetado via construtor, DIP)
    BibliotecaServico ..> ExemplarIndisponivelException : lança
    BibliotecaServico ..> Livro
    BibliotecaServico ..> Exemplar
    BibliotecaServico ..> Usuario
    BibliotecaServico ..> Emprestimo
    BibliotecaServico ..> Reserva
    MenuCLI --> BibliotecaServico : usa
    MenuCLI ..> EstadoInvalidoException : captura
    Main ..> RepositorioEmMemoria : instancia (única classe que conhece a infra concreta)
    Main ..> BibliotecaServico : instancia
    Main ..> MenuCLI : instancia
```

## Notas de leitura

- **Composição** (`Exemplar *-- EstadoExemplar`): o estado não tem sentido nem
  ciclo de vida fora do exemplar ao qual pertence — quando o exemplar some, o
  estado some junto.
- **Agregação** (`Usuario o-- PoliticaEmprestimo`, `Reserva o-- Usuario`): a
  política pode ser um singleton compartilhado por todos os usuários do mesmo
  tipo (aluno/professor/comunidade), e um `Usuario` numa fila de reserva
  continua existindo e sendo usado em outros contextos independente da
  `Reserva`.
- **Associação simples** (`Exemplar --> Livro`, `Emprestimo --> Usuario`,
  `Emprestimo --> Exemplar`, `Reserva --> Exemplar`): referência sem posse de
  ciclo de vida.
- **Realização** (linhas tracejadas com seta vazada `..|>`): toda
  implementação de contrato (State, Strategy, Observer) aparece aqui — é o
  mecanismo central de Late Binding que a defesa oral deve explicar.
- **Agregação** (`BibliotecaServico o-- RepositorioGenerico`): os repositórios
  são injetados via construtor (Inversão de Dependência) — `BibliotecaServico`
  não cria nem é dono do ciclo de vida deles.
- `Main` é a raiz de composição: é a única classe de todo o sistema que
  conhece `RepositorioEmMemoria` (implementação concreta). Trocar por
  persistência em arquivo/BD significa mudar apenas esta classe.
