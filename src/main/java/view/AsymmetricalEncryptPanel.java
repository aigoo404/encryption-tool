package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.EmptyBorder;
import java.io.File;
import java.security.PublicKey;
import model.RSAUtil;

public class AsymmetricalEncryptPanel extends JPanel {

    private JRadioButton encryptFileRadio;
    private JRadioButton encryptTextRadio;
    private JButton chooseFileButton;
    private JTextArea textInputArea;
    private JTextArea outputField; 
    private JButton loadPublicKeyButton;
    private JTextArea publicKeyField;
    private JButton encryptButton;
    
    private File selectedFile;

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

        JPanel middleSection = new JPanel(new BorderLayout(2, 2));

        JPanel topRowPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        JPanel textRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        encryptTextRadio = new JRadioButton("Encrypt a text:");
        encryptTypeGroup.add(encryptTextRadio);
        textRadioPanel.add(encryptTextRadio);
        topRowPanel.add(textRadioPanel);

        JPanel outputLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        outputLabelPanel.add(new JLabel("Encryption output:"));
        topRowPanel.add(outputLabelPanel);

        middleSection.add(topRowPanel, BorderLayout.NORTH);

        JPanel bottomRowPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        textInputArea = createPlaceholderTextArea(TEXT_PLACEHOLDER);
        textInputArea.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(textInputArea);
        scrollPane.setPreferredSize(new Dimension(200, 60));
        bottomRowPanel.add(scrollPane);

        outputField = new JTextArea();
        outputField.setLineWrap(true);
        outputField.setWrapStyleWord(true);
        outputField.setBackground(new Color(245, 245, 245));
        outputField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        outputField.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        outputField.setEditable(false);
        outputField.setRows(3);
        JScrollPane outputScrollPane = new JScrollPane(outputField);
        outputScrollPane.setPreferredSize(new Dimension(200, 60));
        bottomRowPanel.add(outputScrollPane);

        middleSection.add(bottomRowPanel, BorderLayout.CENTER);

        contentPanel.add(middleSection, BorderLayout.CENTER);

        JPanel bottomSection = new JPanel(new BorderLayout(2, 2));
        JPanel keyLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        keyLabelPanel.add(new JLabel("Next, load your key"));

        loadPublicKeyButton = new JButton("load public key");
        loadPublicKeyButton.setBackground(new Color(230, 230, 230));
        loadPublicKeyButton.setForeground(Color.BLACK);
        keyLabelPanel.add(loadPublicKeyButton);

        keyLabelPanel.add(new JLabel(", or type in manually:"));
        bottomSection.add(keyLabelPanel, BorderLayout.NORTH);

        publicKeyField = createPlaceholderTextArea(PUBLIC_KEY_PLACEHOLDER);
        publicKeyField.setRows(2);
        JScrollPane publicKeyScrollPane = new JScrollPane(publicKeyField);
        publicKeyScrollPane.setPreferredSize(new Dimension(getWidth(), 48));
        bottomSection.add(publicKeyScrollPane, BorderLayout.CENTER);

        JPanel encryptButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        encryptButtonPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        encryptButton = new JButton("Encrypt!");
        encryptButton.setPreferredSize(new Dimension(100, 30));
        encryptButton.setBackground(new Color(200, 200, 200));
        encryptButtonPanel.add(encryptButton);
        bottomSection.add(encryptButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomSection, BorderLayout.SOUTH);

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
        fileChooser.setDialogTitle("Select File to Encrypt");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            chooseFileButton.setText(selectedFile.getName());
            JOptionPane.showMessageDialog(this, "File selected: " + selectedFile.getName(), "File Selected", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadPublicKey() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Public Key");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedKeyFile = fileChooser.getSelectedFile();
            try {
                PublicKey publicKey = RSAUtil.loadPublicKeyFromFile(selectedKeyFile.getAbsolutePath());
                String publicKeyBase64 = RSAUtil.keyToBase64(publicKey);
                publicKeyField.setText(publicKeyBase64);
                publicKeyField.setForeground(Color.BLACK);
                JOptionPane.showMessageDialog(this, "Public key loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading public key: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void encrypt() {
        try {
            String publicKeyText = publicKeyField.getText().trim();
            if (publicKeyText.isEmpty() || publicKeyText.equals(PUBLIC_KEY_PLACEHOLDER)) {
                JOptionPane.showMessageDialog(this, "Please load or enter a public key first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (encryptFileRadio.isSelected()) {
                if (selectedFile == null) {
                    JOptionPane.showMessageDialog(this, "Please select a file to encrypt.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                int result = JOptionPane.showConfirmDialog(this, 
                    "The original file will be overwritten.\nDo you want to continue?", 
                    "Confirm Encryption", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);
                
                if (result == JOptionPane.YES_OPTION) {
                    RSAUtil.encryptFileInPlace(selectedFile.getAbsolutePath(), publicKeyText);
                    outputField.setText("File encrypted successfully in place: " + selectedFile.getAbsolutePath());
                }
            } else if (encryptTextRadio.isSelected()) {
                String inputText = textInputArea.getText().trim();
                if (inputText.isEmpty() || inputText.equals(TEXT_PLACEHOLDER)) {
                    JOptionPane.showMessageDialog(this, "Please enter text to encrypt.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String encryptedText = RSAUtil.encrypt(inputText, publicKeyText);
                outputField.setText(encryptedText);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Encryption failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            outputField.setText("Encryption failed: " + e.getMessage());
        }
    }
}
