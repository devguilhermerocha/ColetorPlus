package com.application.coletorplus.ui.admin;

public class DashboardAlerta {
    public enum TipoAlerta {
        VENCIMENTO, ESTOQUE_ZERO
    }

    private String titulo;
    private String subtitulo;
    private TipoAlerta tipo;
    private int prioridade; // 1 para Vencimento (Alta), 2 para Estoque Zero

    public DashboardAlerta(String titulo, String subtitulo, TipoAlerta tipo, int prioridade) {
        this.titulo = titulo;
        this.subtitulo = subtitulo;
        this.tipo = tipo;
        this.prioridade = prioridade;
    }

    public String getTitulo() { return titulo; }
    public String getSubtitulo() { return subtitulo; }
    public TipoAlerta getTipo() { return tipo; }
    public int getPrioridade() { return prioridade; }
}
