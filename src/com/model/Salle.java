package com.model;

public class Salle
{
    private int id;
    private String nomSalle;

    public Salle(int id, String nomSalle)
    {
        this.id = id;
        this.nomSalle = nomSalle;
    }

    public Salle(String nomSalle)
    {
        this.nomSalle = nomSalle;
    }

    public int getId()
    {
        return id;
    }

    public String getNomSalle()
    {
        return nomSalle;
    }
}
