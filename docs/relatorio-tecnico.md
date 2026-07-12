# Relatório Técnico de Arquitetura — Sistema de Biblioteca

> Rascunho/esqueleto (P3). Preencher e depois exportar para PDF antes da
> entrega de 22/06/2026. Seções marcadas `[TODO]` dependem de partes ainda não
> integradas por outros membros do grupo.

## Link do repositório

`[TODO: colar aqui o link público/compartilhado do repositório Git — deve
aparecer em destaque na primeira página do PDF final]`

## 1. Mapeamento do domínio

Domínio escolhido: **sistema de biblioteca** (empréstimos, reservas, exemplares).

| Entidade | Papel no domínio | Justificativa semântica |
|---|---|---|
| `Livro` | Representa o título/obra (metadados bibliográficos) | Separado de `Exemplar` porque um título pode ter N cópias físicas com ciclos de vida independentes — misturar os dois violaria coesão (dois motivos de mudança na mesma classe) |
| `Exemplar` | A cópia física, com estado próprio | Máquina de estados (State pattern): `Disponível → Emprestado → Reservado`, elimina condicionais de status espalhados |
| `Usuario` | Aluno, professor ou comunidade | Delega a política de empréstimo (Strategy) em vez de `if/else` por tipo |
| `Emprestimo` | Liga um `Usuario` a um `Exemplar` | Sabe calcular atraso e multa sozinho — comportamento rico, não getters/setters |
| `Reserva` | Fila de espera por um `Exemplar` indisponível | Notifica interessados via Observer quando o exemplar volta a ficar disponível |
| `RepositorioGenerico<T, ID>` | Abstração de persistência | Isola o domínio da infraestrutura via Generics + Inversão de Dependência |
| `BibliotecaServico` | Camada de aplicação (use cases) | Único ponto que coordena vários colaboradores de domínio; depende apenas de `RepositorioGenerico`, nunca de `RepositorioEmMemoria` diretamente |
| `MenuCLI` / `Main` | Interface (console) e raiz de composição | UI tratada como detalhe de infraestrutura — troca por GUI/Web não tocaria o domínio nem `BibliotecaServico` |

## 2. Diagrama de Classes UML

Ver [`diagrama-classes.md`](diagrama-classes.md) (fonte Mermaid). `[TODO:
exportar para imagem/PDF e embutir aqui na versão final]`.

## 3. Matriz de Justificativa de Design

| Princípio/Padrão | Onde é aplicado | Problema original | Ganho arquitetural |
|---|---|---|---|
| **TRUE — Transparent** | `Exemplar.emprestar()`/`devolver()` delegam para `EstadoExemplar` | Um `if (status == EMPRESTADO)` espalhado pelo código escondia quais transições eram válidas | Cada estado só expõe as transições válidas a partir dele; uma transição inválida falha imediatamente (fail-fast), tornando o efeito de qualquer mudança óbvio |
| **TRUE — Reasonable** | Novo estado (`EmManutencao`, por ex.) exigiria só uma nova classe `EstadoExemplar` | Adicionar um status no modelo anêmico exigiria caçar todos os `switch` no código | Custo de estender é proporcional ao benefício: uma classe nova, zero código existente tocado |
| **Tell, Don't Ask** | `usuario.calcularMulta(dias)`, `exemplar.emprestar()` | Uma API anêmica exigiria `if (exemplar.getStatus() == DISPONIVEL) exemplar.setStatus(EMPRESTADO)` no chamador | A regra de negócio mora dentro do objeto dono do estado, eliminando Inveja de Funcionalidade |
| **LSP** | `EstadoDisponivel`/`EstadoEmprestado`/`EstadoReservado` implementam `EstadoExemplar` | — | Qualquer implementação de `EstadoExemplar` é substituível sem quebrar `Exemplar`; transições inválidas lançam exceção documentada em vez de se comportar de forma surpreendente |
| **OCP + State** | Ciclo de vida do `Exemplar` | — | Novo estado = nova classe, zero modificação nas classes existentes |
| **OCP + Strategy** | `PoliticaEmprestimo` / `PoliticaEmprestimoPadrao` | Regras de prazo/multa por tipo de usuário (aluno/professor/comunidade) | Novo tipo de usuário = nova política, sem tocar em `Usuario` ou `Emprestimo` |
| **OCP + Observer** | `ObservadorReserva` / `Reserva.notificarProximoDaFila()` | Acoplamento direto entre devolução de exemplar e notificação de interessados | `Reserva` não conhece a forma de notificação (e-mail, log, push); qualquer novo canal só implementa `ObservadorReserva` |
| **Generics** | `RepositorioGenerico<T, ID>` + `RepositorioEmMemoria<T, ID>` (Matheus) | — | Evita `ClassCastException` e repetição de repositórios por entidade |
| **Fail-Fast / Exceções customizadas** | `ExemplarIndisponivelException` (checked), `EstadoInvalidoException` (unchecked) (Matheus) — declaradas em `BibliotecaServico.emprestar()` `[integração pendente: EstadoExemplar ainda lança UnsupportedOperationException como placeholder, ver TODOs no arquivo]` | — | Diferencia falha de negócio recuperável (tentar emprestar exemplar indisponível) de violação de contrato/estado interno |
| **Dependency Inversion (persistência/UI)** | `BibliotecaServico` depende só de `RepositorioGenerico`; `MenuCLI` depende só de `BibliotecaServico`; `Main` é a única raiz de composição que conhece `RepositorioEmMemoria` | — | Persistência e interface (CLI) tratadas como detalhe de infraestrutura, substituíveis sem tocar no domínio |

## 4. Guia de execução rápida

```bash
# compilar e rodar os testes
mvn test

# empacotar
mvn package

# executar a aplicação (CLI)
mvn exec:java
```

## 5. Divisão de tarefas

| Membro | Responsabilidade | Status |
|---|---|---|
| P1 | Núcleo do domínio: `Livro`, `Exemplar`, State | ✅ concluído (`origin/dev`) |
| P2 | `Emprestimo`, `Reserva`, Strategy (`PoliticaEmprestimo`), Observer | ✅ concluído (`origin/dev`) |
| Matheus | `RepositorioGenerico<T,ID>`, exceções customizadas, integração final | ✅ persistência e exceções concluídas; integração final `[TODO]` |
| P3 (eu) | Camada de aplicação/interface (CLI), relatório técnico, diagrama UML | ✅ `BibliotecaServico` + `MenuCLI` + `Main` implementados; relatório e diagrama atualizados |

`[TODO: detalhar contribuição individual de cada membro para a nota
individualizada — o enunciado usa o histórico de commits como balizador]`
