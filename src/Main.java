import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EncryptorFrame());
    }
}

class EncryptorFrame extends JFrame {
    private JTextField inputField, outputField, keyField;
    private JButton inputBrowse, outputBrowse, encryptButton, decryptButton;

    public EncryptorFrame() {
        setTitle("File Encryptor / Decryptor - XOR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 250);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Input File
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Input File:"), gbc);
        inputField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(inputField, gbc);
        inputBrowse = new JButton("Browse");
        gbc.gridx = 2;
        gbc.weightx = 0;
        add(inputBrowse, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Output File/Folder:"), gbc);
        outputField = new JTextField();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(outputField, gbc);
        outputBrowse = new JButton("Browse");
        gbc.gridx = 2;
        add(outputBrowse, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Encryption Key (0–255):"), gbc);
        keyField = new JTextField();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(keyField, gbc);

        JPanel buttonPanel = new JPanel();
        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        setVisible(true);

        inputBrowse.addActionListener(e -> chooseFile(inputField, true));
        outputBrowse.addActionListener(e -> chooseFile(outputField, false));
        encryptButton.addActionListener(e -> handleOperation(true));
        decryptButton.addActionListener(e -> handleOperation(false));
    }

    private void chooseFile(JTextField field, boolean openDialog) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int result = openDialog ? chooser.showOpenDialog(this) : chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            field.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void handleOperation(boolean encrypt) {
        String inputPath = inputField.getText().trim();
        String outputPath = outputField.getText().trim();
        String keyText = keyField.getText().trim();

        if (inputPath.isEmpty() || outputPath.isEmpty() || keyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int key;
        try {
            key = Integer.parseInt(keyText);
            if (key < 0 || key > 255)
                throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Key must be a number between 0 and 255.", "Invalid Key",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);

        if (!inputFile.exists()) {
            JOptionPane.showMessageDialog(this, "Input file does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (outputFile.isDirectory()) {
            String inputFileName = inputFile.getName();
            String baseName = inputFileName.contains(".")
                    ? inputFileName.substring(0, inputFileName.lastIndexOf('.'))
                    : inputFileName;
            String extension = inputFileName.contains(".")
                    ? inputFileName.substring(inputFileName.lastIndexOf('.'))
                    : "";

            String suffix = encrypt ? "_encrypted" : "_decrypted";
            outputFile = new File(outputFile, baseName + suffix + extension);
            JOptionPane.showMessageDialog(this,
                    "Auto-generated output file: \n" + outputFile.getAbsolutePath(),
                    "Info", JOptionPane.INFORMATION_MESSAGE);
        }

        FileEncryptor.processFile(inputFile, outputFile, key);

        if (outputFile.exists()) {
            String msg = encrypt ? "File encrypted successfully!" : "File decrypted successfully!";
            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "❌ Operation failed. Check permissions or paths.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
