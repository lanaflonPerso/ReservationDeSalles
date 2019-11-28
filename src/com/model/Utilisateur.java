package com.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utilisateur
{
    private String prenom;
    private String nom;
    private String mail;
    private String motdepasse;
    private List<Reservation> reservations;

    public Utilisateur(String prenom, String nom, String mail, String motdepasse)
    {
        this.prenom = prenom;
        this.nom = nom;
        this.mail = mail;
        this.motdepasse = motdepasse;
        reservations = new ArrayList<>();
    }

    public Utilisateur(String mail)
    {
        this.mail = mail;
    }

    public String getPrenom()
    {
        return prenom;
    }

    public void setPrenom(String prenom)
    {
        this.prenom = prenom;
    }

    public String getNom()
    {
        return nom;
    }

    public void setNom(String nom)
    {
        this.nom = nom;
    }

    public String getMail()
    {
        return mail;
    }

    public void setMail(String mail)
    {
        this.mail = mail;
    }

    public String getMotdepasse()
    {
        return motdepasse;
    }

    public List<Reservation> getReservations()
    {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations)
    {
        this.reservations = reservations;
    }

    public Salle getSalle(String nomSalle)
    {
        for (Reservation reservation : reservations)
        {
            if (reservation.getSalle().getNomSalle().equals("nomSalle"))
            {
                return reservation.getSalle();
            }
        }
        return null;
    }

    public void addReservation(Reservation reservation) throws Exception
    {
        Utils.addReservation(reservation);
        reservations.add(reservation);
    }

    public void deleteReservation(Reservation reservation) throws IOException
    {
        if (!reservations.remove(reservation))
            throw new IOException("La réservation n'a pas été supprimée");
        else
            Utils.annulerReservationSalle(reservation);
    }
}
