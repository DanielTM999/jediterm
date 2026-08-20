# Alterações do fork Orion

Fork mantido para o [OrionIde](https://github.com/DanielTM999). Base: upstream `JetBrains/jediterm`.

- Branch `master`: espelho do upstream. Não commitar nada aqui.
- Branch `orion`: código do fork. Rebase com `git fetch upstream && git rebase upstream/master`.

Código novo vive em `ui/src/com/jediterm/terminal/ui/ext/`, que não existe no upstream e portanto
nunca conflita. A lista abaixo cobre o que foi tocado **em arquivos do upstream** — é o que precisa
ser revisado a cada rebase.

## Build

| Arquivo | Alteração |
|---|---|
| `build.gradle.kts` | `group` = `dtm.ide`; `version` = `<VERSION>-orion.<orionRevision>` |
| `gradle.properties` | nova propriedade `orionRevision` |
| `core/build.gradle.kts` | `artifactId` = `jediterm-core-orion`; POM aponta para o fork; removido o repositório de publicação da JetBrains |
| `ui/build.gradle.kts` | `artifactId` = `jediterm-ui-orion`; `implementation(project(":core"))` → `api(...)`; POM aponta para o fork; removido o repositório de publicação da JetBrains |

O arquivo `VERSION` é deixado intacto de propósito: ele acompanha o upstream, e a revisão do Orion
é versionada à parte em `orionRevision`.

## Código

Novo, sem equivalente upstream (não conflita em rebase):

- `ui/src/com/jediterm/terminal/ui/ext/TerminalFontResolver.java`
- `ui/src/com/jediterm/terminal/ui/ext/GlyphFallbackFontResolver.java`
- `ui/src/com/jediterm/terminal/ui/ext/TerminalKeyInterceptor.java`
- `ui/src/com/jediterm/terminal/ui/ext/TerminalScrollListener.java`
- `ui/src/com/jediterm/terminal/ui/ext/TerminalSettingsListener.java`

### `ui/.../TerminalPanel.java`

| O quê | Por quê |
|---|---|
| `mySettingsProvider` deixou de ser `final`; novos `getSettingsProvider()` / `setSettingsProvider()` / `settingsChanged()` | trocar preferências em runtime sem recriar a sessão |
| `reinitFontAndResize()` passou de `protected` para `public` | reaplicar fonte de fora do painel |
| `getFontToDisplay` consulta um `TerminalFontResolver`; a lógica original virou `getStyledFontToDisplay` | fallback de glifo sem subclasse |
| `handleKeyEvent` roda os `TerminalKeyInterceptor` (com poder de consumir o evento) | `addCustomKeyListener` vê a tecla mas não consegue vetá-la |
| `scrollArea` e `handleMouseWheelEvent` notificam `TerminalScrollListener` | ancorar viewport/seleção durante saída contínua |
| novo `setSelection(TerminalSelection)` público, delegando ao `updateSelection` privado | evita reflexão no campo `mySelection` |
| listas `keyInterceptors` / `scrollListeners` / `settingsListeners` + campo `fontResolver` e respectivos add/remove | registro dos hooks acima |
| **fix:** cursor sublinhado desenhava em `yCoord + height`, fora da célula | invadia a linha de baixo |
| **fix:** `panelPointToCell` dividia por `myCharSize.width/height` sem guarda | `ArithmeticException: / by zero` em eventos de mouse antes do `init()` / depois do `dispose()`, o sintoma que aparecia como crash em `findHyperlink`/`handleHyperlinks` |
| **fix:** `findHyperlink` aceitava `line == height` (off-by-one) | linha fora do buffer |
| `getStyleForeground` usa `UserSettingsProvider.dimIntensity()` no lugar da média fixa 50% | CLIs modernas usam SGR 2 (DIM) para texto secundário; blend 50/50 deixa quase ilegível |
| construtor e `settingsChanged()` aplicam `UserSettingsProvider.getDefaultCursorShape()` | forma do caret configurável (DECSCUSR da aplicação continua tendo precedência) |

### `ui/.../settings/UserSettingsProvider.java`

Dois métodos `default` novos, ambos com o comportamento upstream como padrão:
`dimIntensity()` (`0.5f`) e `getDefaultCursorShape()` (`BLINK_BLOCK`).

### Conhecido, não corrigido

`ControlSequence` trata `:` como caractere não-tratado em vez de separador de subparâmetro, então a forma
ITU T.416 de truecolor (`ESC[38:2::r:g:b m`) não é parseada. A forma com `;` — que é a que o chalk/Ink e
portanto o Claude Code emitem — funciona normalmente.

### `ui/.../JediTermWidget.java`

`mySettingsProvider` deixou de ser `final`; novos `getSettingsProvider()` / `setSettingsProvider()`, que propagam para o painel.

**fix:** `createDefaultStyle()` lia `SettingsProvider.getDefaultStyle()`, que é `@Deprecated`, em vez do par
`getDefaultForeground()` / `getDefaultBackground()` documentado como substituto. Um provider que sobrescrevesse
só o par novo recebia silenciosamente o fallback `TextStyle(BLACK, WHITE)` — cores **indexadas** 0 e 7. Como
`getInversedStyle()` preenche nulos a partir do `StyleState`, o bloco do caret acabava pintado com a cor do
índice 0 da paleta, ou seja, na cor do próprio fundo: caret invisível.

### `core/.../model/TerminalTextBuffer.kt`

`maxHistoryLinesCount` deixou de ser `val` de construtor e virou `var` privada com
`getMaxHistoryLinesCount()` / `setMaxHistoryLinesCount(Int)`. O setter recria o storage de histórico
preservando as linhas mais recentes (e também o backup do buffer alternativo). Sem isso, mudar o
tamanho do scrollback só valeria para sessões novas.

## Publicar no Maven local

    ./gradlew -x test :core:publishToMavenLocal :ui:publishToMavenLocal

Bumpe `orionRevision` em `gradle.properties` a cada publicação consumida pelo OrionIde.
