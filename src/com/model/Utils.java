package com.model;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

public abstract class Utils
{
    private static Connection connection;
    private static Properties properties;
    private static String lastMail;

    /**
     * registers a new user in the database
     *
     * @param nom    the name of the user
     * @param prenom the firstname of the user
     */
    public static void registerUser(String nom, String prenom, String mail, String password) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO utilisateur(mail_user, nom_user, prenom_user, password) value (?,?,?,?)");
        preparedStatement.setString(1, mail);
        preparedStatement.setString(2, nom);
        preparedStatement.setString(3, prenom);
        preparedStatement.setString(4, password);

        preparedStatement.executeUpdate();

        preparedStatement.close();
    }


    private static Salle getSalle(int id_salle)
    {
        Salle salle = null;
        try
        {
            PreparedStatement statement = connection.prepareStatement("select * from salle where id_salle = ?");
            statement.setInt(1, id_salle);

            ResultSet set = statement.executeQuery();

            while (set.next())
                salle = new Salle(set.getInt(1), set.getString(2));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return salle;
    }

    public static List<Reservation> getReservations(Salle salle)
    {
        List<Reservation> reservations = new ArrayList<>();

        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT mail_user, nom_salle, date_debut, heure_debut, date_fin, heure_fin FROM " +
                            "(utilisateur INNER JOIN reservation ON utilisateur.mail_user = reservation.mail_user) INNER JOIN salle" +
                            " ON salle.id_salle = reservation.id_salle where salle.nom_salle = ?");
            preparedStatement.setString(1, salle.getNomSalle());

            addReservations(reservations, preparedStatement);

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return reservations;
    }

    private static void addReservations(List<Reservation> reservations, PreparedStatement preparedStatement) throws SQLException
    {
        ResultSet set = preparedStatement.executeQuery();
        while (set.next())
        {
            Utilisateur utilisateur = new Utilisateur(set.getString(1));
            Salle salle = new Salle(set.getString(2));

            reservations.add(new Reservation(utilisateur, salle, set.getDate(3), set.getString(4), set.getDate(5), set.getString(6)));
        }
    }

    public static Salle getSalle(String nom_salle)
    {
        Salle salle = null;
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from salle where nom_salle = ?");
            preparedStatement.setString(1, nom_salle);

            ResultSet set = preparedStatement.executeQuery();

            while (set.next())
                salle = new Salle(set.getInt(1), set.getString(2));
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return salle;
    }

    public static List<Salle> getSalles()
    {
        List<Salle> salles = new ArrayList<>();

        try
        {
            PreparedStatement statement = connection.prepareStatement("select * from salle");
            ResultSet set = statement.executeQuery();
            while (set.next())
                salles.add(new Salle(set.getInt(1), set.getString(2)));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return salles;
    }

    public static Utilisateur connectUser(String mail, String password)
    {
        Utilisateur user = null;
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM utilisateur WHERE mail_user = ? and password = ?");
            preparedStatement.setString(1, mail);
            preparedStatement.setString(2, password);
            ResultSet set = preparedStatement.executeQuery();

            while (set.next())
                user = new Utilisateur(set.getString(3), set.getString(2), set.getString(1), set.getString(4));

            if (user != null)
            {
                preparedStatement = connection.prepareStatement("SELECT * from reservation where mail_user = ?");
                preparedStatement.setString(1, user.getMail());

                set = preparedStatement.executeQuery();
                while (set.next())
                {
                    Salle salle = getSalle(set.getInt(1));
                    user.getReservations().add(new Reservation(user, salle, set.getDate(3), set.getString(4), set.getDate(5), set.getString(6)));
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return user;
    }

    public static List<Reservation> getAllReservations()
    {
        List<Reservation> reservations = new ArrayList<>();
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT reservation.mail_user, nom_salle, date_debut, heure_debut, date_fin, heure_fin FROM " +
                            "(utilisateur INNER JOIN reservation ON utilisateur.mail_user = reservation.mail_user) INNER JOIN salle" +
                            " ON salle.id_salle = reservation.id_salle");
            addReservations(reservations, preparedStatement);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return reservations;
    }

    static void addReservation(Reservation reservation) throws Exception
    {
        PreparedStatement preparedStatement1 = connection.prepareStatement("SELECT * FROM Reservation WHERE " +
                "((date_debut between ? and ?) or (date_fin between ? and ?)) and ((heure_debut between ? and ?) or (heure_fin between ? and ?))");
        preparedStatement1.setDate(1, reservation.getDateDebut(), Calendar.getInstance());
        preparedStatement1.setDate(2, reservation.getDateFin(), Calendar.getInstance());
        preparedStatement1.setDate(3, reservation.getDateDebut(), Calendar.getInstance());
        preparedStatement1.setDate(4, reservation.getDateFin(), Calendar.getInstance());
        preparedStatement1.setString(5, reservation.getHeureDebut().plusMinutes(1).toString());
        preparedStatement1.setString(6, reservation.getHeureFin().minusMinutes(1).toString());
        preparedStatement1.setString(7, reservation.getHeureDebut().plusMinutes(1).toString());
        preparedStatement1.setString(8, reservation.getHeureFin().minusMinutes(1).toString());
        ResultSet set = preparedStatement1.executeQuery();
        if (set.next())
            throw new Exception("Une réservation existe déjà sur la plage horaire sélectionnée");
        else
        {
            reserveSalle(reservation);
        }
    }

    /**
     * reservation d'une salle
     * Le début et la fin de réservation sont traités en précondition
     * post-condition : ajouter reservation dans l'utilisateur après l'appel de cette fonction
     *
     * @param reservation la réservation a effectuer
     */
    private static void reserveSalle(Reservation reservation) throws Exception
    {
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO reservation value(?,?,?,?,?,?)");
            preparedStatement.setInt(1, reservation.getSalle().getId());
            preparedStatement.setString(2, reservation.getUtilisateur().getMail());
            preparedStatement.setDate(3, reservation.getDateDebut(), Calendar.getInstance());
            preparedStatement.setString(4, reservation.getHeureDebut().toString());
            preparedStatement.setDate(5, reservation.getDateFin(), Calendar.getInstance());
            preparedStatement.setString(6, reservation.getHeureFin().toString());
            preparedStatement.executeUpdate();

        } catch (Exception e)
        {
            throw new Exception("Cette salle a déjà été réservé sur cette plage horaire");
        }
    }

    /**
     * post-condition : supprimer reservation de l'utilisateur après l'appel de cette fonction
     *
     * @param reservation la reservation a supprimer
     */
    static void annulerReservationSalle(Reservation reservation)
    {
        try
        {
            PreparedStatement preparedStatement = connection.prepareStatement(" DELETE FROM reservation WHERE id_salle = ? and date_debut = ? and date_fin = ? and heure_debut = ? and heure_fin = ?");
            preparedStatement.setInt(1, reservation.getSalle().getId());
            preparedStatement.setDate(2, reservation.getDateDebut());
            preparedStatement.setDate(3, reservation.getDateFin());
            preparedStatement.setString(4, reservation.getHeureDebut().toString());
            preparedStatement.setString(5, reservation.getHeureFin().toString());

            preparedStatement.executeUpdate();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * chiffre le mot de passe utilisateur
     *
     * @param password le mot de pass à chiffrer
     * @return le mot de pass chiffré
     */
    public static String hashPassword(String password)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("sha-256");
            md.update(password.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes)
            {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void connection()
    {
        try
        {
            properties = new Properties();
            properties.loadFromXML(new FileInputStream("properties.xml"));

            String DB_URL = properties.get("bdd_url").toString();
            String PASS = properties.get("password").toString();
            String USER = properties.get("user").toString();
            lastMail = properties.get("lastMail").toString();

            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (IOException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    public static String getPassword(String mail) throws IOException
    {
        String password = null;
        try
        {

            PreparedStatement preparedStatement = connection.prepareStatement("select password from utilisateur where mail_user = ?");
            preparedStatement.setString(1, mail);

            ResultSet set = preparedStatement.executeQuery();

            while (set.next())
                password = set.getString(1);
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        if (password == null)
            throw new IOException("Aucun compte utilisateur n'est relié à ce mail");
        return password;
    }

    public static String getLastMail()
    {
        return lastMail;
    }

    public static Properties getProperties()
    {
        return properties;
    }

    public static void validMail(String mail) throws IOException
    {
        InternetAddress address = new InternetAddress();
        address.setAddress(mail);
        try
        {
            address.validate();
        } catch (AddressException e)
        {
            throw new IOException("L'email entré n'est pas un mail");
        }
    }
}
