package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.EmptyBorder;
import java.io.File;

public class SymmetricalDecryptPanel extends JPanel {

    private JRadioButton decryptFileRadio;
    private JRadioButton decryptTextRadio;
    private JButton chooseFileButton;
    private JTextArea textInputArea;
    private JTextArea ivField;
    private JButton loadIVButton;
    private JTextArea secretKeyField;
    private JButton loadSecretKeyButton;
    private JButton decryptButton;
    private JPanel ivSection;
    private JLabel ivMessageLabel;

    private static final String TEXT_PLACEHOLDER = "Your text here...";
    private static final String IV_PLACEHOLDER = "Enter your initialization vector here...";
    private static final String SECRET_KEY_PLACEHOLDER = "Your secret key here...";

    public SymmetricalDecryptPanel() {
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

        JPanel ivLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        ivLabelPanel.add(new JLabel("Enter an initialization vector (IV), or"));
        loadIVButton = new JButton("load one");
        loadIVButton.setBackground(new Color(230, 230, 230));
        loadIVButton.setForeground(Color.BLACK);
        ivLabelPanel.add(loadIVButton);
        topRowPanel.add(ivLabelPanel);

        middleSection.add(topRowPanel, BorderLayout.NORTH);

        JPanel bottomRowPanel = new JPanel(new GridLayout(1, 2, 5, 0));

        textInputArea = createPlaceholderTextArea(TEXT_PLACEHOLDER);
        textInputArea.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(textInputArea);
        scrollPane.setPreferredSize(new Dimension(200, 60));
        bottomRowPanel.add(scrollPane);

        ivSection = new JPanel(new BorderLayout());

        ivField = createPlaceholderTextArea(IV_PLACEHOLDER);
        ivField.setRows(3);
        JScrollPane ivScrollPane = new JScrollPane(ivField);
        ivScrollPane.setPreferredSize(new Dimension(200, 60));

        ivMessageLabel = new JLabel("This mode doesn't use IV", SwingConstants.CENTER);
        ivMessageLabel.setForeground(Color.GRAY);
        ivMessageLabel.setVisible(false);

        ivSection.add(ivScrollPane, BorderLayout.CENTER);
        bottomRowPanel.add(ivSection);

        middleSection.add(bottomRowPanel, BorderLayout.CENTER);

        contentPanel.add(middleSection, BorderLayout.CENTER);

        JPanel bottomSection = new JPanel(new BorderLayout(2, 2));

        JPanel keyLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 2));
        keyLabelPanel.add(new JLabel("Next, load your key"));

        loadSecretKeyButton = new JButton("load secret key");
        loadSecretKeyButton.setBackground(new Color(230, 230, 230));
        loadSecretKeyButton.setForeground(Color.BLACK);
        keyLabelPanel.add(loadSecretKeyButton);

        keyLabelPanel.add(new JLabel(", or type in manually:"));
        bottomSection.add(keyLabelPanel, BorderLayout.NORTH);

        secretKeyField = createPlaceholderTextArea(SECRET_KEY_PLACEHOLDER);
        secretKeyField.setRows(2);
        JScrollPane secretKeyScrollPane = new JScrollPane(secretKeyField);
        secretKeyScrollPane.setPreferredSize(new Dimension(getWidth(), 48));
        bottomSection.add(secretKeyScrollPane, BorderLayout.CENTER);

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
        loadIVButton.addActionListener(e -> loadIV());
        loadSecretKeyButton.addActionListener(e -> loadSecretKey());
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

    public void updateIVPanel(String mode) {
        boolean requiresIV = mode != null && !"ECB".equals(mode) && !"none".equals(mode) && !"".equals(mode);

        if (requiresIV) {
            ivField.setEnabled(true);
            loadIVButton.setEnabled(true);
            ivField.setVisible(true);
            loadIVButton.setVisible(true);
            ivMessageLabel.setVisible(false);

            ivSection.removeAll();
            JScrollPane ivScrollPane = new JScrollPane(ivField);
            ivScrollPane.setPreferredSize(new Dimension(200, 60));
            ivSection.add(ivScrollPane, BorderLayout.CENTER);
        } else {
            ivField.setEnabled(false);
            loadIVButton.setEnabled(false);

            ivSection.removeAll();
            ivSection.add(ivMessageLabel, BorderLayout.CENTER);
            ivMessageLabel.setVisible(true);
        }

        ivSection.revalidate();
        ivSection.repaint();
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

    private void loadIV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Initialization Vector");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Loading IV from: " + selectedFile.getAbsolutePath());
        }
    }

    private void loadSecretKey() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Secret Key");
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Loading secret key from: " + selectedFile.getAbsolutePath());
        }
    }

    private void decrypt() {
        if (decryptFileRadio.isSelected()) {
            System.out.println("Decrypting file...");
        } else {
            String text = textInputArea.getText();
            if (!text.equals(TEXT_PLACEHOLDER)) {
                System.out.println("Decrypting text: " + text);
            }
        }

        String iv = ivField.getText();
        String secretKey = secretKeyField.getText();
        if (!iv.equals(IV_PLACEHOLDER) && ivField.isEnabled()) {
            System.out.println("Using IV: " + iv);
        }
        if (!secretKey.equals(SECRET_KEY_PLACEHOLDER)) {
            System.out.println("Using secret key: " + secretKey);
        }
    }
}