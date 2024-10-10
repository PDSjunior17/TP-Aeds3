// File: MyFrame.java
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class MyFrame extends JFrame implements ActionListener {
    Scanner entrada = new Scanner(System.in);
    JButton buttonLoad, buttonCreate, buttonRead, buttonUpdate, buttonDelete, buttonExit;
    JLabel labelMessage; // Label for displaying confirmation messages
    CRUD crud; // CRUD object to perform operations

    MyFrame() {
        crud = new CRUD(); // Initialize the CRUD instance

        ImageIcon icon = new ImageIcon("data/logo.png");

        // Setup buttons
        buttonLoad = new JButton("Load");
        buttonLoad.setBounds(175, 100, 150, 30);
        buttonLoad.addActionListener(this);

        buttonCreate = new JButton("Create");
        buttonCreate.setBounds(175, 160, 150, 30);
        buttonCreate.addActionListener(this);

        buttonRead = new JButton("Read");
        buttonRead.setBounds(175, 220, 150, 30);
        buttonRead.addActionListener(this);

        buttonUpdate = new JButton("Update");
        buttonUpdate.setBounds(175, 280, 150, 30);
        buttonUpdate.addActionListener(this);

        buttonDelete = new JButton("Delete");
        buttonDelete.setBounds(175, 340, 150, 30);
        buttonDelete.addActionListener(this);

        buttonExit = new JButton("Exit");
        buttonExit.setBounds(175, 400, 150, 30);
        buttonExit.addActionListener(this);

        labelMessage = new JLabel(""); // Message label
        labelMessage.setBounds(50, 450, 400, 30);

        // Frame setup
        this.setTitle("GUI CRUD Interface");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 550);
        this.setVisible(true);
        this.setLayout(null);
        this.getContentPane().setBackground(new Color(0xffffff));
        this.setIconImage(icon.getImage());

        // Add buttons and labels to the frame
        this.add(buttonLoad);
        this.add(buttonCreate);
        this.add(buttonRead);
        this.add(buttonUpdate);
        this.add(buttonDelete);
        this.add(buttonExit);
        this.add(labelMessage);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonLoad) {
            try {
                crud.load();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } // Load data
            labelMessage.setText("Data loaded successfully!");
        }
        if (e.getSource() == buttonCreate) {
            showCreateWindow();
        }
        if (e.getSource() == buttonRead) {
            String idStr = JOptionPane.showInputDialog(this, "Enter the ID of the planet to read:");
            try {
                int id = Integer.parseInt(idStr);
                Planet planet = crud.read(id);
                if (planet != null) {
                    JOptionPane.showMessageDialog(this, "Planet Details:\n" + planet.toString());
                } else {
                    labelMessage.setText("Planet not found.");
                }
            } catch (Exception ex) {
                labelMessage.setText("Invalid ID format.");
            }
        }
        if (e.getSource() == buttonUpdate) {
            String idStr = JOptionPane.showInputDialog(this, "Enter the ID of the planet to update:");
            try {
                int id = Integer.parseInt(idStr);
                showUpdateWindow(id);
            } catch (Exception ex) {
                labelMessage.setText("Invalid ID format.");
            }
        }
        if (e.getSource() == buttonDelete) {
            String idStr = JOptionPane.showInputDialog(this, "Enter the ID of the planet to delete:");
            try {
                int id = Integer.parseInt(idStr);
                Planet deleted = crud.delete(id);
                if (deleted != null) {
                    labelMessage.setText("Deleted planet: " + deleted);
                } else {
                    labelMessage.setText("Planet not found.");
                }
            } catch (Exception ex) {
                labelMessage.setText("Invalid ID format.");
            }
        }
        if (e.getSource() == buttonExit) {
            System.exit(0);
        }
    }

    // Helper method to show a new window for creating a planet
    private void showCreateWindow() {
        JFrame createFrame = new JFrame("Create New Planet");
        createFrame.setSize(400, 300);
        createFrame.setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 50, 100, 30);
        JTextField nameField = new JTextField();
        nameField.setBounds(150, 50, 150, 30);

        JButton createButton = new JButton("Create");
        createButton.setBounds(150, 150, 100, 30);
        createButton.addActionListener(e -> {
            String name = nameField.getText();
            if (!name.isEmpty()) {
                try {
                    crud.create(entrada);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                labelMessage.setText("Planet created successfully!");
                createFrame.dispose();
            } else {
                labelMessage.setText("Planet creation failed. Name cannot be empty.");
            }
        });

        createFrame.add(nameLabel);
        createFrame.add(nameField);
        createFrame.add(createButton);
        createFrame.setVisible(true);
    }

    // Helper method to show a new window for updating a planet
    private void showUpdateWindow(int id) {
        JFrame updateFrame = new JFrame("Update Planet");
        updateFrame.setSize(400, 300);
        updateFrame.setLayout(null);

        JLabel nameLabel = new JLabel("New Name:");
        nameLabel.setBounds(50, 50, 100, 30);
        JTextField nameField = new JTextField();
        nameField.setBounds(150, 50, 150, 30);

        JButton updateButton = new JButton("Update");
        updateButton.setBounds(150, 150, 100, 30);
        updateButton.addActionListener(e -> {
            String newName = nameField.getText();
            if (!newName.isEmpty()) {
                try {
                    crud.update(entrada);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                labelMessage.setText("Planet updated successfully!");
                updateFrame.dispose();
            } else {
                labelMessage.setText("Update failed. Name cannot be empty.");
            }
        });

        updateFrame.add(nameLabel);
        updateFrame.add(nameField);
        updateFrame.add(updateButton);
        updateFrame.setVisible(true);
    }
}
