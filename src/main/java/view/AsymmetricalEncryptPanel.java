package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.EmptyBorder;
import java.io.File;

public class AsymmetricalEncryptPanel extends JPanel {

    private JRadioButton encryptFileRadio;
    private JRadioButton encryptTextRadio;
    private JButton chooseFileButton;
    private JTextArea textInputArea;
    private JButton loadPublicKeyButton;
    private JTextArea publicKeyField;
    private JButton encryptButton;

    private static final String TEXT_PLACEHOLDER = "Your text here...";
    private static final String PUBLIC_KEY_PLACEHOLDER = "Your public key here...";

    public AsymmetricalEncryptPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(2, 2, 2, 2));

        JPanel contentPanel = new JPanel(new BorderLayout(2, 2));

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        ButtonGroup encryptTypeGroup = new ButtonGroup();

        encryptFileRadio = new JRadioButton("Encrypt a file");
        encryptFileRadio.setSelected(true);
        encryptTypeGroup.add(encryptFileRadio);
        radioPanel.add(encryptFileRadio);

        chooseFileButton = new JButton("choose file");
        chooseFileButton.setBackground(new Color(230, 230, 230));
        chooseFileButton.setForeground(Color.BLACK);
        radioPanel.add(chooseFileButton);

        radioPanel.add(new JLabel(", or"));

        contentPanel.add(radioPanel, BorderLayout.NORTH);

        JPanel textPanel = new JPanel(new BorderLayout(2, 2));
        JPanel textRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));

        encryptTextRadio = new JRadioButton("Encrypt a text:");
        encryptTypeGroup.add(encryptTextRadio);
        textRadioPanel.add(encryptTextRadio);

        textPanel.add(textRadioPanel, BorderLayout.NORTH);

        textInputArea = createPlaceholderTextArea(TEXT_PLACEHOLDER);
        textInputArea.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(textInputArea);
        scrollPane.setPreferredSize(new Dimension(getWidth(), 60));
        textPanel.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(textPanel, BorderLayout.CENTER);

        JPanel keyPanel = new JPanel(new BorderLayout(2, 2));
        JPanel keyLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        keyLabelPanel.add(new JLabel("Next, load your key"));

        loadPublicKeyButton = new JButton("load public key");
        loadPublicKeyButton.setBackground(new Color(230, 230, 230));
        loadPublicKeyButton.setForeground(Color.BLACK);
        keyLabelPanel.add(loadPublicKeyButton);

        keyLabelPanel.add(new JLabel(", or type in manually:"));
        keyPanel.add(keyLabelPanel, BorderLayout.NORTH);

        publicKeyField = createPlaceholderTextArea(PUBLIC_KEY_PLACEHOLDER);
        publicKeyField.setRows(2);
        JScrollPane publicKeyScrollPane = new JScrollPane(publicKeyField);
        publicKeyScrollPane.setPreferredSize(new Dimension(getWidth(), 48));
        keyPanel.add(publicKeyScrollPane, BorderLayout.CENTER);

        JPanel encryptButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        encryptButtonPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        encryptButton = new JButton("Encrypt!");
        encryptButton.setPreferredSize(new Dimension(100, 30));
        encryptButton.setBackground(new Color(200, 200, 200));
        encryptButtonPanel.add(encryptButton);
        keyPanel.add(encryptButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(keyPanel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        chooseFileButton.addActionListener(e -> chooseFile());
        loadPublicKeyButton.addActionListener(e -> loadPublicKey());
        encryptButton.addActionListener(e -> encrypt());

        encryptFileRadio.addActionListener(e -> {
            chooseFileButton.setEnabled(true);
            textInputArea.setEnabled(false);
        });

        encryptTextRadio.addActionListener(e -> {
            chooseFileButton.setEnabled(false);
            textInputArea.setEnabled(true);
        });
    }

    private JTextArea createPlaceholderTextArea(String placeholder) {
        JTextArea textArea = new JTextArea(placeholder);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(new Color(245, 245, 245));
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        textArea.setForeground(Color.GRAY);

        textArea.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textArea.getText().equals(placeholder)) {
                    textArea.setText("");
                    textArea.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textArea.getText().isEmpty()) {
                    textArea.setText(placeholder);
                    textArea.setForeground(Color.GRAY);
                }
            }
        });

        return textArea;
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    private void loadPublicKey() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Public Key");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Loading public key from: " + selectedFile.getAbsolutePath());
        }
    }

    private void encrypt() {
        if (encryptFileRadio.isSelected()) {
            System.out.println("Encrypting file...");
        } else {
            String text = textInputArea.getText();
            if (!text.equals(TEXT_PLACEHOLDER)) {
                System.out.println("Encrypting text: " + text);
            }
        }

        String publicKey = publicKeyField.getText();
        if (!publicKey.equals(PUBLIC_KEY_PLACEHOLDER)) {
            System.out.println("Using public key: " + publicKey);
        }
    }
}
