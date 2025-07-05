package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.EmptyBorder;
import java.io.File;
import java.security.PrivateKey;
import model.RSAUtil;

public class AsymmetricalDecryptPanel extends JPanel {

    private JRadioButton decryptFileRadio;
    private JRadioButton decryptTextRadio;
    private JButton chooseFileButton;
    private JTextArea textInputArea;
    private JTextArea outputField;
    private JTextArea privateKeyField;
    private JButton loadPrivateKeyButton;
    private JButton decryptButton;

    private File selectedFile;

    private static final String TEXT_PLACEHOLDER = "Your encrypted text here...";
    private static final String PRIVATE_KEY_PLACEHOLDER = "Your private key here...";

    public AsymmetricalDecryptPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(2, 2, 2, 2));

        JPanel contentPanel = new JPanel(new BorderLayout(2, 2));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        ButtonGroup decryptTypeGroup = new ButtonGroup();

        decryptFileRadio = new JRadioButton("Decrypt a file");
        decryptFileRadio.setSelected(true);
        decryptTypeGroup.add(decryptFileRadio);
        filePanel.add(decryptFileRadio);

        chooseFileButton = new JButton("choose file");
        chooseFileButton.setBackground(new Color(230, 230, 230));
        chooseFileButton.setForeground(Color.BLACK);
        filePanel.add(chooseFileButton);

        filePanel.add(new JLabel(", or"));

        contentPanel.add(filePanel, BorderLayout.NORTH);

        JPanel middleSection = new JPanel(new BorderLayout(2, 2));

        JPanel topRowPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        JPanel textRadioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        decryptTextRadio = new JRadioButton("Decrypt a text:");
        decryptTypeGroup.add(decryptTextRadio);
        textRadioPanel.add(decryptTextRadio);
        topRowPanel.add(textRadioPanel);

        JPanel outputLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        outputLabelPanel.add(new JLabel("Decryption output:"));
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

        loadPrivateKeyButton = new JButton("load private key");
        loadPrivateKeyButton.setBackground(new Color(230, 230, 230));
        loadPrivateKeyButton.setForeground(Color.BLACK);
        keyLabelPanel.add(loadPrivateKeyButton);

        keyLabelPanel.add(new JLabel(", or type in manually:"));
        bottomSection.add(keyLabelPanel, BorderLayout.NORTH);

        privateKeyField = createPlaceholderTextArea(PRIVATE_KEY_PLACEHOLDER);
        privateKeyField.setRows(2);
        JScrollPane privateKeyScrollPane = new JScrollPane(privateKeyField);
        privateKeyScrollPane.setPreferredSize(new Dimension(getWidth(), 48));
        bottomSection.add(privateKeyScrollPane, BorderLayout.CENTER);

        JPanel decryptButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
        decryptButtonPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
        decryptButton = new JButton("Decrypt!");
        decryptButton.setPreferredSize(new Dimension(100, 30));
        decryptButton.setBackground(new Color(200, 200, 200));
        decryptButtonPanel.add(decryptButton);
        bottomSection.add(decryptButtonPanel, BorderLayout.SOUTH);

        contentPanel.add(bottomSection, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);

        chooseFileButton.addActionListener(e -> chooseFile());
        loadPrivateKeyButton.addActionListener(e -> loadPrivateKey());
        decryptButton.addActionListener(e -> decrypt());

        decryptFileRadio.addActionListener(e -> {
            chooseFileButton.setEnabled(true);
            textInputArea.setEnabled(false);
        });

        decryptTextRadio.addActionListener(e -> {
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
        fileChooser.setDialogTitle("Select Encrypted File to Decrypt");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            chooseFileButton.setText(selectedFile.getName());
            JOptionPane.showMessageDialog(this, "File selected: " + selectedFile.getName(), "File Selected",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadPrivateKey() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Private Key");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedKeyFile = fileChooser.getSelectedFile();
            try {
                PrivateKey privateKey = RSAUtil.loadPrivateKeyFromFile(selectedKeyFile.getAbsolutePath());
                String privateKeyBase64 = RSAUtil.keyToBase64(privateKey);
                privateKeyField.setText(privateKeyBase64);
                privateKeyField.setForeground(Color.BLACK);
                JOptionPane.showMessageDialog(this, "Private key loaded successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading private key: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void decrypt() {
        try {
            String privateKeyText = privateKeyField.getText().trim();
            if (privateKeyText.isEmpty() || privateKeyText.equals(PRIVATE_KEY_PLACEHOLDER)) {
                JOptionPane.showMessageDialog(this, "Please load or enter a private key first.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (decryptFileRadio.isSelected()) {
                if (selectedFile == null) {
                    JOptionPane.showMessageDialog(this, "Please select a file to decrypt.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int result = JOptionPane.showConfirmDialog(this,
                        "The original file will be overwritten.\nDo you want to continue?",
                        "Confirm Decryption",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    RSAUtil.decryptFileInPlace(selectedFile.getAbsolutePath(), privateKeyText);
                    outputField.setText("File decrypted successfully in place: " + selectedFile.getAbsolutePath());
                }
            } else if (decryptTextRadio.isSelected()) {
                String inputText = textInputArea.getText().trim();
                if (inputText.isEmpty() || inputText.equals(TEXT_PLACEHOLDER)) {
                    JOptionPane.showMessageDialog(this, "Please enter text to decrypt.", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String decryptedText = RSAUtil.decrypt(inputText, privateKeyText);
                outputField.setText(decryptedText);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Decryption failed: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            outputField.setText("Decryption failed: " + e.getMessage());
        }
    }
}