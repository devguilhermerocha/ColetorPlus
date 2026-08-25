# Implementation Plan - Style Update for Adjustment Screen

This plan details the changes to align `fragment_ajuste.xml` with the "Admin Novo Produto" and "Admin Produto" screen styles, using modern Material Design components like `TextInputLayout` and `MaterialButton`.

## User Review Required

> [!NOTE]
> The layout will be simplified by removing the `MaterialCardView`, matching the clean look of the admin dialogs. The scanner functionality will be more prominent with a large button at the top, consistent with the admin product entry flow.

## Proposed Changes

### UI Components

#### [MODIFY] [fragment_ajuste.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_ajuste.xml)
- Increase root padding to `24dp`.
- Add a large `MaterialButton` for scanning at the top.
- Add "OU DIGITE ABAIXO" label.
- Replace `EditText` + `ImageButton` for EAN with a `TextInputLayout` (OutlinedBox) with `endIconDrawable="@drawable/leitor"`.
- Convert "Registrar Avaria" to a themed `MaterialButton`.
- Remove the surrounding `MaterialCardView`.

#### [MODIFY] [AjusteFragment.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/ui/user/AjusteFragment.java)
- Add `setEndIconOnClickListener` for the new EAN `TextInputLayout`.
- Ensure all view bindings match the new layout IDs.

## Verification Plan

### Manual Verification
- Deploy the app and navigate to the "Ajuste / Avaria" screen.
- Verify that the layout matches the "Admin Novo Produto" style (e.g., big scan button, outlined text fields).
- Test the big scan button to ensure it triggers the barcode scanner.
- Test the scanner icon inside the EAN field to ensure it also triggers the barcode scanner.
- Test the "Registrar Avaria" button to ensure the toast message still appears.
