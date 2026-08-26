# Plan: Refactor "Ajuste / Avaria" with Stock Subtraction

Modernize the "Ajuste / Avaria" screen and implement the logic to subtract a specific quantity of damaged/lost items from the product's total stock.

## User Review Required

> [!IMPORTANT]
> - **Quantity Removal**: The new "Quantidade" field will directly **subtract** the entered value from the `Produto.quantidadeTotal` in the database.
> - **Visual Consistency**: The screen will now match the Admin/Catalog style with a prominent scanner button and a clean layout.

## Proposed Changes

### 1. UI Modernization

#### [MODIFY] [fragment_ajuste.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_ajuste.xml)
- **Layout Overhaul**: Remove the `MaterialCardView` and increase padding to `24dp`.
- **Top Scanner**: Add a large `MaterialButton` ("ESCANEAR PRODUTO").
- **EAN Input**: Horizontal layout with `EditText` and a scanner `ImageButton` icon.
- **Quantity Field**: Add a `TextInputLayout` for "Quantidade a Remover (Avaria)".
- **Reason Field**: Keep "Motivo (Opcional)" as an outlined `TextInputLayout`.
- **Action Button**: Use a red-themed `MaterialButton` for "Confirmar Baixa de Avaria".

### 2. Logic Implementation

#### [MODIFY] [AjusteFragment.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/ui/user/AjusteFragment.java)
- **Confirm Adjustment**:
    - Validate that EAN and Quantity are provided.
    - Fetch the product from the database via EAN.
    - **Subtract** the entered quantity from the product's `quantidadeTotal`.
    - Update the product in the database.
    - Show a success toast with the updated stock level.
- **Scanner Integration**: Connect both the big button and the small icon to the `ScannerHelper`.

## Verification Plan

### Manual Verification
1. **Initial Stock**: Check a product's stock in the "Consulta" screen (e.g., 20 units).
2. **Adjustment**: Go to "Avaria", scan/type the EAN, enter "5" as quantity, and confirm.
3. **Verify**: Go back to "Consulta" and verify the stock is now **15**.
4. **Validation**: Try to adjust a product that doesn't exist and verify the error message.
