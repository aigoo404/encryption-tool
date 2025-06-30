package controller;

import javax.swing.*;

import view.AsymmetricalGenkeyPanel;
import view.SymmetricalGenkeyPanel;
import view.SymmetricalEncryptPanel;
import view.DigitalSignatureSignPanel;
import view.DigitalSignatureVerifyPanel;

import java.awt.*;

public class MainFrame extends JFrame {

    private AsymmetricalGenkeyPanel asymmetricalGenkeyPanel;
    private SymmetricalGenkeyPanel symmetricalGenkeyPanel;
    private DigitalSignatureSignPanel digitalSignatureSignPanel;
    private DigitalSignatureVerifyPanel digitalSignatureVerifyPanel;
    private JPanel centerPanel;
    private JComboBox<String> encryptionTypeCombo;
    private JComboBox<String> algorithmCombo;
    private JComboBox<String> modeCombo;
    private JComboBox<String> keySizeCombo;
    private JComboBox<String> paddingCombo;

    public MainFrame() {
        setTitle("Encryption tool v1.1-alpha");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(690, 420);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 0, 5));

        JPanel encryptionTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        encryptionTypePanel.add(new JLabel("Choose encryption type:"));
        encryptionTypeCombo = new JComboBox<>(
                new String[] { "None", "Digital Signature", "Classical", "Asymmetrical", "Symmetrical" });
        encryptionTypeCombo.setPreferredSize(new Dimension(120, 24));
        encryptionTypePanel.add(encryptionTypeCombo);
        topPanel.add(encryptionTypePanel);

        JPanel algorithmPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        algorithmPanel.add(new JLabel("Choose algorithm:"));
        algorithmCombo = new JComboBox<>(new String[] { "" });
        algorithmCombo.setPreferredSize(new Dimension(80, 24));
        algorithmCombo.setEnabled(false);
        algorithmPanel.add(algorithmCombo);

        algorithmPanel.add(new JLabel("Mode:"));
        modeCombo = new JComboBox<>(new String[] { "" });
        modeCombo.setPreferredSize(new Dimension(80, 24));
        modeCombo.setEnabled(false);
        algorithmPanel.add(modeCombo);

        algorithmPanel.add(new JLabel("Key size (bits):"));
        keySizeCombo = new JComboBox<>(new String[] { "1024", "2048", "4096" });
        keySizeCombo.setPreferredSize(new Dimension(80, 24));
        keySizeCombo.setEnabled(false);
        algorithmPanel.add(keySizeCombo);

        algorithmPanel.add(new JLabel("Padding:"));
        paddingCombo = new JComboBox<>(new String[] { "" });
        paddingCombo.setPreferredSize(new Dimension(100, 24));
        paddingCombo.setEnabled(false);
        algorithmPanel.add(paddingCombo);

        topPanel.add(algorithmPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        centerPanel = new JPanel(new BorderLayout());

        asymmetricalGenkeyPanel = new AsymmetricalGenkeyPanel(false);
        symmetricalGenkeyPanel = new SymmetricalGenkeyPanel();
        digitalSignatureSignPanel = new DigitalSignatureSignPanel();
        digitalSignatureVerifyPanel = new DigitalSignatureVerifyPanel();

        JPanel emptyPanel = new JPanel();
        emptyPanel.add(new JLabel("Please select an encryption type to begin."));
        centerPanel.add(emptyPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        encryptionTypeCombo.addActionListener(e -> updateEncryptionType());
        algorithmCombo.addActionListener(e -> updateAlgorithmOptions());
        modeCombo.addActionListener(e -> updateModeOptions());
    }

    private void updateEncryptionType() {
        String selectedType = (String) encryptionTypeCombo.getSelectedItem();

        centerPanel.removeAll();

        algorithmCombo.removeAllItems();
        modeCombo.removeAllItems();
        keySizeCombo.removeAllItems();
        paddingCombo.removeAllItems();

        if ("Symmetrical".equals(selectedType)) {
            algorithmCombo.setEnabled(true);
            modeCombo.setEnabled(true);
            keySizeCombo.setEnabled(true);
            paddingCombo.setEnabled(true);

            algorithmCombo.addItem("AES");
            algorithmCombo.addItem("DES");
            algorithmCombo.addItem("3DES");

            modeCombo.addItem("ECB");
            modeCombo.addItem("CBC");
            modeCombo.addItem("CFB");
            modeCombo.addItem("OFB");
            modeCombo.addItem("CTR");
            modeCombo.setSelectedItem("CBC");

            paddingCombo.addItem("NoPadding");
            paddingCombo.addItem("PKCS5");
            paddingCombo.addItem("ISO10126");

            centerPanel.add(symmetricalGenkeyPanel, BorderLayout.CENTER);

        } else if ("Digital Signature".equals(selectedType)) {
            algorithmCombo.setEnabled(true);
            modeCombo.setEnabled(true);
            keySizeCombo.setEnabled(true);
            paddingCombo.setEnabled(true);

            algorithmCombo.addItem("RSA");
            algorithmCombo.addItem("DSA");
            algorithmCombo.addItem("ECDSA");

            modeCombo.addItem("Sign");
            modeCombo.addItem("Verify");
            modeCombo.setSelectedItem("Sign");

            keySizeCombo.addItem("1024");
            keySizeCombo.addItem("2048");
            keySizeCombo.addItem("4096");

            paddingCombo.addItem("PKCS1Padding");
            paddingCombo.addItem("PSS");

            centerPanel.add(digitalSignatureSignPanel, BorderLayout.CENTER);

        } else if ("Classical".equals(selectedType)) {

            algorithmCombo.setEnabled(true);
            modeCombo.setEnabled(true);
            keySizeCombo.setEnabled(false);
            paddingCombo.setEnabled(false);

            algorithmCombo.addItem("Caesar");
            algorithmCombo.addItem("Vigenere");

            modeCombo.addItem("Encrypt");
            modeCombo.addItem("Decrypt");

            JPanel classicalPanel = new JPanel();
            classicalPanel.add(new JLabel("Classical Encryption functionality - Coming Soon"));
            centerPanel.add(classicalPanel, BorderLayout.CENTER);

        } else if ("Asymmetrical".equals(selectedType)) {
            algorithmCombo.setEnabled(true);
            modeCombo.setEnabled(true);
            keySizeCombo.setEnabled(true);
            paddingCombo.setEnabled(true);

            algorithmCombo.addItem("RSA");

            modeCombo.addItem("none");

            keySizeCombo.addItem("1024");
            keySizeCombo.addItem("2048");
            keySizeCombo.addItem("4096");
            keySizeCombo.setSelectedItem("1024");

            paddingCombo.addItem("No padding");
            paddingCombo.addItem("PKCS1Padding");

            centerPanel.add(asymmetricalGenkeyPanel, BorderLayout.CENTER);
        }

        centerPanel.revalidate();
        centerPanel.repaint();
    }

    private void updateAlgorithmOptions() {
        String selectedAlgorithm = (String) algorithmCombo.getSelectedItem();

        keySizeCombo.removeAllItems();

        if ("AES".equals(selectedAlgorithm)) {
            keySizeCombo.addItem("128");
            keySizeCombo.addItem("192");
            keySizeCombo.addItem("256");
        } else if ("DES".equals(selectedAlgorithm)) {
            keySizeCombo.addItem("56");
        } else if ("3DES".equals(selectedAlgorithm)) {
            keySizeCombo.addItem("112");
            keySizeCombo.addItem("168");
        }
    }

    private void updateModeOptions() {
        String selectedMode = (String) modeCombo.getSelectedItem();
        String selectedType = (String) encryptionTypeCombo.getSelectedItem();

        if ("Symmetrical".equals(selectedType) && symmetricalGenkeyPanel != null) {
            symmetricalGenkeyPanel.updateIVPanel(selectedMode);
            if (symmetricalGenkeyPanel.getComponentCount() > 0) {
                Component[] components = symmetricalGenkeyPanel.getComponents();
                for (Component comp : components) {
                    if (comp instanceof JTabbedPane) {
                        JTabbedPane tabbedPane = (JTabbedPane) comp;
                        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                            Component tabComponent = tabbedPane.getComponentAt(i);
                            if (tabComponent instanceof SymmetricalEncryptPanel) {
                                ((SymmetricalEncryptPanel) tabComponent).updateIVPanel(selectedMode);
                            }
                        }
                    }
                }
            }
        } else if ("Digital Signature".equals(selectedType)) {
            centerPanel.removeAll();
            
            if ("Sign".equals(selectedMode)) {
                centerPanel.add(digitalSignatureSignPanel, BorderLayout.CENTER);
            } else if ("Verify".equals(selectedMode)) {
                centerPanel.add(digitalSignatureVerifyPanel, BorderLayout.CENTER);
            }
            
            centerPanel.revalidate();
            centerPanel.repaint();
        }
    }

    public String getSelectedAlgorithm() {
        return (String) algorithmCombo.getSelectedItem();
    }
    
    public String getSelectedMode() {
        return (String) modeCombo.getSelectedItem();
    }
    
    public String getSelectedPadding() {
        String padding = (String) paddingCombo.getSelectedItem();

        if ("PKCS5".equals(padding)) {
            return "PKCS5Padding";
        }
        return padding;
    }
    
    public String getSelectedKeySize() {
        return (String) keySizeCombo.getSelectedItem();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}