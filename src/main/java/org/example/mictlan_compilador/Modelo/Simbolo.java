package org.example.mictlan_compilador.Modelo;

public class Simbolo {
    private int id;
    private String palabra;

    public Simbolo(int id, String palabra) {
        this.id = id;
        this.palabra = palabra;
    }

    public int getId() { return id; }
    public String getPalabra() { return palabra; }
}
