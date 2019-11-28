package com.controller;

import com.Notification;
import com.model.Reservation;
import com.model.Salle;
import com.model.Utilisateur;
import com.model.Utils;
import com.view.Connexion;
import com.view.ForgotPassword;
import com.view.MainView;
import com.view.SignUp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class Controller
{
    private MainView mainView;
    private Utilisateur utilisateur;
    private Connexion connexion;

    public Controller()
    {
        Utils.connection();
        opening();
    }

    private void opening()
    {
        connexion = new Connexion();
        connexion.getMailField().setText(Utils.getLastMail());

        connexion.getMdpForgot().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                actionForgot();
                connexion.dispose();
            }
        });

        connexion.getSignUp().addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseReleased(MouseEvent e)
            {
                actionSignUp();
                connexion.dispose();
            }
        });

        connexion.getMdpField().addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        logIn();
                    } catch (IOException e1)
                    {
                        new Notification(connexion, "Erreur", e1.getMessage());
                    }
                }
            }
        });

        connexion.getSeConnecterButton().addActionListener(e ->
        {
            try
            {
                logIn();
            } catch (IOException e1)
            {
                new Notification(connexion, "Erreur", e1.getMessage());
            }
        });

        connexion.open();
        if (!connexion.getMailField().getText().equals(""))
            connexion.getMdpField().requestFocusInWindow();
    }

    private void logIn() throws IOException
    {
        String mail = connexion.getMailField().getText();
        Utils.validMail(mail);
        String mdp = Utils.hashPassword(Arrays.toString(connexion.getMdpField().getPassword()));
        utilisateur = Utils.connectUser(mail, mdp);
        if (utilisateur == null)
            throw new IOException("Le nom d'utilisateur ou mot de passe invalide");
        else
        {
            try
            {
                Utils.getProperties().setProperty("lastMail", mail);
                Utils.getProperties().storeToXML(new FileOutputStream("properties.xml"), "Sauvegarde");
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            connexion();
            connexion.dispose();
        }
    }

    private void init()
    {
        initListReservations(null);
        initListSalles();
        initBoxReservations();
        initBoxSalles();
        initDaysHours();

        mainView.getDeleteReservation().addActionListener(e -> deleteReservation());
        mainView.getAddReservation().addActionListener(e -> addReservation());
    }

    private void actionForgot()
    {
        ForgotPassword forgotPassword = new ForgotPassword();
        forgotPassword.getForgotButton().addActionListener(e -> sendPassword(forgotPassword));

        forgotPassword.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                connexion.setVisible(true);
            }
        });

        forgotPassword.getMailField().addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    sendPassword(forgotPassword);
                }
            }
        });

        forgotPassword.open();
    }

    private void sendPassword(ForgotPassword forgotPassword)
    {
        String mail = forgotPassword.getMailField().getText();
        try
        {
            System.out.println(Utils.getPassword(mail));
        } catch (IOException e1)
        {
            new Notification(forgotPassword, "Erreur", e1.getMessage());
        }
    }

    private void actionSignUp()
    {
        SignUp signUp = new SignUp();
        signUp.getSignupButton().addActionListener(e ->
        {
            try
            {
                register(signUp);
            } catch (IOException e1)
            {
                new Notification(signUp, "Erreur", e1.getMessage());
            }
        });
        signUp.getMdpField().addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    try
                    {
                        register(signUp);
                    } catch (IOException e1)
                    {
                        new Notification(signUp, "Erreur", e1.getMessage());
                    }
                }
            }
        });

        signUp.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                connexion.setVisible(true);
            }
        });
        signUp.open();
    }

    private void register(SignUp signUp) throws IOException
    {
        String prenom = signUp.getPrenomField().getText();
        String nom = signUp.getNomField().getText();
        String mail = signUp.getMailField().getText();
        Utils.validMail(mail);
        String motDePasse = Utils.hashPassword(Arrays.toString(signUp.getMdpField().getPassword()));
        utilisateur = new Utilisateur(prenom, nom, mail, motDePasse);
        try
        {
            assert motDePasse != null;
            if (!prenom.equals("") && !nom.equals("") && !mail.equals("") && !motDePasse.equals(""))
            {
                Utils.registerUser(utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getMail(), utilisateur.getMotdepasse());
                try
                {
                    Utils.getProperties().setProperty("lastMail", mail);
                    Utils.getProperties().storeToXML(new FileOutputStream("properties.xml"), "Sauvegarde");
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                signUp.dispose();
                connexion();
            } else
            {
                if (prenom.equals(""))
                    signUp.getPrenomField().setBorder(BorderFactory.createLineBorder(Color.red));
                else
                    signUp.getPrenomField().setBorder(BorderFactory.createLineBorder(Color.lightGray));
                if (nom.equals(""))
                    signUp.getNomField().setBorder(BorderFactory.createLineBorder(Color.red));
                else
                    signUp.getNomField().setBorder(BorderFactory.createLineBorder(Color.lightGray));
                if (mail.equals(""))
                    signUp.getMailField().setBorder(BorderFactory.createLineBorder(Color.red));
                else
                    signUp.getMailField().setBorder(BorderFactory.createLineBorder(Color.lightGray));
                if (motDePasse.equals("")) // Mot de passe vide
                    signUp.getMdpField().setBorder(BorderFactory.createLineBorder(Color.red));
                else
                    signUp.getMdpField().setBorder(BorderFactory.createLineBorder(Color.lightGray));
                throw new IOException("Les champs ne peuvent pas être vide");
            }
        } catch (SQLException e1)
        {
            new Notification(signUp, "Erreur", "L'email est déjà utilisé par un autre utilisateur");
        }
    }

    private void connexion()
    {
        mainView = new MainView();
        init();
        mainView.open();
    }

    private void initListSalles()
    {
        DefaultListModel<String> defaultListModel = new DefaultListModel<>();

        List<Salle> salles = Utils.getSalles();

        for (Salle salle : salles)
        {
            defaultListModel.addElement(salle.getNomSalle());
        }

        mainView.getListSalle().setModel(defaultListModel);
    }

    private void initListReservations(Salle salle)
    {
        DefaultTableModel defaultTableModel = new DefaultTableModel()
        {
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
            }
        };

        defaultTableModel.addColumn("Utilisateur");
        defaultTableModel.addColumn("Salle");
        defaultTableModel.addColumn("Début");
        defaultTableModel.addColumn("Fin");

        List<Reservation> reservations;
        if (salle == null)
            reservations = Utils.getAllReservations();
        else
            reservations = Utils.getReservations(salle);

        for (Reservation reservation : reservations)
        {
            Vector<String> data = new Vector<>();
            data.add(reservation.getUtilisateur().getMail());
            data.add(reservation.getSalle().getNomSalle());
            data.add(reservation.getDateDebut().toString() + " " + reservation.getHeureDebut().toString());
            data.add(reservation.getDateFin().toString() + " " + reservation.getHeureFin().toString());
            defaultTableModel.addRow(data);
        }

        mainView.getTableReservations().setModel(defaultTableModel);
        mainView.getTableReservations().setAutoCreateRowSorter(true);
        defaultTableModel.fireTableDataChanged();
    }

    private void addReservation()
    {
        Salle salle = Utils.getSalle(String.valueOf(mainView.getBoxSalle().getSelectedItem()));
        String jourDebut = String.valueOf(mainView.getBoxJourDebut().getSelectedItem());
        String heureDebut = String.valueOf(mainView.getBoxHeureDebut().getSelectedItem());
        String jourFin = String.valueOf(mainView.getBoxJourFin().getSelectedItem());
        String heurFin = String.valueOf(mainView.getBoxHeureFin().getSelectedItem());

        String[] debutJour = jourDebut.split("-");
        String[] finJour = jourFin.split("-");

        LocalDate dateDebut = LocalDate.of(Integer.valueOf(debutJour[0]), Integer.valueOf(debutJour[1]), Integer.valueOf(debutJour[2]));
        Date dateDeb = Date.valueOf(dateDebut);

        LocalDate dateFin = LocalDate.of(Integer.valueOf(finJour[0]), Integer.valueOf(finJour[1]), Integer.valueOf(finJour[2]));
        Date dateF = Date.valueOf(dateFin);

        Reservation reservation = new Reservation(utilisateur, salle, dateDeb, heureDebut, dateF, heurFin);
        try
        {
            utilisateur.addReservation(reservation);
            initBoxReservations();
            new Notification(mainView, "Ok", "La réservation a bien été ajoutée");
            initListReservations(null);
        } catch (Exception e)
        {
            new Notification(mainView, "Erreur", e.getMessage());
        }
    }

    private void initDaysHours()
    {
        for (int i = 0; i < 30; i++)
        {
            LocalDate date = LocalDate.now().plusDays(i);
            mainView.getBoxJourDebut().addItem(Date.valueOf(date.toString()).toString());
            mainView.getBoxJourFin().addItem(Date.valueOf(date.toString()).toString());
        }

        LocalTime time = LocalTime.of(7, 0);
        while ((time = time.plusHours(1)).getHour() <= 22)
        {
            mainView.getBoxHeureDebut().addItem(time.toString());
            if (time.getHour() < 22)
                mainView.getBoxHeureFin().addItem(time.plusHours(1).toString());
        }

        mainView.getBoxHeureDebut().addItemListener(e -> changeHours());

        mainView.getBoxJourDebut().addItemListener(e -> changeHours());

        mainView.getBoxJourFin().addItemListener(e -> changeHours());
    }

    private void changeHours()
    {
        if (Date.valueOf(String.valueOf(mainView.getBoxJourDebut().getSelectedItem())).after(Date.valueOf(String.valueOf(mainView.getBoxJourFin().getSelectedItem()))))
        {
            mainView.getBoxJourDebut().setSelectedItem(mainView.getBoxJourFin().getSelectedItem());
            new Notification(mainView, "Erreur", "L'heure de début de réservation ne peut pas être après l'heure de fin de réservation");
        }
        mainView.getBoxHeureFin().removeAllItems();
        if (String.valueOf(mainView.getBoxJourDebut().getSelectedItem()).equals(String.valueOf(mainView.getBoxJourFin().getSelectedItem())))
        {
            LocalTime inTime = LocalTime.parse((CharSequence) Objects.requireNonNull(mainView.getBoxHeureDebut().getSelectedItem()));
            if (inTime.equals(LocalTime.of(22, 0)))
                mainView.getBoxJourFin().setSelectedIndex(mainView.getBoxJourFin().getSelectedIndex() + 1);
            while ((inTime = inTime.plusHours(1)).getHour() <= 22)
            {
                mainView.getBoxHeureFin().addItem(inTime.toString());
            }
        } else
        {
            LocalTime inTime = LocalTime.of(7, 0);
            while ((inTime = inTime.plusHours(1)).getHour() <= 22)
            {
                mainView.getBoxHeureFin().addItem(inTime.toString());
            }
        }
    }

    private void initBoxSalles()
    {
        for (Salle salle : Utils.getSalles())
        {
            mainView.getBoxSalle().addItem(salle.getNomSalle());
        }
    }

    private void initBoxReservations()
    {
        mainView.getReservationBox().removeAllItems();
        for (Reservation reservation : utilisateur.getReservations())
        {
            mainView.getReservationBox().addItem(reservation);
        }
    }

    private void deleteReservation()
    {
        Reservation reservation = (Reservation) mainView.getReservationBox().getSelectedItem();

        try
        {
            utilisateur.deleteReservation(reservation);
            initBoxReservations();
            initListReservations(null);
            new Notification(mainView, "Ok", "La réservation a bien été supprimée");
        } catch (IOException e)
        {
            new Notification(mainView, "Erreur", e.getMessage());
        }
    }
}
