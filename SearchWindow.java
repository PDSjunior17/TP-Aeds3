import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class SearchWindow extends JFrame implements ActionListener {
    private JTextField searchBar;
    private JComboBox<String> algorithmChoice;
    private JButton searchButton;
    private PatternMatcher patternMatcher;
    private CRUD crud;
    private static final int RESULTS_PER_PAGE = 20;

    public SearchWindow(CRUD crud) {
        this.crud = crud;
        this.patternMatcher = new PatternMatcher();
        setupWindow();
    }

    private void setupWindow() {
        this.setTitle("Search Planets");
        this.setSize(400, 200);
        this.setLayout(null);
        this.setResizable(false);
        this.getContentPane().setBackground(new Color(0xffffff));

        searchBar = new JTextField();
        searchBar.setBounds(50, 30, 300, 30);

        String[] algorithms = {"KMP Search", "Boyer-Moore Search"};
        algorithmChoice = new JComboBox<>(algorithms);
        algorithmChoice.setBounds(50, 70, 300, 30);

        searchButton = new JButton("Search");
        searchButton.setBounds(150, 110, 100, 30);
        searchButton.addActionListener(this);

        this.add(searchBar);
        this.add(algorithmChoice);
        this.add(searchButton);

        this.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == searchButton) {
            String pattern = searchBar.getText();
            if (pattern.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search pattern");
                return;
            }

            try {
                List<Integer> foundIntegers;
                if (algorithmChoice.getSelectedItem().equals("KMP Search")) {
                    foundIntegers = patternMatcher.searchKMP(pattern);
                } else {
                    foundIntegers = patternMatcher.searchBoyerMoore(pattern);
                }

                if (foundIntegers.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No matches found");
                } else {
                    showPaginatedResultsWindow(foundIntegers);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error during search: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private void showPaginatedResultsWindow(List<Integer> foundIds) {
        JFrame resultsWindow = new JFrame("Search Results");
        resultsWindow.setSize(400, 500);
        resultsWindow.setLayout(new BorderLayout());

        JPanel numbersPanel = new JPanel();
        numbersPanel.setLayout(new BoxLayout(numbersPanel, BoxLayout.Y_AXIS));

        JPanel navigationPanel = new JPanel(new FlowLayout());
        JButton prevButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
        JLabel pageLabel = new JLabel("Page 1 of 1");
        navigationPanel.add(prevButton);
        navigationPanel.add(pageLabel);
        navigationPanel.add(nextButton);

        JScrollPane scrollPane = new JScrollPane(numbersPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        resultsWindow.add(scrollPane, BorderLayout.CENTER);
        resultsWindow.add(navigationPanel, BorderLayout.SOUTH);

        final int totalPages = (int) Math.ceil((double) foundIds.size() / RESULTS_PER_PAGE);
        final int[] currentPage = {1};

        Runnable updateResults = () -> {
            numbersPanel.removeAll();
            
            JLabel headerLabel = new JLabel(String.format("Found %d matches:", foundIds.size()));
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            numbersPanel.add(headerLabel);

            int startIndex = (currentPage[0] - 1) * RESULTS_PER_PAGE;
            int endIndex = Math.min(startIndex + RESULTS_PER_PAGE, foundIds.size());

            JPanel gridPanel = new JPanel(new GridLayout(0, 5, 10, 10));
            gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            for (int i = startIndex; i < endIndex; i++) {
                JLabel numberLabel = new JLabel(foundIds.get(i).toString());
                numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
                numberLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                numberLabel.setPreferredSize(new Dimension(50, 30));
                gridPanel.add(numberLabel);
            }

            numbersPanel.add(gridPanel);
            
            pageLabel.setText(String.format("Page %d of %d", currentPage[0], totalPages));
            prevButton.setEnabled(currentPage[0] > 1);
            nextButton.setEnabled(currentPage[0] < totalPages);
            
            numbersPanel.revalidate();
            numbersPanel.repaint();
        };

        prevButton.addActionListener(e -> {
            if (currentPage[0] > 1) {
                currentPage[0]--;
                updateResults.run();
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage[0] < totalPages) {
                currentPage[0]++;
                updateResults.run();
            }
        });

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> resultsWindow.dispose());
        navigationPanel.add(Box.createHorizontalStrut(20));
        navigationPanel.add(closeButton);

        updateResults.run();

        resultsWindow.setLocationRelativeTo(null);
        resultsWindow.setVisible(true);
    }
}