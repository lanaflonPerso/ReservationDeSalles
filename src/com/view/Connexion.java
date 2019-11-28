package com.view;

import javax.swing.*;

public class Connexion extends JFrame
{
    private JPanel globalPanel;
    private JTextField mailField;
    private JPasswordField mdpField;
    private JButton seConnecterButton;
    private JLabel mdpForgot;
    private JLabel signUp;

    public Connexion()
    {
        setContentPane(globalPanel);
        setTitle("Connexion");
        mdpForgot.setText("<html><u>" + mdpForgot.getText() + "</u></html>");
        signUp.setText("<html><u>" + signUp.getText() + "</u></html>");
    }

    public void open()
    {
        pack();
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public JTextField getMailField()
    {
        return mailField;
    }

    public JPasswordField getMdpField()
    {
        return mdpField;
    }

    public JButton getSeConnecterButton()
    {
        return seConnecterButton;
    }

    public JLabel getMdpForgot()
    {
        return mdpForgot;
    }

    public JLabel getSignUp()
    {
        return signUp;
    }
}
