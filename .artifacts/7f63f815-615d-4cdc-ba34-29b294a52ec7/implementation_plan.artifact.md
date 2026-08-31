# Plano de Implementação: Padronização Total de Títulos e Alinhamento

O objetivo é padronizar todos os títulos internos do aplicativo (Usuário e Admin), garantindo que tenham o mesmo tamanho, cor e alinhamento, além de ajustar botões que possam estar desalinhados.

## User Review Required

> [!IMPORTANT]
> - Todos os títulos usarão o tamanho **20sp** e a cor **Azul Principal** (`?attr/colorPrimary`) para manter a identidade visual.
> - As margens serão ajustadas para que o título fique sempre a **16dp** da borda da tela (8dp do container + 8dp do título).

## Proposed Changes

### [Geral]
Padronização de todos os 9 fragmentos.

#### [MODIFY] [fragment_consulta_produto.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_consulta_produto.xml)
- Ajustar `TextView` do título: `20sp`, `bold`, `?attr/colorPrimary`, `marginStart="8dp"`, `marginTop="8dp"`, `marginBottom="16dp"`.

#### [MODIFY] [fragment_entrada.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_entrada.xml)
- Ajustar `TextView` do título: `20sp`, `bold`, `?attr/colorPrimary`, `marginStart="8dp"`, `marginTop="8dp"`, `marginBottom="16dp"`.

#### [MODIFY] [fragment_saida.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_saida.xml)
- Ajustar `TextView` do título: `20sp`, `bold`, `?attr/colorPrimary`, `marginStart="8dp"`, `marginTop="8dp"`, `marginBottom="16dp"`.

#### [MODIFY] [fragment_ajuste.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_ajuste.xml)
- Ajustar `TextView` do título: `20sp`, `bold`, `?attr/colorPrimary`, `marginStart="8dp"`, `marginTop="8dp"`, `marginBottom="16dp"`.

#### [MODIFY] [fragment_admin_dashboard.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_dashboard.xml)
- Ajustar `TextView` do título: `20sp`, `bold`, `?attr/colorPrimary`, `marginStart="8dp"`, `marginTop="8dp"`, `marginBottom="16dp"`.

#### [MODIFY] [fragment_admin_audit.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_audit.xml)
- Ajustar `TextView` do título: `20sp`, `bold`, `?attr/colorPrimary`, `marginStart="8dp"`, `marginTop="8dp"`, `marginBottom="16dp"`.

#### [MODIFY] [fragment_admin_inventory.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_inventory.xml)
- Ajustar `TextView` do título: `20sp`, `bold`, `?attr/colorPrimary`, `marginStart="8dp"`, `marginTop="8dp"`, `marginBottom="16dp"`.

#### [MODIFY] [fragment_admin_users.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_users.xml)
- Ajustar o cabeçalho (`LinearLayout`): remover padding extra (`paddingTop`).
- Ajustar `TextView`: `20sp`, `bold`, `?attr/colorPrimary`, `marginStart="8dp"`, `marginTop="8dp"`, `marginBottom="8dp"`.
- Ajustar botão "ADICIONAR": reduzir altura para `48dp` para não empurrar o título.

#### [MODIFY] [fragment_admin_products.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_products.xml)
- Ajustar o cabeçalho (`LinearLayout`): remover padding extra (`paddingTop`).
- Ajustar `TextView`: `20sp`, `bold`, `?attr/colorPrimary`, `marginStart="8dp"`, `marginTop="8dp"`, `marginBottom="8dp"`.
- Ajustar botão "NOVO": reduzir altura para `48dp` para não empurrar o título.

## Verification Plan

### Manual Verification
- Navegar por todas as telas do app.
- Confirmar que o título não "pula" de posição ao trocar de aba.
- Verificar se os títulos do Admin (Equipe/Produtos) estão na mesma altura dos outros.
