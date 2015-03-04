package com.jbake.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class SettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JComboBox mCountryComboBox;
    private JComboBox mLanguageComboBox;

    public SettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        // call onOK() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onOK() on ESCAPE
        contentPane.registerKeyboardAction(e -> onOK(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        List<Locale> availableLocales = Arrays.asList(Locale.getAvailableLocales());
        Stream<String> countries = availableLocales.stream().map(Locale::getCountry);
        countries.forEach(mCountryComboBox::addItem);

        Stream<String> languages = availableLocales.stream().map(Locale::getLanguage);
        languages.forEach(mLanguageComboBox::addItem);        
        
        mCountryComboBox.setSelectedItem(Settings.getInstance().getLocaleCountry());
        mLanguageComboBox.setSelectedItem(Settings.getInstance().getLocaleLanguage());

        mCountryComboBox.addItemListener(e -> {
            if(e.getStateChange() == 1) {
                System.out.println("Got one...");
                Settings.getInstance().setLocaleCountry((String) e.getItem());
            }
        });

        mLanguageComboBox.addItemListener(e -> {
            if(e.getStateChange() == 1) {
                System.out.println("Got one...");
                Settings.getInstance().setLocaleLanguage((String) e.getItem());
            }
        });
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void open() {
        SettingsDialog dialog = new SettingsDialog();
        dialog.setTitle("JBake UI - Settings");
        dialog.setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE));
        dialog.pack();
        dialog.setSize(new Dimension(500, 300));
        dialog.setVisible(true);
    }
}
