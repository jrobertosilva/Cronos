package com.projetocronos.cronos.cronos.helper;

/**
 * Created by jose- on 17/09/2017.
 */

public class _Default {
    protected String _mensagem;
    protected boolean _status;

    public _Default(){
        this._mensagem = "";
        this._status = true;
    }

    public String get_mensagem() {
        return _mensagem;
    }

    public void set_mensagem(String _mensagem) {
        this._mensagem = _mensagem;
    }

    public boolean is_status() {
        return _status;
    }

    public void set_status(boolean _status) {
        this._status = _status;
    }
}
