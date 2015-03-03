package com.jbake.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

public class SettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox mCountryComboBox;
    private JComboBox mLanguageComboBox;

    public SettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        Stream<ListItem> countries = Arrays.asList(Locale.getISOCountries()).stream().map(key -> new ListItem(key, new Locale("", key).getDisplayCountry()));
        countries.forEach(mCountryComboBox::addItem);

        Stream<ListItem> languages = Arrays.asList(Locale.getAvailableLocales()).stream().map(key -> new ListItem(key.getLanguage(), key.getDisplayLanguage()));
        languages.forEach(mLanguageComboBox::addItem);
        
        mLanguageComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ListItem item = (ListItem)e.getItem();
                System.out.println(item.key + " -> " + item.label);
            }
        });
    }
    
    private static class ListItem{
        public final String key;
        public final String label;

        private ListItem(String key, String label) {
            this.key = key;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void open() {
        SettingsDialog dialog = new SettingsDialog();
        dialog.pack();
        dialog.setSize(new Dimension(500, 300));
        dialog.setVisible(true);
    }
}
