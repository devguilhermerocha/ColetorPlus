# Plan: Final Admin UI Standardization

Unify the Top Bar alignment, standardize action buttons across admin screens, and refine the "New User/Product" dialogs to match the application's "Clear" design standard.

## Proposed Changes

### 1. Top Bar Alignment

#### [MODIFY] [activity_admin.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/activity_admin.xml)
- Add `android:fitsSystemWindows="true"` to the root `ConstraintLayout`. This ensures the `AppBarLayout` respects the system status bar, matching the behavior and positioning of the User's `MainActivity`.

### 2. Button Standardization

#### [MODIFY] [fragment_admin_products.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_products.xml)
#### [MODIFY] [fragment_admin_users.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_users.xml)
#### [MODIFY] [fragment_admin_audit.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_audit.xml)
- Increase the height of header action buttons (NOVO, ADICIONAR, LIMPAR) to be more touch-friendly and robust.
- Ensure `app:iconPadding` and `app:iconSize` are consistent with the "User" operations buttons.

### 3. Dialog Refinement

#### [MODIFY] [dialog_admin_novo_produto.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/dialog_admin_novo_produto.xml)
#### [MODIFY] [dialog_admin_novo_usuario.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/dialog_admin_novo_usuario.xml)
- Adjust internal spacing to ensure they look "Clear" and professional.
- Match the `68dp` scanner button height for the product dialog.

## Verification Plan

### Manual Verification
1. **Visual Alignment**: Navigate between User and Admin roles. Verify the Toolbar title and icons are at the same vertical level relative to the top of the screen.
2. **Button Consistency**: Verify that buttons like "NOVO" and "ADICIONAR" have a consistent, modern feel across management screens.
3. **Dialog Check**: Open both New Product and New User dialogs. Confirm they share the same padding, border styles, and field alignment.
