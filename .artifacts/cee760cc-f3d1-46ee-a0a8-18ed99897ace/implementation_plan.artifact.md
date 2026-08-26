# Plan: Standardize EAN Input and Enforce Batch Selection

Refactor the "Ajuste / Avaria" screen to use a standard `TextInputLayout` for EAN entry, matching the rest of the form fields, while ensuring batch selection is mandatory for adjustments.

## Proposed Changes

### 1. UI Standardization

#### [MODIFY] [fragment_ajuste.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_ajuste.xml)
- **EAN Field**: Replace the horizontal `LinearLayout` + `EditText` combination with a single `TextInputLayout` (style `OutlinedBox`).
- **Scanner Integration**: Add the scanner icon as the `endIconDrawable` of the `TextInputLayout`.
- **Consistency**: This ensures all inputs on the screen (EAN, Batch, Quantity, Reason) look and behave identically according to Material Design standards.

### 2. Logic Refinement

#### [MODIFY] [AjusteFragment.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/ui/user/AjusteFragment.java)
- **View Binding**: Update references from `binding.btnScanEanAvaria` to `binding.tilCodigoAvaria.setEndIconOnClickListener`.
- **Mandatory Batch Check**:
    - Reinforce the check in `confirmarBaixaAvaria` to ensure `selectedValidadeTimestamp` is strictly required.
    - Added user feedback in `setupRemovalHoldButton` to warn about missing batch selection during the hold attempt.

## Verification Plan

### Manual Verification
1. **Standardized Look**: Open "Avaria" and verify all fields are `OutlinedBox` style.
2. **Scanner Integration**: Tap the scanner icon inside the EAN field and verify it works.
3. **Mandatory Batch**:
    - Enter an EAN and quantity.
    - Try to hold the confirm button **without** picking a batch.
    - Verify the "Selecione o lote primeiro!" toast appears.
4. **Successful Adjustment**: Select a batch, hold for 3s, and verify stock subtraction.
