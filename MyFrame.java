
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
    JButton buttonLoad, buttonCreate, buttonRead, buttonUpdate, buttonDelete, buttonExit, buttonCompression, buttonSearch;
    JLabel labelMessage; 
    CRUD crud; 

    MyFrame() {
        crud = new CRUD(); 

        ImageIcon icon = new ImageIcon("data/logo.png");

        
        buttonLoad = new JButton("Load");
        buttonLoad.setBounds(175, 100, 150, 30);
        buttonLoad.addActionListener(this);

        buttonCreate = new JButton("Create");
        buttonCreate.setBounds(175, 160, 150, 30);
        buttonCreate.addActionListener(this);

        buttonRead = new JButton("Read");
        buttonRead.setBounds(175, 220, 150, 30);
        buttonRead.addActionListener(this);

        buttonSearch = new JButton("Search");
        buttonSearch.setBounds(175, 280, 150, 30);
        buttonSearch.addActionListener(this);

        buttonUpdate = new JButton("Update");
        buttonUpdate.setBounds(175, 340, 150, 30);
        buttonUpdate.addActionListener(this);

        buttonDelete = new JButton("Delete");
        buttonDelete.setBounds(175, 400, 150, 30);
        buttonDelete.addActionListener(this);

        buttonCompression = new JButton("Compression");
        buttonCompression.setBounds(175, 460, 150, 30);
        buttonCompression.addActionListener(this);

        buttonExit = new JButton("Exit");
        buttonExit.setBounds(175, 520, 150, 30);
        buttonExit.addActionListener(this);

        labelMessage = new JLabel(""); 
        labelMessage.setBounds(50, 560, 400, 30);

        
        this.setTitle("GUI CRUD Interface");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(500, 660);
        this.setVisible(true);
        this.setLayout(null);
        this.getContentPane().setBackground(new Color(0xffffff));
        this.setIconImage(icon.getImage());

        
        this.add(buttonLoad);
        this.add(buttonCreate);
        this.add(buttonRead);
        this.add(buttonSearch);
        this.add(buttonUpdate);
        this.add(buttonDelete);
        this.add(buttonCompression);
        this.add(buttonExit);
        this.add(labelMessage);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buttonLoad) {
            try {
                crud.load();
            } catch (Exception e1) {
                
                e1.printStackTrace();
            } 
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
                ex.printStackTrace();
                labelMessage.setText("Invalid ID format.");
            }
        }
        if (e.getSource() == buttonSearch) {
            SearchWindow searchWindow = new SearchWindow(crud);
            searchWindow.setVisible(true);
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
        if (e.getSource() == buttonCompression) {
            CompressionMenu compressionMenu = new CompressionMenu();
            compressionMenu.setVisible(true);
        }
        if (e.getSource() == buttonExit) {
            System.exit(0);
        }
    }

    
    private void showCreateWindow() {
        Planet tmp = new Planet();
        try {
            tmp.setId(0); 
    
            
            String name = JOptionPane.showInputDialog(this, "Inserir nome:");
            if (name != null && !name.isEmpty()) {
                tmp.setName(name);
            }
    
            String dataRelease = JOptionPane.showInputDialog(this, "Inserir data (yyyy-mm-dd):");
            if (dataRelease != null && !dataRelease.isEmpty()) {
                tmp.setDataRelase(dataRelease);
            }
    
            String host = JOptionPane.showInputDialog(this, "Inserir estrela hospedeira:");
            if (host != null && !host.isEmpty()) {
                tmp.setHost(host);
            }
    
            String numStarsStr = JOptionPane.showInputDialog(this, "Inserir número de estrelas:");
            if (numStarsStr != null && !numStarsStr.isEmpty()) {
                tmp.setNumStars(Integer.parseInt(numStarsStr)); 
            }
    
            String numPlanetsStr = JOptionPane.showInputDialog(this, "Inserir número de planetas:");
            if (numPlanetsStr != null && !numPlanetsStr.isEmpty()) {
                tmp.setNumPlanets(Integer.parseInt(numPlanetsStr)); 
            }
    
            String discoveryMethod = JOptionPane.showInputDialog(this, "Inserir método de descoberta:");
            if (discoveryMethod != null && !discoveryMethod.isEmpty()) {
                tmp.setDiscoveryMethod(discoveryMethod);
            }
    
            String discoveryYearStr = JOptionPane.showInputDialog(this, "Inserir ano de descoberta:");
            if (discoveryYearStr != null && !discoveryYearStr.isEmpty()) {
                tmp.setDiscoveryYear(Integer.parseInt(discoveryYearStr)); 
            }
    
            String discoveryFacility = JOptionPane.showInputDialog(this, "Inserir local de descoberta:");
            if (discoveryFacility != null && !discoveryFacility.isEmpty()) {
                tmp.setDiscoveryFacility(discoveryFacility);
            }
    
            String controvStr = JOptionPane.showInputDialog(this, "Inserir flag de controvérsia (true/false):");
            if (controvStr != null && !controvStr.isEmpty()) {
                tmp.setControv(Boolean.parseBoolean(controvStr)); 
            }
    
            String massStr = JOptionPane.showInputDialog(this, "Inserir massa estelar:");
            if (massStr != null && !massStr.isEmpty()) {
                tmp.setMass(Long.parseLong(massStr)); 
            }
    
            String tempStr = JOptionPane.showInputDialog(this, "Inserir temperatura estelar:");
            if (tempStr != null && !tempStr.isEmpty()) {
                tmp.setstarTemperature(Double.parseDouble(tempStr)); 
            }
    
            
            String[] metalRatioLines = new String[2];
            metalRatioLines[0] = JOptionPane.showInputDialog(this, "Insira a primeira linha da proporção metálica:");
            metalRatioLines[1] = JOptionPane.showInputDialog(this, "Insira a segunda linha da proporção metálica:");
            tmp.setMetalRatio(metalRatioLines);
    
            
            crud.create(tmp);
            labelMessage.setText("Planet with ID "+ tmp.getId() +" created successfully!");
            
        } catch (NumberFormatException ex) {
            labelMessage.setText("Error: Invalid number format in one of the fields.");
        } catch (Exception ex) {
            labelMessage.setText("Error: " + ex.getMessage());
        }
    }
    

    
    private void showUpdateWindow(int id) throws Exception{
        Planet tmp = crud.read(id); 
        if (tmp == null) {
            labelMessage.setText("Planet with ID " + id + " not found.");
            return;
        }
    
        try {
            
    
            
            String name = JOptionPane.showInputDialog(this, "Inserir nome:", tmp.getName());
            if (name != null && !name.isEmpty()) {
                tmp.setName(name);
            }
    
            
            String dataRelease = JOptionPane.showInputDialog(this, "Inserir data (yyyy-mm-dd):");
            if (dataRelease != null && !dataRelease.isEmpty()) {
                tmp.setDataRelase(dataRelease);
            }
    
            
            String host = JOptionPane.showInputDialog(this, "Inserir estrela hospedeira:");
            if (host != null && !host.isEmpty()) {
                tmp.setHost(host);
            }
    
            
            String numStarsStr = JOptionPane.showInputDialog(this, "Inserir número de estrelas:", tmp.getNumStars());
            if (numStarsStr != null && !numStarsStr.isEmpty()) {
                tmp.setNumStars(Integer.parseInt(numStarsStr));
            }
    
            
            String numPlanetsStr = JOptionPane.showInputDialog(this, "Inserir número de planetas:", tmp.getNumPlanets());
            if (numPlanetsStr != null && !numPlanetsStr.isEmpty()) {
                tmp.setNumPlanets(Integer.parseInt(numPlanetsStr));
            }
    
            
            String discoveryMethod = JOptionPane.showInputDialog(this, "Inserir método de descoberta:");
            if (discoveryMethod != null && !discoveryMethod.isEmpty()) {
                tmp.setDiscoveryMethod(discoveryMethod);
            }
    
            
            String discoveryYearStr = JOptionPane.showInputDialog(this, "Inserir ano de descoberta:", tmp.getDiscoveryYear());
            if (discoveryYearStr != null && !discoveryYearStr.isEmpty()) {
                tmp.setDiscoveryYear(Integer.parseInt(discoveryYearStr));
            }
    
            
            String discoveryFacility = JOptionPane.showInputDialog(this, "Inserir local de descoberta:", tmp.getDiscoveryFacility());
            if (discoveryFacility != null && !discoveryFacility.isEmpty()) {
                tmp.setDiscoveryFacility(discoveryFacility);
            }
    
            
            String controvStr = JOptionPane.showInputDialog(this, "Inserir flag de controvérsia (true/false):", tmp.isControv());
            if (controvStr != null && !controvStr.isEmpty()) {
                tmp.setControv(Boolean.parseBoolean(controvStr));
            }
    
            
            String massStr = JOptionPane.showInputDialog(this, "Inserir massa estelar:", tmp.getMass());
            if (massStr != null && !massStr.isEmpty()) {
                tmp.setMass(Long.parseLong(massStr));
            }
    
            
            String tempStr = JOptionPane.showInputDialog(this, "Inserir temperatura estelar (ou -1 se desconhecida):", tmp.getstarTemperature());
            if (tempStr != null && !tempStr.isEmpty()) {
                tmp.setstarTemperature(Double.parseDouble(tempStr));
            }
    
            
            String[] currentRatios = tmp.getMetalRatio();
            if (currentRatios == null || currentRatios.length < 2) {
                currentRatios = new String[]{"", ""}; 
            }

            
            String[] metalRatioLines = new String[2];
            metalRatioLines[0] = JOptionPane.showInputDialog(this, 
                "Inserir a primeira linha da proporção metálica:", 
                currentRatios[0]);
            metalRatioLines[1] = JOptionPane.showInputDialog(this, 
                "Inserir a segunda linha da proporção metálica:", 
                currentRatios[1]);

            
            if (metalRatioLines[0] != null && metalRatioLines[1] != null) {
                tmp.setMetalRatio(metalRatioLines);
            }
    
            
            crud.update(tmp);
            labelMessage.setText("Planet updated successfully!");
    
        } catch (NumberFormatException ex) {
            labelMessage.setText("Error: Invalid number format in one of the fields.");
        } catch (Exception ex) {
            labelMessage.setText("Error: " + ex.getMessage());
        }
    }
}
