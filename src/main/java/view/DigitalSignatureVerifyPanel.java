package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.EmptyBorder;
import java.io.File;
import model.digitalSignature;
import model.DSAUtil;
import controller.MainFrame;

public class DigitalSignatureVerifyPanel extends JPanel {

    private JButton chooseFileButton;
    private JButton loadPublicKeyButton;
    private JButton loadSignatureButton;
    private JTextArea publicKeyField;
    private JTextArea signatureInputArea;
    private JTextArea verificationResultArea;
    private JButton verifyButton;

    private JRadioButton alreadyHashedRadio;
    private JRadioButton notHashedRadio;
    private ButtonGroup hashGroup;

    private File selectedFile;
    private String loadedSignature;

    private static final String PUBLIC_KEY_PLACEHOLDER = "Your public key here...";
    private static final String SIGNATURE_PLACEHOLDER = "Signature to verify...";

    public DigitalSignatureVerifyPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel keySection = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        keySection.add(new JLabel("First, load your public key:"));
        loadPublicKeyButton = new JButton("Load key");
        loadPublicKeyButton.setBackground(new Color(230, 230, 230));
        loadPublicKeyButton.setForeground(Color.BLACK);
        keySection.add(loadPublicKeyButton);
        mainPanel.add(keySection, BorderLayout.NORTH);

        JPanel hashSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        hashSection.add(new JLabel("Is your file already hashed?"));

        alreadyHashedRadio = new JRadioButton("Yes, it's already hashed");
        notHashedRadio = new JRadioButton("No, hash it for me", true);

        hashGroup = new ButtonGroup();
        hashGroup.add(alreadyHashedRadio);
        hashGroup.add(notHashedRadio);

        hashSection.add(alreadyHashedRadio);
        hashSection.add(notHashedRadio);

        JPanel signSection = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        filePanel.add(new JLabel("Next, choose the file to verify:"));
        chooseFileButton = new JButton("Choose file");
        chooseFileButton.setBackground(new Color(230, 230, 230));
        chooseFileButton.setForeground(Color.BLACK);
        filePanel.add(chooseFileButton);
        signSection.add(filePanel);

        JPanel sigPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        sigPanel.add(new JLabel("Then, load the signature:"));
        loadSignatureButton = new JButton("Load signature");
        loadSignatureButton.setBackground(new Color(230, 230, 230));
        loadSignatureButton.setForeground(Color.BLACK);
        sigPanel.add(loadSignatureButton);
        signSection.add(sigPanel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(hashSection, BorderLayout.NORTH);
        centerPanel.add(signSection, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel actionSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        actionSection.add(new JLabel("Done?"));
        verifyButton = new JButton("Verify!");
        verifyButton.setBackground(new Color(200, 200, 200));
        verifyButton.setForeground(Color.BLACK);
        actionSection.add(verifyButton);
        mainPanel.add(actionSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.NORTH);

        JPanel outputSection = new JPanel(new BorderLayout(5, 5));
        outputSection.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel resultLabel = new JLabel("Verification result:");
        outputSection.add(resultLabel, BorderLayout.NORTH);

        verificationResultArea = new JTextArea();
        verificationResultArea.setLineWrap(true);
        verificationResultArea.setWrapStyleWord(true);
        verificationResultArea.setBackground(new Color(245, 245, 245));
        verificationResultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        verificationResultArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        verificationResultArea.setEditable(false);
        verificationResultArea.setRows(4);
        outputSection.add(verificationResultArea, BorderLayout.CENTER);

        add(outputSection, BorderLayout.CENTER);

        publicKeyField = createPlaceholderTextArea(PUBLIC_KEY_PLACEHOLDER);
        signatureInputArea = createPlaceholderTextArea(SIGNATURE_PLACEHOLDER);

        chooseFileButton.addActionListener(e -> chooseFile());
        loadPublicKeyButton.addActionListener(e -> loadPublicKey());
        loadSignatureButton.addActionListener(e -> loadSignature());
        verifyButton.addActionListener(e -> verifySignature());
    }

    public void updateAlgorithmOptions(String algorithm) {
        if ("DSA".equals(algorithm)) {
            alreadyHashedRadio.setEnabled(false);
            notHashedRadio.setSelected(true);
            alreadyHashedRadio.setToolTipText("DSA doesn't support raw hash verification");
        } else {
            alreadyHashedRadio.setEnabled(true);
            alreadyHashedRadio.setToolTipText(null);
        }
    }

    private void verifySignature() {
        try {
            String publicKeyText = publicKeyField.getText().trim();
            if (publicKeyText.isEmpty() || publicKeyText.equals(PUBLIC_KEY_PLACEHOLDER)) {
                JOptionPane.showMessageDialog(this, "Please load a public key first.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this, "Please select a file to verify.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (loadedSignature == null || loadedSignature.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please load a signature to verify.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
            String selectedAlgorithm = mainFrame.getSelectedAlgorithm();

            boolean isAlreadyHashed = alreadyHashedRadio.isSelected();
            boolean isValid;

            if ("DSA".equals(selectedAlgorithm)) {
                isValid = DSAUtil.verifyFile(selectedFile.getAbsolutePath(), loadedSignature, publicKeyText);
            } else {
                isValid = digitalSignature.verifyFile(selectedFile.getAbsolutePath(), loadedSignature, publicKeyText,
                        isAlreadyHashed);
            }

            if (isValid) {
                verificationResultArea.setText("✓ Signature is VALID");
                verificationResultArea.setForeground(new Color(0, 128, 0));
            } else {
                verificationResultArea.setText("✗ Signature is INVALID");
                verificationResultArea.setForeground(Color.RED);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Verification failed: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            verificationResultArea.setText("Verification failed: " + e.getMessage());
            verificationResultArea.setForeground(Color.RED);
        }
    }

    private JTextArea createPlaceholderTextArea(String placeholder) {
        JTextArea textArea = new JTextArea();
        textArea.setText(placeholder);
        textArea.setForeground(Color.GRAY);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

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
        fileChooser.setDialogTitle("Select file to verify");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            chooseFileButton.setText(selectedFile.getName());
        }
    }

    private void loadPublicKey() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Public Key");
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter("Key files (*.pem, *.key)", "pem", "key"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File keyFile = fileChooser.getSelectedFile();
                String keyContent = new String(java.nio.file.Files.readAllBytes(keyFile.toPath()));
                publicKeyField.setText(keyContent);
                publicKeyField.setForeground(Color.BLACK);
                loadPublicKeyButton.setText("Key loaded: " + keyFile.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load public key: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadSignature() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Signature");
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter("Signature files (*.sig)", "sig"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File sigFile = fileChooser.getSelectedFile();
                loadedSignature = digitalSignature.loadSignatureFromFile(sigFile.getAbsolutePath());
                signatureInputArea.setText(loadedSignature);
                signatureInputArea.setForeground(Color.BLACK);
                loadSignatureButton.setText("Signature loaded: " + sigFile.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load signature: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}