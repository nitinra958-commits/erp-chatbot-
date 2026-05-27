import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ERPChatbotGUI extends JFrame {
    private static final Color PRIMARY = new Color(31, 79, 121);
    private static final Color ACCENT = new Color(0, 128, 128);
    private static final Color BACKGROUND = new Color(244, 247, 250);
    private static final Color PANEL = Color.WHITE;

    // HashMap stores student records by student ID/username.
    private final HashMap<String, Student> students = new HashMap<>();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private JTextArea adminStudentArea;
    private JComboBox<String> teacherStudentCombo;
    private JTextArea chatArea;
    private String loggedInStudentId;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                    // Default look and feel is fine if the system theme is unavailable.
                }
                new ERPChatbotGUI().setVisible(true);
            }
        });
    }

    public ERPChatbotGUI() {
        seedStudents();

        setTitle("College ERP Chatbot System");
        setSize(850, 600);
        setMinimumSize(new Dimension(760, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createAdminPanel(), "admin");
        mainPanel.add(createTeacherPanel(), "teacher");
        mainPanel.add(createStudentPanel(), "student");

        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }

    private void seedStudents() {
        Student s1 = new Student("Amit Sharma");
        s1.attendance = 86;
        s1.marks = 78;
        s1.fees = "PAID";
        s1.subjects.add("Java");
        s1.subjects.add("DBMS");

        Student s2 = new Student("Priya Verma");
        s2.attendance = 92;
        s2.marks = 88;
        s2.fees = "UNPAID";
        s2.subjects.add("Data Structures");
        s2.subjects.add("Computer Networks");

        students.put("S101", s1);
        students.put("S102", s2);
    }

    private JPanel createLoginPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(BACKGROUND);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PANEL);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 232)),
                BorderFactory.createEmptyBorder(28, 34, 28, 34)
        ));

        JLabel title = new JLabel("College ERP Chatbot Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY);

        JTextField usernameField = new JTextField(18);
        JPasswordField passwordField = new JPasswordField(18);
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "Teacher", "Student"});
        JButton loginButton = styledButton("Login", PRIMARY);

        JLabel help = new JLabel("Admin: admin/admin123 | Teacher: teacher/teacher123 | Student: S101/student123");
        help.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        help.setForeground(new Color(90, 99, 110));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        form.add(title, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        form.add(new JLabel("Username"), gbc);
        gbc.gridx = 1;
        form.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        form.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        form.add(new JLabel("Role"), gbc);
        gbc.gridx = 1;
        form.add(roleBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        form.add(loginButton, gbc);

        gbc.gridy++;
        form.add(help, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String role = (String) roleBox.getSelectedItem();

                if (isValidLogin(username, password, role)) {
                    usernameField.setText("");
                    passwordField.setText("");

                    if ("Admin".equals(role)) {
                        refreshAdminStudentArea();
                        cardLayout.show(mainPanel, "admin");
                    } else if ("Teacher".equals(role)) {
                        refreshTeacherStudentCombo();
                        cardLayout.show(mainPanel, "teacher");
                    } else {
                        loggedInStudentId = username;
                        loadStudentChat();
                        cardLayout.show(mainPanel, "student");
                    }
                } else {
                    JOptionPane.showMessageDialog(ERPChatbotGUI.this,
                            "Invalid login details. Please check username, password, and role.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        wrapper.add(form);
        return wrapper;
    }

    private boolean isValidLogin(String username, String password, String role) {
        if ("Admin".equals(role)) {
            return "admin".equals(username) && "admin123".equals(password);
        }
        if ("Teacher".equals(role)) {
            return "teacher".equals(username) && "teacher123".equals(password);
        }
        return students.containsKey(username) && "student123".equals(password);
    }

    private JPanel createAdminPanel() {
        JPanel panel = createBasePanel("Admin Panel");

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBackground(PANEL);
        form.setBorder(BorderFactory.createTitledBorder("Add New Student"));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JButton addButton = styledButton("Add Student", ACCENT);

        form.add(new JLabel("Student ID / Login Username"));
        form.add(idField);
        form.add(new JLabel("Student Name"));
        form.add(nameField);
        form.add(new JLabel(""));
        form.add(addButton);

        adminStudentArea = new JTextArea();
        adminStudentArea.setEditable(false);
        adminStudentArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(adminStudentArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("All Students"));

        JPanel actions = new JPanel(new BorderLayout(10, 10));
        actions.setBackground(BACKGROUND);
        actions.add(form, BorderLayout.NORTH);
        actions.add(scrollPane, BorderLayout.CENTER);
        actions.add(logoutButton(), BorderLayout.SOUTH);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String name = nameField.getText().trim();

                if (id.isEmpty() || name.isEmpty()) {
                    showInfo("Please enter both student ID and name.");
                    return;
                }
                if (students.containsKey(id)) {
                    showInfo("Student ID already exists.");
                    return;
                }

                students.put(id, new Student(name));
                idField.setText("");
                nameField.setText("");
                refreshAdminStudentArea();
                refreshTeacherStudentCombo();
                showInfo("Student added successfully. Login password for students is student123.");
            }
        });

        panel.add(actions, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTeacherPanel() {
        JPanel panel = createBasePanel("Teacher Panel");

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(PANEL);
        form.setBorder(BorderFactory.createTitledBorder("Update Student Record"));

        teacherStudentCombo = new JComboBox<>();
        JTextField attendanceField = new JTextField(12);
        JTextField marksField = new JTextField(12);
        JComboBox<String> feesBox = new JComboBox<>(new String[]{"PAID", "UNPAID"});
        JTextField subjectField = new JTextField(12);
        JButton loadButton = styledButton("Load", PRIMARY);
        JButton updateButton = styledButton("Update", ACCENT);
        JTextArea selectedStudentArea = new JTextArea(9, 30);
        selectedStudentArea.setEditable(false);
        selectedStudentArea.setFont(new Font("Consolas", Font.PLAIN, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addFormRow(form, gbc, 0, "Select Student", teacherStudentCombo);
        addFormRow(form, gbc, 1, "Attendance (%)", attendanceField);
        addFormRow(form, gbc, 2, "Marks (%)", marksField);
        addFormRow(form, gbc, 3, "Fees Status", feesBox);
        addFormRow(form, gbc, 4, "Add Subject", subjectField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        buttonPanel.setBackground(PANEL);
        buttonPanel.add(loadButton);
        buttonPanel.add(updateButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        form.add(buttonPanel, gbc);

        gbc.gridy = 6;
        form.add(new JScrollPane(selectedStudentArea), gbc);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBackground(BACKGROUND);
        content.add(form, BorderLayout.CENTER);
        content.add(logoutButton(), BorderLayout.SOUTH);

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = getSelectedStudentId();
                if (id == null) {
                    showInfo("No student selected.");
                    return;
                }
                Student student = students.get(id);
                attendanceField.setText(String.valueOf(student.attendance));
                marksField.setText(String.valueOf(student.marks));
                feesBox.setSelectedItem(student.fees);
                selectedStudentArea.setText(formatStudentDetails(id, student));
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = getSelectedStudentId();
                if (id == null) {
                    showInfo("No student selected.");
                    return;
                }

                Student student = students.get(id);
                try {
                    double attendance = Double.parseDouble(attendanceField.getText().trim());
                    double marks = Double.parseDouble(marksField.getText().trim());

                    if (attendance < 0 || attendance > 100 || marks < 0 || marks > 100) {
                        showInfo("Attendance and marks must be between 0 and 100.");
                        return;
                    }

                    student.attendance = attendance;
                    student.marks = marks;
                    student.fees = (String) feesBox.getSelectedItem();

                    String subject = subjectField.getText().trim();
                    if (!subject.isEmpty() && !student.subjects.contains(subject)) {
                        student.subjects.add(subject);
                    }

                    subjectField.setText("");
                    selectedStudentArea.setText(formatStudentDetails(id, student));
                    showInfo("Student record updated successfully.");
                } catch (NumberFormatException ex) {
                    showInfo("Please enter valid numbers for attendance and marks.");
                }
            }
        });

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStudentPanel() {
        JPanel panel = createBasePanel("Student Chatbot");

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField inputField = new JTextField();
        JButton sendButton = styledButton("Send", ACCENT);

        JPanel inputPanel = new JPanel(new BorderLayout(8, 8));
        inputPanel.setBackground(BACKGROUND);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setBackground(BACKGROUND);
        bottom.add(inputPanel, BorderLayout.CENTER);
        bottom.add(logoutButton(), BorderLayout.SOUTH);

        ActionListener sendAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText().trim();
                if (message.isEmpty()) {
                    return;
                }

                chatArea.append("You: " + message + "\n");
                chatArea.append("Bot: " + getBotResponse(message) + "\n\n");
                inputField.setText("");
            }
        };

        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);

        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBasePanel(String titleText) {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(PRIMARY);
        panel.add(title, BorderLayout.NORTH);
        return panel;
    }

    private JButton styledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return button;
    }

    private JButton logoutButton() {
        JButton button = styledButton("Logout", new Color(105, 111, 122));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loggedInStudentId = null;
                cardLayout.show(mainPanel, "login");
            }
        });
        return button;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, java.awt.Component component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private void refreshAdminStudentArea() {
        if (adminStudentArea == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        for (String id : students.keySet()) {
            builder.append(formatStudentDetails(id, students.get(id))).append("\n");
        }
        adminStudentArea.setText(builder.toString());
    }

    private void refreshTeacherStudentCombo() {
        if (teacherStudentCombo == null) {
            return;
        }

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (String id : students.keySet()) {
            model.addElement(id + " - " + students.get(id).name);
        }
        teacherStudentCombo.setModel(model);
    }

    private String getSelectedStudentId() {
        Object selected = teacherStudentCombo.getSelectedItem();
        if (selected == null) {
            return null;
        }
        return selected.toString().split(" - ")[0];
    }

    private void loadStudentChat() {
        Student student = students.get(loggedInStudentId);
        chatArea.setText("");
        chatArea.append("Bot: Welcome, " + student.name + "!\n");
        chatArea.append("Bot: Ask me about attendance, marks, fees, subjects, or details.\n\n");
    }

    private String getBotResponse(String message) {
        Student student = students.get(loggedInStudentId);
        String lower = message.toLowerCase();

        // The chatbot only reads data for the currently logged-in student.
        if (lower.contains("attendance")) {
            return "Your attendance is " + student.attendance + "%.";
        }
        if (lower.contains("marks")) {
            return "Your marks are " + student.marks + "%.";
        }
        if (lower.contains("fees")) {
            return "Your fees status is " + student.fees + ".";
        }
        if (lower.contains("subjects")) {
            return student.subjects.isEmpty()
                    ? "No subjects have been added yet."
                    : "Your subjects are: " + String.join(", ", student.subjects) + ".";
        }
        if (lower.contains("details")) {
            return "\n" + formatStudentDetails(loggedInStudentId, student);
        }

        return "I can help with attendance, marks, fees, subjects, and details.";
    }

    private String formatStudentDetails(String id, Student student) {
        String subjectsText = student.subjects.isEmpty() ? "None" : String.join(", ", student.subjects);
        return "ID: " + id + "\n"
                + "Name: " + student.name + "\n"
                + "Attendance: " + student.attendance + "%\n"
                + "Marks: " + student.marks + "%\n"
                + "Fees: " + student.fees + "\n"
                + "Subjects: " + subjectsText + "\n";
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}

class Student {
    String name;
    double attendance;
    double marks;
    String fees;
    List<String> subjects;

    Student(String name) {
        this.name = name;
        this.attendance = 0;
        this.marks = 0;
        this.fees = "UNPAID";
        this.subjects = new ArrayList<>();
    }
}