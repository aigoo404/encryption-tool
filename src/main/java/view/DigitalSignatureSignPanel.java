package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.EmptyBorder;
import java.io.File;
import model.digitalSignature;

public class DigitalSignatureSignPanel extends JPanel {

    private JButton chooseFileButton;
    private JButton loadPrivateKeyButton;
    private JTextArea privateKeyField;
    private JTextArea signatureOutputArea;
    private JButton signButton;
    private JButton saveToFileButton;
    private JButton embedToFileButton;
    
    private JRadioButton alreadyHashedRadio;
    private JRadioButton notHashedRadio;
    private ButtonGroup hashGroup;

    private File selectedFile;

    private static final String PRIVATE_KEY_PLACEHOLDER = "Your private key here...";

    public DigitalSignatureSignPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel keySection = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        keySection.add(new JLabel("First, load your private key:"));
        loadPrivateKeyButton = new JButton("Load key");
        loadPrivateKeyButton.setBackground(new Color(230, 230, 230));
        loadPrivateKeyButton.setForeground(Color.BLACK);
        keySection.add(loadPrivateKeyButton);
        mainPanel.add(keySection, BorderLayout.NORTH);

        JPanel hashSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        hashSection.add(new JLabel("Is your file already hashed?"));
        alreadyHashedRadio = new JRadioButton("Yes, it's already hashed");
        notHashedRadio = new JRadioButton("No, hash it for me (SHA256withRSA)", true);
        
        hashGroup = new ButtonGroup();
        hashGroup.add(alreadyHashedRadio);
        hashGroup.add(notHashedRadio);
        
        hashSection.add(alreadyHashedRadio);
        hashSection.add(notHashedRadio);
        
        JPanel signSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        signSection.add(new JLabel("Next, choose what you want to sign:"));
        chooseFileButton = new JButton("Choose file");
        chooseFileButton.setBackground(new Color(230, 230, 230));
        chooseFileButton.setForeground(Color.BLACK);
        signSection.add(chooseFileButton);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(hashSection, BorderLayout.NORTH);
        centerPanel.add(signSection, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel actionSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        actionSection.add(new JLabel("Done?"));
        signButton = new JButton("Sign it!");
        signButton.setBackground(new Color(200, 200, 200));
        signButton.setForeground(Color.BLACK);
        actionSection.add(signButton);
        mainPanel.add(actionSection, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.NORTH);

        JPanel outputSection = new JPanel(new BorderLayout(5, 5));
        outputSection.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel signatureLabel = new JLabel("Here is your signature:");
        outputSection.add(signatureLabel, BorderLayout.NORTH);

        signatureOutputArea = new JTextArea();
        signatureOutputArea.setLineWrap(true);
        signatureOutputArea.setWrapStyleWord(true);
        signatureOutputArea.setBackground(new Color(245, 245, 245));
        signatureOutputArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        signatureOutputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        signatureOutputArea.setEditable(false);
        signatureOutputArea.setRows(8);
        outputSection.add(signatureOutputArea, BorderLayout.CENTER);

        JPanel bottomActionSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        bottomActionSection.setBorder(new EmptyBorder(10, 0, 0, 0));
        bottomActionSection.add(new JLabel("Choose your next action:"));
        
        saveToFileButton = new JButton("Save it to a file");
        saveToFileButton.setBackground(new Color(230, 230, 230));
        saveToFileButton.setForeground(Color.BLACK);
        bottomActionSection.add(saveToFileButton);
        
        bottomActionSection.add(new JLabel(", or"));
        
        embedToFileButton = new JButton("Embed it to the file");
        embedToFileButton.setBackground(new Color(230, 230, 230));
        embedToFileButton.setForeground(Color.BLACK);
        bottomActionSection.add(embedToFileButton);
        
        outputSection.add(bottomActionSection, BorderLayout.SOUTH);
        
        add(outputSection, BorderLayout.CENTER);

        privateKeyField = createPlaceholderTextArea(PRIVATE_KEY_PLACEHOLDER);

        chooseFileButton.addActionListener(e -> chooseFile());
        loadPrivateKeyButton.addActionListener(e -> loadPrivateKey());
        signButton.addActionListener(e -> signData());
        saveToFileButton.addActionListener(e -> saveSignatureToFile());
        embedToFileButton.addActionListener(e -> embedSignatureToFile());
    }

    private void signData() {
        try {
            String privateKeyText = privateKeyField.getText().trim();
            if (privateKeyText.isEmpty() || privateKeyText.equals(PRIVATE_KEY_PLACEHOLDER)) {
                JOptionPane.showMessageDialog(this, "Please load a private key first.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            if (selectedFile == null) {
                JOptionPane.showMessageDialog(this, "Please select a file to sign.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
    
            boolean isAlreadyHashed = alreadyHashedRadio.isSelected();
            String signature = digitalSignature.signFile(selectedFile.getAbsolutePath(), privateKeyText, isAlreadyHashed);
            signatureOutputArea.setText(signature);
    
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Signing failed: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            signatureOutputArea.setText("Signing failed: " + e.getMessage());
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
        fileChooser.setDialogTitle("Select file to sign");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            chooseFileButton.setText(selectedFile.getName());
        }
    }

    private void loadPrivateKey() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Private Key");
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter("Key files (*.pem, *.key)", "pem", "key"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File keyFile = fileChooser.getSelectedFile();
                String keyContent = new String(java.nio.file.Files.readAllBytes(keyFile.toPath()));
                privateKeyField.setText(keyContent);
                privateKeyField.setForeground(Color.BLACK);
                loadPrivateKeyButton.setText("Key loaded: " + keyFile.getName());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to load private key: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveSignatureToFile() {
        String signature = signatureOutputArea.getText().trim();
        if (signature.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No signature to save. Please sign data first.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Signature");
        fileChooser.setSelectedFile(new File("signature.sig"));
        fileChooser.setFileFilter(
                new javax.swing.filechooser.FileNameExtensionFilter("Signature files (*.sig)", "sig"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File outputFile = fileChooser.getSelectedFile();
                if (!outputFile.getName().toLowerCase().endsWith(".sig")) {
                    outputFile = new File(outputFile.getAbsolutePath() + ".sig");
                }
                digitalSignature.saveSignatureToFile(signature, outputFile.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "Signature saved successfully to: " + outputFile.getAbsolutePath(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Failed to save signature: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void embedSignatureToFile() {
        String signature = signatureOutputArea.getText().trim();
        if (signature.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No signature to embed. Please sign data first.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "No file selected to embed signature into.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String originalPath = selectedFile.getAbsolutePath();
            String signedFilePath;
            
            int lastDotIndex = originalPath.lastIndexOf('.');
            if (lastDotIndex > 0) {
                signedFilePath = originalPath.substring(0, lastDotIndex) + ".signed" + originalPath.substring(lastDotIndex);
            } else {
                signedFilePath = originalPath + ".signed";
            }
            
            byte[] originalContent = java.nio.file.Files.readAllBytes(selectedFile.toPath());
            String embeddedContent = new String(originalContent) + "\n\n--- DIGITAL SIGNATURE ---\n" + signature
                    + "\n--- END SIGNATURE ---";
    
            java.nio.file.Files.write(java.nio.file.Paths.get(signedFilePath), embeddedContent.getBytes());
    
            JOptionPane.showMessageDialog(this,
                    "Signature embedded successfully into new file: " + signedFilePath + "\n\nOriginal file remains unchanged for verification.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to embed signature: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
