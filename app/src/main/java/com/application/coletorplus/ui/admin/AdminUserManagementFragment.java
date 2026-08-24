package com.application.coletorplus.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.coletorplus.data.database.AppDatabase;
import com.application.coletorplus.data.model.Usuario;
import com.application.coletorplus.databinding.FragmentAdminUsersBinding;
import com.application.coletorplus.databinding.ItemUsuarioBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminUserManagementFragment extends Fragment {

    private FragmentAdminUsersBinding binding;
    private UsuarioAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa o RecyclerView
        binding.rvUserManagement.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UsuarioAdapter();
        binding.rvUserManagement.setAdapter(adapter);

        // Configuração do clique do botão para adicionar novo usuário
        binding.btnNovoUsuario.setOnClickListener(v -> {
            showAddUserDialog();
        });

        carregarUsuarios();
    }

    private void carregarUsuarios() {
        new Thread(() -> {
            List<Usuario> lista = AppDatabase.getInstance(requireContext()).usuarioDao().listarTodos();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter.setUsuarios(lista);
                });
            }
        }).start();
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Novo Usuário");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText inputNome = new EditText(getContext());
        inputNome.setHint("Nome Completo");
        layout.addView(inputNome);

        final EditText inputLogin = new EditText(getContext());
        inputLogin.setHint("Matrícula / Login");
        layout.addView(inputLogin);

        final EditText inputPassword = new EditText(getContext());
        inputPassword.setHint("Senha");
        inputPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputPassword);

        builder.setView(layout);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String nome = inputNome.getText().toString().trim();
            String login = inputLogin.getText().toString().trim();
            String senha = inputPassword.getText().toString().trim();

            if (!nome.isEmpty() && !login.isEmpty() && !senha.isEmpty()) {
                new Thread(() -> {
                    Usuario novoUsuario = new Usuario(nome, login, senha, "OPERADOR");
                    AppDatabase.getInstance(requireContext()).usuarioDao().inserir(novoUsuario);
                    
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            carregarUsuarios(); // Atualiza a lista
                        });
                    }
                }).start();
            } else {
                Toast.makeText(getContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private static class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {
        private List<Usuario> usuarios = new ArrayList<>();

        public void setUsuarios(List<Usuario> usuarios) {
            this.usuarios = usuarios;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemUsuarioBinding binding = ItemUsuarioBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Usuario u = usuarios.get(position);
            holder.binding.tvItemNomeUsuario.setText(u.getNome());
            holder.binding.tvItemMatricula.setText("Matrícula: " + u.getMatricula());
            holder.binding.tvItemPerfil.setText(u.getPerfil());
            
            // Estilização simples do perfil
            if ("MASTER".equals(u.getPerfil())) {
                holder.binding.tvItemPerfil.setBackgroundColor(0xFF4CAF50); // Verde
            } else {
                holder.binding.tvItemPerfil.setBackgroundColor(0xFF2196F3); // Azul
            }
        }

        @Override
        public int getItemCount() {
            return usuarios.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ItemUsuarioBinding binding;
            public ViewHolder(ItemUsuarioBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}