package Math_Intelligent_Tutoring_System;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Scanner;

public class VolumeTutoringSystem extends JFrame {

    private JComboBox<String> topicComboBox;
    private JButton fetchButton, loginButton, registerButton, logoutButton;
    private JTextPane resultPane;
    private JTextField usernameField, registerUsernameField;
    private JPasswordField passwordField, registerPasswordField;
    private CardLayout cardLayout;
    private JPanel containerPanel;

    private static final String OWL_FILE = "VolumeOntology.owl";
    private static final String BASE_URI = "http://www.owl-ontologies.com/VolumeOntology.owl#";
    private static final String USER_FILE = "users.txt";

    public VolumeTutoringSystem() {
        setTitle("Math Intelligent Tutoring System");
        setSize(700, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);

        // Adding all panels
        containerPanel.add(createLoginPanel(), "LoginPanel");
        containerPanel.add(createRegisterPanel(), "RegisterPanel");
        containerPanel.add(createMainPanel(), "MainPanel");

        add(containerPanel);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Math Intelligent Tutoring System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(60, 63, 65));

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(72, 209, 204), Color.WHITE);

        registerButton = new JButton("Register");
        styleButton(registerButton, new Color(135, 206, 250), Color.WHITE);

        panel.add(titleLabel, createConstraints(0, 0, 2));
        panel.add(createLabel("Username:"), createConstraints(0, 1));
        panel.add(usernameField, createConstraints(1, 1));
        panel.add(createLabel("Password:"), createConstraints(0, 2));
        panel.add(passwordField, createConstraints(1, 2));
        panel.add(loginButton, createConstraints(0, 3, 2));
        panel.add(registerButton, createConstraints(0, 4, 2));

        loginButton.addActionListener(e -> validateLogin());
        registerButton.addActionListener(e -> cardLayout.show(containerPanel, "RegisterPanel"));

        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Register New Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(new Color(60, 63, 65));

        registerUsernameField = new JTextField(15);
        registerPasswordField = new JPasswordField(15);
        JButton createButton = new JButton("Create Account");
        styleButton(createButton, new Color(72, 209, 204), Color.WHITE);

        panel.add(titleLabel, createConstraints(0, 0, 2));
        panel.add(createLabel("Username:"), createConstraints(0, 1));
        panel.add(registerUsernameField, createConstraints(1, 1));
        panel.add(createLabel("Password:"), createConstraints(0, 2));
        panel.add(registerPasswordField, createConstraints(1, 2));
        panel.add(createButton, createConstraints(0, 3, 2));

        createButton.addActionListener(e -> registerUser());
        return panel;
    }

    private JPanel createMainPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(255, 250, 240));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(230, 230, 250));

        JLabel topicLabel = new JLabel("Select Shape Topic:");
        topicLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        topicComboBox = new JComboBox<>(new String[]{"SphereVolume", "CubeVolume", "CylinderVolume"});
        topicComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        fetchButton = new JButton("Fetch Example");
        styleButton(fetchButton, new Color(135, 206, 250), Color.WHITE);

        logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(255, 69, 0), Color.WHITE);

        topPanel.add(topicLabel);
        topPanel.add(topicComboBox);
        topPanel.add(fetchButton);
        topPanel.add(logoutButton);

        resultPane = new JTextPane();
        resultPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resultPane.setBorder(BorderFactory.createLineBorder(new Color(169, 169, 169), 1));

        JScrollPane scrollPane = new JScrollPane(resultPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        fetchButton.addActionListener(e -> fetchExamples());
        logoutButton.addActionListener(e -> cardLayout.show(containerPanel, "LoginPanel"));

        return panel;
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return label;
    }

    private GridBagConstraints createConstraints(int x, int y) {
        return createConstraints(x, y, 1);
    }

    private GridBagConstraints createConstraints(int x, int y, int width) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void validateLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        try (Scanner scanner = new Scanner(new File(USER_FILE))) {
            while (scanner.hasNextLine()) {
                String[] user = scanner.nextLine().split(",");
                if (user[0].equals(username) && user[1].equals(password)) {
                    cardLayout.show(containerPanel, "MainPanel");
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerUser() {
        try (FileWriter writer = new FileWriter(USER_FILE, true)) {
            writer.write(registerUsernameField.getText() + "," + new 
        String(registerPasswordField.getPassword()) + "\n");
            JOptionPane.showMessageDialog(this, "Account Created");
            cardLayout.show(containerPanel, "LoginPanel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchExamples() {
        String topic = topicComboBox.getSelectedItem().toString();
        String queryStr = "PREFIX vol: <" + BASE_URI + "> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "SELECT ?conceptLabel ?exampleLabel ?answer WHERE { "
                + "vol:" + topic + " vol:hasConcept ?concept . "
                + "?concept rdfs:label ?conceptLabel ; vol:hasExample ?example . "
                + "?example rdfs:label ?exampleLabel ; vol:hasAnswer ?answer . }";
        try (InputStream in = FileManager.get().open(OWL_FILE)) {
            OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
            model.read(in, BASE_URI);
            Query query = QueryFactory.create(queryStr);
            QueryExecution qe = QueryExecutionFactory.create(query, model);
            ResultSet results = qe.execSelect();

            StringBuilder sb = new StringBuilder();
            while (results.hasNext()) {
                QuerySolution sol = results.nextSolution();
                sb.append("Concept: ").append(sol.get("conceptLabel")).append("\n");
                sb.append("Example: ").append(sol.get("exampleLabel")).append("\n");
                sb.append("Answer: ").append(sol.get("answer")).append("\n\n");
            }
            resultPane.setText(sb.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching data.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new VolumeTutoringSystem();
    }
}
