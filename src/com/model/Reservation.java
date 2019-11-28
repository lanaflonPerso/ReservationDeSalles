package com.model;

import java.sql.Date;
import java.time.LocalTime;

public class Reservation
{
    private Utilisateur utilisateur;
    private Salle salle;
    private Date dateDebut;
    private LocalTime heureDebut;
    private Date dateFin;
    private LocalTime heureFin;

    public Reservation(Utilisateur utilisateur, Salle salle, Date dateDebut, String heureDebut, Date dateFin, String heureFin)
    {
        this.utilisateur = utilisateur;
        this.salle = salle;
        this.dateDebut = dateDebut;
        this.heureDebut = LocalTime.parse(heureDebut);
        this.dateFin = dateFin;
        this.heureFin = LocalTime.parse(heureFin);
    }

    public Utilisateur getUtilisateur()
    {
        return utilisateur;
    }

    public Salle getSalle()
    {
        return salle;
    }

    public Date getDateDebut()
    {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut)
    {
        this.dateDebut = dateDebut;
    }

    public Date getDateFin()
    {
        return dateFin;
    }

    public void setDateFin(Date dateFin)
    {
        this.dateFin = dateFin;
    }

    public LocalTime getHeureDebut()
    {
        return heureDebut;
    }

    public LocalTime getHeureFin()
    {
        return heureFin;
    }

    @Override
    public String toString()
    {
        return salle.getNomSalle() + " le " + dateDebut.toString() + " à " + heureDebut.toString() + " jusqu'au " + dateFin.toString() + " à " + heureFin.toString();
    }
}
