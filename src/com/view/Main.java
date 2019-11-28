package com.view;

import com.controller.Controller;

import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            e.printStackTrace();
        }
        new Controller();

    }
}