# Plano de Implementação - Ajuste de Tamanho dos Ícones

Este plano visa aumentar a visibilidade dos ícones de scanner (leitor e qrcode) e do botão de adicionar usuário, garantindo que as imagens PNG preencham melhor o espaço.

## Propostas de Mudanças

### [Botões de Scanner (ImageButtons)]
Vou aumentar o tamanho dos botões de `48dp` para `56dp` e ajustar o preenchimento para que o ícone PNG fique maior e mais centralizado.

#### [MODIFY] [fragment_consulta_produto.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_consulta_produto.xml)
- `btnScannerConsulta`: Tamanho 56dp, padding 4dp, scaleType centerInside.

#### [MODIFY] [fragment_entrada.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_entrada.xml)
- `btnScannerRua` e `btnScannerProdutoEntrada`: Tamanho 56dp, padding 4dp, scaleType centerInside.

#### [MODIFY] [fragment_saida.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_saida.xml)
- `btnScannerSaida`: Tamanho 56dp, padding 4dp, scaleType centerInside.

#### [MODIFY] [fragment_ajuste.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_ajuste.xml)
- `btnScannerAvaria`: Tamanho 56dp, padding 4dp, scaleType centerInside.

### [Botão de Adicionar (MaterialButton)]
Vou aumentar o tamanho do ícone de "+" no botão de cadastro de equipe.

#### [MODIFY] [fragment_admin_users.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_users.xml)
- `btnNovoUsuario`: Adicionar `app:iconSize="32dp"` para destacar o ícone de adicionar.

## Plano de Verificação

### Verificação Manual
1. Abrir o Preview do Android Studio para cada fragmento.
2. Confirmar se os ícones de leitor/qrcode estão maiores e mais fáceis de tocar.
3. Verificar se o botão de adicionar na tela de equipe ficou mais imponente.
