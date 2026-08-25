# Ajuste Visual de Ícones e Scanner de Endereços

O objetivo é harmonizar o tamanho dos ícones nas barras de busca e atualizar a iconografia da gestão de endereços para usar o símbolo de QR Code.

## User Review Required

> [!NOTE]
> Vou padronizar o tamanho visual dos ícones em 24dp dentro das barras, garantindo que o espaçamento (padding) deixe a interface mais limpa.

## Proposed Changes

### UI & UX

#### [MODIFY] [fragment_admin_inventory.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_inventory.xml)
- Trocar o `src` do `btnBarcodeScanner` de `@drawable/leitor` para `@drawable/qrcode`.
- Ajustar `padding` e `scaleType` para que o ícone fique proporcional à barra de 48dp.
- Adicionar um ícone de QR Code decorativo no `cardInventoryDetails` ao lado do nome da rua.

#### [MODIFY] [fragment_admin_products.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_products.xml)
- Ajustar o tamanho visual dos ícones no `TextInputLayout` usando `app:startIconSize` e `app:endIconSize` se disponível, ou ajustar o padding do drawable.

#### [MODIFY] [fragment_consulta_produto.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_consulta_produto.xml)
- Sincronizar o tamanho do ícone de scanner com a barra de busca para manter a proporção.

## Verification Plan

### Manual Verification
- Verificar se o ícone de QR Code aparece na tela de Gestão de Endereços.
- Confirmar se os ícones de scanner nas barras de busca estão centralizados e com tamanho "relevante" (nem muito pequenos, nem tocando as bordas).
