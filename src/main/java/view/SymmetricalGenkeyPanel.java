package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.border.EmptyBorder;
import java.io.File;
import model.AESUtil;

public class SymmetricalGenkeyPanel extends JPanel {

    private JTextArea secretKeyArea;
    private JTextArea ivArea;
    private JButton generateKeyButton;
    private JButton saveSecretKeyButton;
    private JButton saveIVButton;
    private JButton generateIVButton;
    private JPanel ivPanel;
    private JLabel ivMessageLabel;
    private JTabbedPane tabbedPane;

    private static final String SECRET_KEY_PLACEHOLDER = "Enter your secret key here";
    private static final String IV_PLACEHOLDER = "Enter your IV here";

    public SymmetricalGenkeyPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));

        tabbedPane = new JTabbedPane();

        JPanel generateKeyTab = createGenerateKeyTab();
        tabbedPane.addTab("Generate Key", generateKeyTab);

        SymmetricalEncryptPanel encryptTab = new SymmetricalEncryptPanel();
        tabbedPane.addTab("Encrypt", encryptTab);

        SymmetricalDecryptPanel decryptTab = new SymmetricalDecryptPanel();
        tabbedPane.addTab("Decrypt", decryptTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createGenerateKeyTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel topSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        topSection.add(new JLabel("Create a secret key:"));

        generateKeyButton = new JButton("Generate Key");
        generateKeyButton.setPreferredSize(new Dimension(100, 25));
        generateKeyButton.setBackground(Color.white);
        generateKeyButton.setForeground(Color.black);
        generateKeyButton.setFocusPainted(false);
        topSection.add(generateKeyButton);

        topSection.add(new JLabel(", or type in manually:"));

        topSection.add(Box.createHorizontalStrut(20));

        topSection.add(new JLabel("(Optional) Create an IV, or"));

        generateIVButton = new JButton("generate one");
        generateIVButton.setPreferredSize(new Dimension(96, 25));
        generateIVButton.setBackground(Color.white);
        generateIVButton.setForeground(Color.black);
        generateIVButton.setFocusPainted(false);
        topSection.add(generateIVButton);

        panel.add(topSection, BorderLayout.NORTH);

        JPanel centerSection = new JPanel(new GridLayout(1, 2, 10, 0));

        JPanel secretKeyPanel = new JPanel(new BorderLayout(5, 5));

        secretKeyArea = createPlaceholderTextArea(SECRET_KEY_PLACEHOLDER);
        JScrollPane secretKeyScroll = new JScrollPane(secretKeyArea);
        secretKeyScroll.setPreferredSize(new Dimension(300, 100));
        secretKeyPanel.add(secretKeyScroll, BorderLayout.CENTER);

        saveSecretKeyButton = new JButton("Save secret key");
        saveSecretKeyButton.setMargin(new Insets(2, 10, 2, 10));
        JPanel secretKeyButtonPanel = new JPanel();
        secretKeyButtonPanel.setLayout(new BoxLayout(secretKeyButtonPanel, BoxLayout.PAGE_AXIS));
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonWrapper.add(saveSecretKeyButton);
        secretKeyButtonPanel.add(Box.createVerticalStrut(5));
        secretKeyButtonPanel.add(buttonWrapper);
        secretKeyButtonPanel.add(Box.createVerticalStrut(5));
        secretKeyPanel.add(secretKeyButtonPanel, BorderLayout.SOUTH);

        ivPanel = new JPanel(new BorderLayout(5, 5));

        ivArea = createPlaceholderTextArea(IV_PLACEHOLDER);
        JScrollPane ivScroll = new JScrollPane(ivArea);
        ivScroll.setPreferredSize(new Dimension(300, 100));
        ivPanel.add(ivScroll, BorderLayout.CENTER);

        saveIVButton = new JButton("Save IV");
        saveIVButton.setMargin(new Insets(2, 10, 2, 10));
        JPanel ivButtonPanel = new JPanel();
        ivButtonPanel.setLayout(new BoxLayout(ivButtonPanel, BoxLayout.PAGE_AXIS));
        JPanel ivButtonWrapper = new JPanel();
        ivButtonWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
        ivButtonWrapper.add(saveIVButton);
        ivButtonPanel.add(Box.createVerticalStrut(5));
        ivButtonPanel.add(ivButtonWrapper);
        ivButtonPanel.add(Box.createVerticalStrut(5));
        ivPanel.add(ivButtonPanel, BorderLayout.SOUTH);

        ivMessageLabel = new JLabel("This mode doesn't need IV", SwingConstants.CENTER);
        ivMessageLabel.setFont(ivMessageLabel.getFont().deriveFont(Font.ITALIC));
        ivMessageLabel.setForeground(Color.GRAY);
        ivMessageLabel.setVisible(false);

        centerSection.add(secretKeyPanel);
        centerSection.add(ivPanel);

        panel.add(centerSection, BorderLayout.CENTER);

        generateKeyButton.addActionListener(e -> generateKey());
        saveSecretKeyButton.addActionListener(e -> saveSecretKey());
        generateIVButton.addActionListener(e -> generateIV());
        saveIVButton.addActionListener(e -> saveIV());

        return panel;
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

    public void updateIVPanel(String mode) {
        boolean requiresIV = mode != null && !"ECB".equals(mode) && !"CTR".equals(mode) && !"none".equals(mode) && !"".equals(mode);

        if (requiresIV) {
            ivArea.setEnabled(true);
            generateIVButton.setEnabled(true);
            saveIVButton.setEnabled(true);
            ivArea.setVisible(true);
            generateIVButton.setVisible(true);
            saveIVButton.setVisible(true);
            ivMessageLabel.setVisible(false);

            ivPanel.remove(ivMessageLabel);

            if (ivPanel.getComponentCount() < 2) {
                ivPanel.removeAll();

                JScrollPane ivScroll = new JScrollPane(ivArea);
                ivScroll.setPreferredSize(new Dimension(300, 100));
                ivPanel.add(ivScroll, BorderLayout.CENTER);

                saveIVButton.setMargin(new Insets(2, 10, 2, 10));
                JPanel ivButtonPanel = new JPanel();
                ivButtonPanel.setLayout(new BoxLayout(ivButtonPanel, BoxLayout.PAGE_AXIS));
                JPanel ivButtonWrapper = new JPanel();
                ivButtonWrapper.setLayout(new FlowLayout(FlowLayout.CENTER));
                ivButtonWrapper.add(saveIVButton);
                ivButtonPanel.add(Box.createVerticalStrut(5));
                ivButtonPanel.add(ivButtonWrapper);
                ivButtonPanel.add(Box.createVerticalStrut(5));
                ivPanel.add(ivButtonPanel, BorderLayout.SOUTH);
            }
        } else {
            ivArea.setEnabled(false);
            generateIVButton.setEnabled(false);
            saveIVButton.setEnabled(false);

            ivPanel.removeAll();
            ivPanel.add(ivMessageLabel, BorderLayout.CENTER);
            ivMessageLabel.setVisible(true);
        }

        ivPanel.revalidate();
        ivPanel.repaint();

        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            Component tabComponent = tabbedPane.getComponentAt(i);
            if (tabComponent instanceof SymmetricalEncryptPanel) {
                ((SymmetricalEncryptPanel) tabComponent).updateIVPanel(mode);
            }
        }

        Component[] components = tabbedPane.getComponents();
        for (Component component : components) {
            if (component instanceof SymmetricalEncryptPanel) {
                ((SymmetricalEncryptPanel) component).updateIVPanel(mode);
            } else if (component instanceof SymmetricalDecryptPanel) {
                ((SymmetricalDecryptPanel) component).updateIVPanel(mode);
            }
        }
    }

    private void generateKey() {
        try {
            int keySize = 256;
            String generatedKey = AESUtil.genKey(keySize);

            secretKeyArea.setForeground(Color.BLACK);
            secretKeyArea.setText(generatedKey);

            System.out.println("Secret key generated successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating key: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveSecretKey() {
        if (secretKeyArea.getText().equals(SECRET_KEY_PLACEHOLDER) || secretKeyArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No secret key to save. Please generate or enter a key first.",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Secret Key");
      
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".pem")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".pem");
            }

            try {
                AESUtil.saveKey(secretKeyArea.getText(), selectedFile.getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                        "Secret key saved successfully to: " + selectedFile.getAbsolutePath(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving key: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void generateIV() {
        try {
            String mode = "CBC";
            String generatedIV = AESUtil.genIV(mode);

            ivArea.setForeground(Color.BLACK);
            ivArea.setText(generatedIV);

            System.out.println("IV generated successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating IV: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void saveIV() {
        if (ivArea.getText().equals(IV_PLACEHOLDER) || ivArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No IV to save. Please generate or enter an IV first.", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save IV");
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".txt");
            }

            try {
                AESUtil.saveIV(ivArea.getText(), selectedFile.getAbsolutePath());
                JOptionPane.showMessageDialog(this, "IV saved successfully to: " + selectedFile.getAbsolutePath(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving IV: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
}