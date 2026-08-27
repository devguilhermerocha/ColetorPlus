package com.application.coletorplus.data.session;

import com.application.coletorplus.data.model.Usuario;

public class SessionManager {
    private static SessionManager instance;
    private Usuario usuarioLogado;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
    }

    public Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    public void clearSession() {
        this.usuarioLogado = null;
    }
}
