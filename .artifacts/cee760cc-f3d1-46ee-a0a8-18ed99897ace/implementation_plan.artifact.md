# Plan: UI Polishing and Dialog Standardization

Refine the application's visual polish by fixing the Admin status bar color, standardizing dialog headers and buttons, and improving the audit cleanup action.

## Proposed Changes

### 1. Admin Status Bar Color

#### [MODIFY] [activity_admin.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/activity_admin.xml)
- Ensure the `AppBarLayout` has `android:background="?attr/colorPrimary"`.
- Verify if `android:fitsSystemWindows="true"` is correctly applied to the root container to allow the status bar to inherit the theme's primary color.

### 2. Standardized "Clear" Dialogs (Top Bar & Buttons)

#### [MODIFY] [dialog_admin_novo_produto.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/dialog_admin_novo_produto.xml)
#### [MODIFY] [dialog_admin_novo_usuario.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/dialog_admin_novo_usuario.xml)
- **Top Bar**: Add a blue header (`@color/purple_500`) with white title text inside the dialog layout.
- **Buttons**:
    - Remove reliance on `AlertDialog`'s default buttons.
    - Add a horizontal `LinearLayout` at the bottom of the XML with two `MaterialButton` components: "CANCELAR" (Outlined) and "SALVAR" (Contained, Blue background, White text).
- **Padding**: Set root padding to `0dp` and apply padding only to the content area (middle part) to allow the top bar to touch the edges.

### 3. Logic Refactoring

#### [MODIFY] [AdminProductManagementFragment.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/ui/admin/AdminProductManagementFragment.java)
#### [MODIFY] [AdminUserManagementFragment.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/ui/admin/AdminUserManagementFragment.java)
- Update `showAdd...Dialog` methods:
    - Inflate the view.
    - Create the `AlertDialog` without title or buttons (since they are now in the XML).
    - Find the internal "SALVAR" and "CANCELAR" buttons.
    - Wire the logic to these internal buttons and dismiss the dialog manually.

### 4. Audit Button Visuals

#### [MODIFY] [fragment_admin_audit.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/fragment_admin_audit.xml)
- Change `btnLimparAudit` to a contained `MaterialButton` with `@android:color/holo_red_dark` background for a clearer destructive action visual.

## Verification Plan

### Manual Verification
1. **Status Bar**: Open the Admin area. Verify the top status bar (battery/clock) is blue.
2. **Dialogs**: Open "New Product" and "New User". Verify they have a blue title bar and blue primary buttons.
3. **Audit**: Verify the "Limpar Histórico" button is now more prominent (solid red).
