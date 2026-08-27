package com.application.coletorplus.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.application.coletorplus.data.dao.AuditoriaDao;
import com.application.coletorplus.data.dao.EnderecoDao;
import com.application.coletorplus.data.dao.ProdutoDao;
import com.application.coletorplus.data.dao.UsuarioDao;
import com.application.coletorplus.data.dao.ValidadeDao;
import com.application.coletorplus.data.model.Auditoria;
import com.application.coletorplus.data.model.Endereco;
import com.application.coletorplus.data.model.Produto;
import com.application.coletorplus.data.model.ProdutoEndereco;
import com.application.coletorplus.data.model.ProdutoValidade;
import com.application.coletorplus.data.model.Usuario;
import com.application.coletorplus.data.model.Validade;

import java.util.concurrent.Executors;

@Database(entities = {
        Produto.class,
        Usuario.class,
        Endereco.class,
        Validade.class,
        ProdutoEndereco.class,
        ProdutoValidade.class,
        Auditoria.class
}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ProdutoDao produtoDao();
    public abstract UsuarioDao usuarioDao();
    public abstract EnderecoDao enderecoDao();
    public abstract ValidadeDao validadeDao();
    public abstract AuditoriaDao auditoriaDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "coletorplus_database"
                            )
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        // Conta Master Padrão gerada no primeiro boot
                                        Usuario masterInitial = new Usuario("Administrador Master", "admin", "1234", "MASTER");
                                        getInstance(context).usuarioDao().inserir(masterInitial);
                                    });
                                }
                            })
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}