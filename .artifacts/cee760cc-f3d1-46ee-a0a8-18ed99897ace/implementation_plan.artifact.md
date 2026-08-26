# Implementation Plan - Multi-Option Unaddressing (Saída)

Implement a flexible unaddressing system in the **Saída** screen. Operators will be able to identify a warehouse location and then either remove all products from it at once or selectively remove individual products from a list.

## User Review Required

> [!IMPORTANT]
> - **Bulk Unaddressing**: A new "Zerar Local" (Clear Location) button will allow removing all product links from a street in one tap.
> - **Selective Unaddressing**: A real-time list of all products currently at the identified location will be displayed, each with a remove button.

## Proposed Changes

### 1. Database & DAO Extensions

#### [MODIFY] [EnderecoDao.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/data/dao/EnderecoDao.java)
- Add `@Query("DELETE FROM produto_endereco_ref WHERE enderecoId = :enderecoId")`
  `void desenderecarTodosProdutos(int enderecoId);`

### 2. UI Refinement

#### [MODIFY] [fragment_saida.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_saida.xml)
- **Street Input**: Standardize as an `AutoCompleteTextView` inside an Outlined `TextInputLayout` (UPPER standard).
- **Street Info Card**: Add a confirmation card to show the identified street name.
- **Bulk Action**: Add a "LIMPAR TODO LOCAL" button (red-outlined style).
- **Product List**: Configure `rvSaidaItens` to show products currently in the identified street.

### 3. Adapter Creation

#### [NEW] `SaidaBatchAdapter.java`
- Adapter to display products stored at the selected location.
- Each item will show: Product Name, EAN, and a "Minus" icon to desendereçar that specific product.

### 4. Logic Implementation

#### [MODIFY] [SaidaFragment.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/ui/user/SaidaFragment.java)
- **Street Identification**: Fetch products from `buscarProdutosPorEndereco` as soon as a street is confirmed.
- **Bulk Removal**: Implement `desenderecarTodosProdutos` logic.
- **Individual Removal**: Implement `desenderecarProduto` logic for each list item.
- **Auto-Refresh**: Automatically refresh the list after any removal operation.

## Verification Plan

### Manual Verification
1. **Selection**: Scan or type a street name. Verify the info card appears and the product list is populated.
2. **Individual Removal**: Tap the "-" icon on a product. Verify it disappears from the list and its link is removed (verify in Consulta screen).
3. **Bulk Removal**: Tap "LIMPAR TODO LOCAL". Verify the list becomes empty and all products previously there are now unaddressed.
4. **Consistency**: Ensure all inputs and buttons follow the standardized measurements (64dp height, 28dp icons).
