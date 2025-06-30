package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.EmptyBorder;
import java.io.File;

public class AsymmetricalGenkeyPanel extends JPanel {

    private JComboBox<String> algorithmCombo;
    private JComboBox<String> modeCombo;
    private JComboBox<String> keySizeCombo;
    private JComboBox<String> paddingCombo;

    private JTextArea publicKeyArea;
    private JTextArea privateKeyArea;

    private JButton savePublicKeyButton;
    private JButton savePrivateKeyButton;

    public AsymmetricalGenkeyPanel(boolean createComboBoxes) {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));

        if (createComboBoxes) {
            JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 5));

            JPanel encryptionTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            encryptionTypePanel.add(new JLabel("Choose encryption type:"));
            JComboBox<String> encryptionTypeCombo = new JComboBox<>(new String[] { "Asymmetric" });
            encryptionTypeCombo.setPreferredSize(new Dimension(100, 24));
            encryptionTypePanel.add(encryptionTypeCombo);
            topPanel.add(encryptionTypePanel);

            JPanel algorithmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

            algorithmPanel.add(new JLabel("Choose algorithm:"));
            algorithmCombo = new JComboBox<>(new String[] { "RSA" });
            algorithmCombo.setPreferredSize(new Dimension(80, 24));
            algorithmPanel.add(algorithmCombo);

            algorithmPanel.add(new JLabel("Mode:"));
            modeCombo = new JComboBox<>(new String[] { "none" });
            modeCombo.setPreferredSize(new Dimension(80, 24));
            algorithmPanel.add(modeCombo);

            algorithmPanel.add(new JLabel("Key size (bits):"));
            keySizeCombo = new JComboBox<>(new String[] { "1024", "2048", "4096" });
            keySizeCombo.setPreferredSize(new Dimension(80, 24));
            algorithmPanel.add(keySizeCombo);

            algorithmPanel.add(new JLabel("Padding:"));
            paddingCombo = new JComboBox<>(new String[] { "No padding", "PKCS1Padding" });
            paddingCombo.setPreferredSize(new Dimension(100, 24));
            algorithmPanel.add(paddingCombo);

            topPanel.add(algorithmPanel);
            add(topPanel, BorderLayout.NORTH);
        }

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel generateKeyPanel = createKeyGenerationPanel();
        tabbedPane.addTab("Generate Key", generateKeyPanel);

        AsymmetricalEncryptPanel encryptPanel = new AsymmetricalEncryptPanel();
        tabbedPane.addTab("Encrypt", encryptPanel);

        AsymmetricalDecryptPanel decryptPanel = new AsymmetricalDecryptPanel();
        tabbedPane.addTab("Decrypt", decryptPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public AsymmetricalGenkeyPanel() {
        this(true);
    }

    private static final String PUBLIC_KEY_PLACEHOLDER = "Enter your public key here";
    private static final String PRIVATE_KEY_PLACEHOLDER = "Enter your private key here";

    private JPanel createKeyGenerationPanel() {
        JPanel keyGenPanel = new JPanel(new BorderLayout());

        JPanel keyGenLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        keyGenLabelPanel.add(new JLabel("Create RSA public/private key:"));
        JButton generateKeyButton = new JButton("Generate Key");

        generateKeyButton.setPreferredSize(new Dimension(generateKeyButton.getPreferredSize().width, 24));
        keyGenLabelPanel.add(generateKeyButton);
        keyGenLabelPanel.add(new JLabel(", or type in manually:"));
        keyGenPanel.add(keyGenLabelPanel, BorderLayout.NORTH);

        JPanel keyDisplayPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel publicKeyPanel = new JPanel(new BorderLayout());
        publicKeyPanel.add(new JLabel("Public key:"), BorderLayout.NORTH);
        publicKeyArea = createPlaceholderTextArea(PUBLIC_KEY_PLACEHOLDER);
        JScrollPane publicKeyScroll = new JScrollPane(publicKeyArea);
        publicKeyScroll.setPreferredSize(new Dimension(269, 120));
        publicKeyPanel.add(publicKeyScroll, BorderLayout.CENTER);

        savePublicKeyButton = new JButton("Save public key");
        savePublicKeyButton.setMargin(new Insets(2, 8, 2, 8));
        savePublicKeyButton.addActionListener(e -> openSaveDialog("public"));

        JPanel publicButtonWrapper = new JPanel();
        publicButtonWrapper.setLayout(new BoxLayout(publicButtonWrapper, BoxLayout.Y_AXIS));

        JPanel publicButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        publicButtonPanel.add(savePublicKeyButton);
        publicButtonWrapper.add(publicButtonPanel);
        publicButtonWrapper.add(Box.createVerticalStrut(5));

        publicKeyPanel.add(publicButtonWrapper, BorderLayout.SOUTH);

        JPanel privateKeyPanel = new JPanel(new BorderLayout());
        privateKeyPanel.add(new JLabel("Private key:"), BorderLayout.NORTH);
        privateKeyArea = createPlaceholderTextArea(PRIVATE_KEY_PLACEHOLDER);
        JScrollPane privateKeyScroll = new JScrollPane(privateKeyArea);
        privateKeyScroll.setPreferredSize(new Dimension(269, 120));
        privateKeyPanel.add(privateKeyScroll, BorderLayout.CENTER);

        savePrivateKeyButton = new JButton("Save private key");
        savePrivateKeyButton.setMargin(new Insets(2, 8, 2, 8));
        savePrivateKeyButton.addActionListener(e -> openSaveDialog("private"));

        JPanel privateButtonWrapper = new JPanel();
        privateButtonWrapper.setLayout(new BoxLayout(privateButtonWrapper, BoxLayout.Y_AXIS));

        JPanel privateButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
        privateButtonPanel.add(savePrivateKeyButton);
        privateButtonWrapper.add(privateButtonPanel);
        privateButtonWrapper.add(Box.createVerticalStrut(5));

        privateKeyPanel.add(privateButtonWrapper, BorderLayout.SOUTH);

        keyDisplayPanel.add(publicKeyPanel);
        keyDisplayPanel.add(privateKeyPanel);

        keyGenPanel.add(keyDisplayPanel, BorderLayout.CENTER);

        return keyGenPanel;
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

    private void openSaveDialog(String keyType) {
        JTextArea keyArea = keyType.equals("public") ? publicKeyArea : privateKeyArea;
        String placeholder = keyType.equals("public") ? PUBLIC_KEY_PLACEHOLDER : PRIVATE_KEY_PLACEHOLDER;

        if (keyArea.getText().equals(placeholder)) {
            JOptionPane.showMessageDialog(this, "No " + keyType + " key to save. Please generate or enter a key first.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save " + keyType + " key");

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            System.out.println("Save " + keyType + " key to: " + fileToSave.getAbsolutePath());
        }
    }
}
