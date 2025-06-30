package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.EmptyBorder;
import java.io.File;

public class AsymmetricalDecryptPanel extends JPanel {

    private JRadioButton decryptFileRadio;
    private JRadioButton decryptTextRadio;
    private JButton chooseFileButton;
    private JTextArea textInputArea;
    private JTextArea outputField;
    private JTextArea privateKeyField;
    private JButton loadPrivateKeyButton;
    private JButton decryptButton;

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
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    private void loadPrivateKey() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Private Key");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Loading private key from: " + selectedFile.getAbsolutePath());
        }
    }

    private void decrypt() {
        String decryptedResult = "";

        if (decryptFileRadio.isSelected()) {
            System.out.println("Decrypting file...");
            decryptedResult = "File decryption result would appear here...";
        } else {
            String text = textInputArea.getText();
            if (!text.equals(TEXT_PLACEHOLDER)) {
                System.out.println("Decrypting text: " + text);
                decryptedResult = "Decrypted text result: " + text + " (decrypted)";
            }
        }

        String privateKey = privateKeyField.getText();
        if (!privateKey.equals(PRIVATE_KEY_PLACEHOLDER)) {
            System.out.println("Using private key: " + privateKey);
        }

        if (!decryptedResult.isEmpty()) {
            outputField.setText("----------------\ndecrypted result:\n" + decryptedResult);
        }
    }
}
