# Plan: Remove Navigation Drawer (Side Bar)

Remove the Navigation Drawer (Side Bar) from the main user interface to simplify navigation, relying exclusively on the Bottom Navigation and Overflow menu.

## Proposed Changes

### 1. Layout Refactoring

#### [MODIFY] [activity_main.xml](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/res/layout/activity_main.xml)
- Remove `DrawerLayout` and `NavigationView`.
- Keep the `include` for `app_bar_main` as the direct child of the root `FrameLayout` (or the appropriate parent).

### 2. Activity Logic Cleanup

#### [MODIFY] [MainActivity.java](file:///C:/Users/Guilherme59234906/Desktop/PistaLimpa/app/src/main/java/com/application/coletorplus/MainActivity.java)
- Update `mAppBarConfiguration` to remove the `.setOpenableLayout(binding.drawerLayout)` call. This will remove the "Hamburger" icon and the drawer trigger.
- Delete the block of code that finds and configures `navigationView`.
- Clean up unused imports related to `NavigationView`.

## Verification Plan

### Manual Verification
1. **App Launch**: Open the app and verify the "Hamburger" icon is gone from the top bar.
2. **Bottom Nav**: Verify that switching between Stock, Entrada, Saída, and Ajuste still works via the bottom bar.
3. **Drawer Swipe**: Try swiping from the left edge of the screen to ensure the drawer no longer opens.
