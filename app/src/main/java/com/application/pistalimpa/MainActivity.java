package com.application.pistalimpa;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.application.pistalimpa.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Infla o ViewBinding da Activity
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 2. Configura a Toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // 3. Removemos ou ajustamos o FAB (Botão Flutuante) se necessário
        if (binding.appBarMain.fab != null) {
            binding.appBarMain.fab.setOnClickListener(view -> {
                // Ação rápida futura (ex: abrir câmera direto)
            });
        }

        // 4. Captura o NavController através do NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // 5. Configuração do Menu Lateral (Drawer) para telas maiores/smartphones
        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_reposicao, R.id.nav_historico, R.id.nav_settings)
                    .setOpenableLayout(binding.drawerLayout)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        // 6. Configuração da BottomNavigation (caso o layout responsivo utilize no rodapé)
        BottomNavigationView bottomNavigationView = binding.appBarMain.contentMain.bottomNavView;
        if (bottomNavigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_reposicao, R.id.nav_historico, R.id.nav_settings)
                    .build();
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        NavigationView navView = findViewById(R.id.nav_view);

        // Se o menu lateral não estiver visível (ex: telas específicas/foldables), infla o menu de três pontos
        if (navView == null) {
            getMenuInflater().inflate(R.menu.overflow, menu);
        }
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Redirecionamento correto para as Configurações quando clicado no menu de contexto
        if (item.getItemId() == R.id.nav_settings) {
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}