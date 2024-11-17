import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CompressionMenu extends JFrame implements ActionListener {
    private JButton buttonCompress, buttonDecompress, buttonClose;
    private JLabel labelMessage;
    private CompressionSystem compressionSystem;

    public CompressionMenu() {
        compressionSystem = new CompressionSystem();

        // Setup buttons
        buttonCompress = new JButton("Compress File");
        buttonCompress.setBounds(75, 50, 150, 30);
        buttonCompress.addActionListener(this);

        buttonDecompress = new JButton("Decompress File");
        buttonDecompress.setBounds(75, 100, 150, 30);
        buttonDecompress.addActionListener(this);

        buttonClose = new JButton("Close");
        buttonClose.setBounds(75, 150, 150, 30);
        buttonClose.addActionListener(this);

        labelMessage = new JLabel("");
        labelMessage.setBounds(25, 200, 250, 30);

        // Frame setup
        this.setTitle("Compression Menu");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(300, 300);
        this.setLayout(null);
        this.getContentPane().setBackground(new Color(0xffffff));

        // Add components to frame
        this.add(buttonCompress);
        this.add(buttonDecompress);
        this.add(buttonClose);
        this.add(labelMessage);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonCompress) {
            String version = JOptionPane.showInputDialog(this, "Enter version number:");
            if (version != null && !version.trim().isEmpty()) {
                try {
                    String results = CompressionSystem.compressFile(version);
                    // Create a JTextArea for better formatting of multiline text
                    JTextArea textArea = new JTextArea(results);
                    textArea.setEditable(false);
                    textArea.setWrapStyleWord(true);
                    textArea.setLineWrap(true);
                    
                    // Put the JTextArea in a JScrollPane to handle long text
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(500, 400));
                    
                    JOptionPane.showMessageDialog(this, scrollPane, 
                        "Compression Results", JOptionPane.INFORMATION_MESSAGE);
                    labelMessage.setText("Compression completed successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                        "Compression Error", JOptionPane.ERROR_MESSAGE);
                    labelMessage.setText("Compression failed.");
                }
            }
        }
        else if (e.getSource() == buttonDecompress) {
            String version = JOptionPane.showInputDialog(this, "Enter version to decompress:");
            if (version != null && !version.trim().isEmpty()) {
                try {
                    // Create StringBuilder to collect all results
                    StringBuilder allResults = new StringBuilder();
                    
                    // Decompress Huffman file
                    String huffmanFile = "data/planets_huffmanV" + version + ".huff";
                    String huffmanResults = CompressionSystem.decompressFile(huffmanFile);
                    allResults.append("Huffman:\n");
                    allResults.append(huffmanResults);
                    allResults.append("\n");
                    
                    // Decompress LZW file
                    String lzwFile = "data/planets_lzwV" + version + ".lzw";
                    String lzwResults = CompressionSystem.decompressFile(lzwFile);
                    allResults.append("\nLZW:\n");
                    allResults.append(lzwResults);
                    
                    // Display results in a scrollable text area
                    JTextArea textArea = new JTextArea(allResults.toString());
                    textArea.setEditable(false);
                    textArea.setWrapStyleWord(true);
                    textArea.setLineWrap(true);
                    
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(500, 400));
                    
                    JOptionPane.showMessageDialog(this, scrollPane, 
                        "Decompression Results", JOptionPane.INFORMATION_MESSAGE);
                    labelMessage.setText("Decompression completed successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), 
                        "Decompression Error", JOptionPane.ERROR_MESSAGE);
                    labelMessage.setText("Decompression failed.");
                }
            }
        }
        else if (e.getSource() == buttonClose) {
            this.dispose();
        }
    }
}