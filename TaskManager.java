import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTable;

public class TaskManager {

    private JFrame frame;
    private DefaultTableModel tableModel;
    private JTable processTable;
    private JTextField processNameField;
    private JButton listProcessesButton;
    private JButton killProcessButton;
    private JTextArea outputTextArea;
    private JButton osNameButton;

    public TaskManager() {
        frame = new JFrame("Task Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 400);

        tableModel = new DefaultTableModel();
        processTable = new JTable(tableModel);
        tableModel.addColumn("Image Name");
        tableModel.addColumn("PID");
        tableModel.addColumn("Session Name");
        tableModel.addColumn("Session#");
        tableModel.addColumn("Mem Usage");

        JScrollPane scrollPane = new JScrollPane(processTable);

        processNameField = new JTextField(20);

        listProcessesButton = new JButton("List Processes");
        listProcessesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listProcesses();
            }
        });

        killProcessButton = new JButton("Kill Process");
        killProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String processName = processNameField.getText();
                killProcess(processName);
            }
        });

        outputTextArea = new JTextArea(10, 40);
        outputTextArea.setEditable(false);

        osNameButton = new JButton("OS Name");
        osNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String os = System.getProperty("os.name");
                outputTextArea.append("Operating System: " + os + "\n");
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Enter Process Name:"));
        panel.add(processNameField);
        panel.add(listProcessesButton);
        panel.add(killProcessButton);
        panel.add(osNameButton);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.add(new JScrollPane(outputTextArea), BorderLayout.NORTH);

        frame.setVisible(true);
    }

    public void listProcesses() {
        String os = System.getProperty("os.name").toLowerCase();

        // Clear the table
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }

        if (os.contains("win")) {
            listProcessesWindows();
        } else if (os.contains("mac")) {
            listProcessesMac();
        } else {
            // Handle unsupported OS
            outputTextArea.append("Unsupported operating system.\n");
        }
    }

    private void listProcessesWindows() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist");
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length >= 5) {
                    tableModel.addRow(new Object[]{
                            parts[0], // Image Name
                            parts[1], // PID
                            parts[2], // Session Name
                            parts[3], // Session#
                            parts[4] // Mem Usage
                    });
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void listProcessesMac() {
        // Add code to list processes on macOS here
        // You can use "ps -e" and parse the output as needed
    }

    private void killProcess(String processName) {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            killProcessWindows(processName);
        } else if (os.contains("mac")) {
            // Add code to kill the process on macOS
            // You can implement the process killing logic for macOS here
            // For example, you can use "pkill" or "killall" commands
            // Be sure to handle any exceptions that may occur
            outputTextArea.append("Process killing is not supported on macOS in this example.\n");
        } else {
            outputTextArea.append("Unsupported operating system.\n");
        }
    }

    // Kill a process by name on Windows
    private void killProcessWindows(String processName) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("taskkill", "/F", "/IM", processName);
            Process process = processBuilder.start();
            process.waitFor();
            outputTextArea.append("Process " + processName + " has been killed.\n");
        } catch (IOException | InterruptedException e) {
            outputTextArea.append("Error: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TaskManager();
            }
        });
    }
}
