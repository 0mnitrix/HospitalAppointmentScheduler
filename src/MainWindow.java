import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainWindow extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private AppointmentManager manager;
    private JLabel statusLabel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private boolean darkTheme = false;
    private String currentUserRole = "User";

    public MainWindow(AppointmentManager manager) {
        if (!authenticate()) {
            System.exit(0);
        }

        this.manager = manager;
        setTitle("Hospital Appointment Scheduler");
        setSize(1000, 550);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField();
        searchField.setToolTipText("Search by name, doctor or date...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        statusLabel = new JLabel("Welcome " + currentUserRole + "!");
        topPanel.add(statusLabel, BorderLayout.NORTH);
        topPanel.add(searchField, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Patient", "Doctor", "Date", "Time", "Status"}, 0);
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add Appointment");
        addButton.addActionListener(this::onAdd);

        JButton editButton = new JButton("Edit Selected");
        editButton.addActionListener(this::onEdit);

        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(this::onDelete);

        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> {
            FileHandler.exportToCSV(manager.getAppointments(), "appointments_export.csv");
            setStatus("Appointments exported.");
        });

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(e -> {
            FileHandler.saveAppointments(manager.getAppointments());
            setStatus("Appointments saved.");
        });

        JButton logButton = new JButton("View Log");
        logButton.addActionListener(e -> viewLog());

        JButton themeButton = new JButton("Toggle Dark Theme");
        themeButton.addActionListener(e -> toggleTheme());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(logButton);
        buttonPanel.add(themeButton);

        if (!currentUserRole.equals("Admin")) {
            addButton.setEnabled(false);
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
            saveButton.setEnabled(false);
        }

        add(buttonPanel, BorderLayout.SOUTH);

        refreshTable();
        setVisible(true);
    }

    private boolean authenticate() {
        Map<String, String> users = new HashMap<>();
        users.put("admin", "admin123");
        users.put("user", "user123");

        Map<String, String> roles = new HashMap<>();
        roles.put("admin", "Admin");
        roles.put("user", "User");

        JPanel panel = new JPanel(new GridLayout(2, 2));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (users.containsKey(username) && users.get(username).equals(password)) {
                currentUserRole = roles.get(username);
                Logger.log("[AUTH] Login successful for user: " + username + " as " + currentUserRole);
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
                return authenticate();
            }
        }
        return false;
    }

    private void onAdd(ActionEvent e) {
        String patient = JOptionPane.showInputDialog(this, "Enter patient name:");
        String doctor = JOptionPane.showInputDialog(this, "Enter doctor name:");
        String date = JOptionPane.showInputDialog(this, "Enter date (YYYY-MM-DD):");
        String time = JOptionPane.showInputDialog(this, "Enter time (HH:MM):");
        String[] statuses = {"Scheduled", "Cancelled"};
        String status = (String) JOptionPane.showInputDialog(this, "Select status:", "Status",
                JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);

        if (patient != null && doctor != null && date != null && time != null && status != null) {
            Appointment app = new Appointment(
                    manager.getAppointments().size() + 1,
                    patient, doctor, date, time, status
            );
            manager.getAppointments().add(app);
            Logger.log("[GUI] Added appointment for " + patient);
            FileHandler.saveAppointments(manager.getAppointments());
            refreshTable();
            setStatus("Appointment added and saved.");
        }
    }

    private void onEdit(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            int id = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
            Appointment app = manager.getAppointments().stream().filter(a -> a.getId() == id).findFirst().orElse(null);
            if (app != null) {
                String patient = JOptionPane.showInputDialog(this, "Edit patient name:", app.getPatientName());
                String doctor = JOptionPane.showInputDialog(this, "Edit doctor name:", app.getDoctorName());
                String date = JOptionPane.showInputDialog(this, "Edit date (YYYY-MM-DD):", app.getDate());
                String time = JOptionPane.showInputDialog(this, "Edit time (HH:MM):", app.getTime());
                String[] statuses = {"Scheduled", "Cancelled"};
                String status = (String) JOptionPane.showInputDialog(this, "Select status:", "Status",
                        JOptionPane.QUESTION_MESSAGE, null, statuses, app.getStatus());

                if (patient != null && doctor != null && date != null && time != null && status != null) {
                    Appointment updated = new Appointment(id, patient, doctor, date, time, status);
                    manager.getAppointments().set(modelRow, updated);
                    Logger.log("[GUI] Edited appointment ID " + id);
                    FileHandler.saveAppointments(manager.getAppointments());
                    refreshTable();
                    setStatus("Appointment updated and saved.");
                }
            }
        }
    }

    private void onDelete(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            int id = Integer.parseInt(model.getValueAt(selectedRow, 0).toString());
            manager.getAppointments().removeIf(app -> app.getId() == id);
            Logger.log("[GUI] Deleted appointment ID " + id);
            FileHandler.saveAppointments(manager.getAppointments());
            refreshTable();
            setStatus("Appointment deleted and saved.");
        }
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Appointment app : manager.getAppointments()) {
            model.addRow(new Object[]{
                    app.getId(),
                    app.getPatientName(),
                    app.getDoctorName(),
                    app.getDate(),
                    app.getTime(),
                    app.getStatus()
            });
        }
    }

    private void filterTable() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void setStatus(String message) {
        statusLabel.setText("[" + LocalDateTime.now().withNano(0) + "] " + message);
    }

    private void viewLog() {
        try {
            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get("log.txt"));
            JTextArea textArea = new JTextArea(String.join("\n", lines));
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 300));
            JOptionPane.showMessageDialog(this, scrollPane, "Log Viewer", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Log file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleTheme() {
        try {
            if (!darkTheme) {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                darkTheme = true;
                setStatus("Dark theme enabled.");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                darkTheme = false;
                setStatus("Light theme restored.");
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to change theme.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        AppointmentManager manager = new AppointmentManager();
        manager.setAppointments(FileHandler.loadAppointments());
        new MainWindow(manager);
    }
}