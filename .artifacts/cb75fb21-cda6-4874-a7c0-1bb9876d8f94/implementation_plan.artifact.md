# Implementation Plan - Ajuste do Activity Main e UI do PistaLimpa

O arquivo `activity_main.xml` atual contém erros de sintaxe XML (tags não fechadas e estrutura incorreta) que impedem a compilação e visualização da tela. Além disso, o conteúdo da interface personalizada do "PistaLimpa" foi inserido diretamente no layout principal da Activity, o que conflita com a arquitetura de Navegação (Navigation Drawer + Bottom Navigation) do projeto.

## Objetivo
Corrigir os erros de sintaxe no `activity_main.xml` e mover a interface personalizada para o Fragmento inicial (`TransformFragment`), garantindo que o app funcione corretamente e mantenha a estrutura de navegação.

## User Review Required

> [!IMPORTANT]
> O projeto utiliza um template de "Responsive Activity" com Navigation Drawer e Bottom Navigation. A interface personalizada que você criou (título, busca, checkbox, botão adicionar) será movida para o `fragment_transform.xml`, que é a tela inicial do aplicativo. Isso permitirá que você continue usando o menu lateral e a barra superior sem quebrar o código do `MainActivity.java`.

## Propostas de Mudanças

### [Layout Principal]

#### [MODIFY] [activity_main.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/activity_main.xml)
- Corrigir a sintaxe XML.
- Restaurar a estrutura padrão com `DrawerLayout`, `NavigationView` e o include do `app_bar_main`.
- Remover o conteúdo personalizado deste arquivo para evitar conflitos com a lógica do `MainActivity.java`.

#### [MODIFY] [fragment_transform.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_transform.xml)
- Inserir a interface personalizada do PistaLimpa aqui:
    - Título "PistaLimpa 🛒".
    - Campo de busca e botão de câmera.
    - Checkbox "Crítico" e botão "Adicionar".
    - RecyclerView para a lista de produtos.

### [Recursos]

#### [NEW] [item_produto.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/item_produto.xml)
- Criar um layout básico para os itens da lista de produtos, já que ele é referenciado no `RecyclerView`.

## Plano de Verificação

### Verificação Manual
1. Abrir o Preview do Android Studio para o `activity_main.xml` e `fragment_transform.xml`.
2. Compilar o projeto para garantir que não existam erros de Binding.
3. Verificar se a interface aparece corretamente ao iniciar o app.
