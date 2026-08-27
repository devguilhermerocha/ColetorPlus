package com.application.coletorplus.ui.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.coletorplus.R;
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
        adapter = new UsuarioAdapter(this::confirmarRemocao);
        binding.rvUserManagement.setAdapter(adapter);

        // Configuração do clique do botão para adicionar novo usuário
        binding.btnNovoUsuario.setOnClickListener(v -> {
            showAddUserDialog();
        });

        carregarUsuarios();
    }

    private void confirmarRemocao(Usuario usuario) {
        if ("admin".equals(usuario.getMatricula())) {
            Toast.makeText(getContext(), "O usuário admin padrão não pode ser removido!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Remover Usuário")
                .setMessage("Deseja realmente remover " + usuario.getNome() + "?")
                .setPositiveButton("Remover", (dialog, which) -> {
                    new Thread(() -> {
                        AppDatabase.getInstance(requireContext()).usuarioDao().deletar(usuario);
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "Usuário removido", Toast.LENGTH_SHORT).show();
                                carregarUsuarios();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("Cancelar", null)
                .show();
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

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_admin_novo_usuario, null);
        final EditText inputNome = view.findViewById(R.id.etNovoUsuarioNome);
        final EditText inputLogin = view.findViewById(R.id.etNovoUsuarioMatricula);
        final EditText inputPassword = view.findViewById(R.id.etNovoUsuarioSenha);

        builder.setView(view);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String nome = inputNome.getText().toString().trim().toUpperCase();
            String login = inputLogin.getText().toString().trim().toUpperCase();
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
        private final OnUserDeleteListener deleteListener;

        public interface OnUserDeleteListener {
            void onDelete(Usuario usuario);
        }

        public UsuarioAdapter(OnUserDeleteListener deleteListener) {
            this.deleteListener = deleteListener;
        }

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

            holder.binding.btnRemoverUsuario.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDelete(u);
                }
            });
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