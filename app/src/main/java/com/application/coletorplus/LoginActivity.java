package com.application.coletorplus;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Usuario;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etMatricula, etSenha;
    private Button btnLogin;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etMatricula = findViewById(R.id.etLogin);
        etSenha = findViewById(R.id.etSenha);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> realizarLogin());
    }

    private void realizarLogin() {
        String matricula = etMatricula.getText() != null ? etMatricula.getText().toString().trim() : "";
        String senha = etSenha.getText() != null ? etSenha.getText().toString().trim() : "";

        if (matricula.isEmpty()) {
            etMatricula.setError("Informe a matrícula");
            etMatricula.requestFocus();
            return;
        }
        if (senha.isEmpty()) {
            etSenha.setError("Informe a senha");
            etSenha.requestFocus();
            return;
        }

        new Thread(() -> {
            Usuario usuarioAutenticado = AppDatabase.getInstance(getApplicationContext())
                    .usuarioDao()
                    .autenticar(matricula, senha);

            runOnUiThread(() -> {
                if (usuarioAutenticado != null) {
                    Toast.makeText(this, "Bem-vindo, " + usuarioAutenticado.getNome(), Toast.LENGTH_SHORT).show();

                    Intent intent;
                    if ("MASTER".equals(usuarioAutenticado.getPerfil())) {
                        intent = new Intent(LoginActivity.this, AdminActivity.class);
                    } else {
                        intent = new Intent(LoginActivity.this, MainActivity.class);
                    }

                    intent.putExtra("PERFIL_USUARIO", usuarioAutenticado.getPerfil());
                    intent.putExtra("NOME_USUARIO", usuarioAutenticado.getNome());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Matrícula ou senha incorretos!", Toast.LENGTH_SHORT).show();
                    etSenha.setText("");
                }
            });
        }).start();
    }
}