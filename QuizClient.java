import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class QuizClient extends JFrame {
    private JTextArea questionArea;
    private JTextField answerField;
    private JButton submitButton;
    private JLabel feedbackLabel;
    private JLabel scoreLabel;

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private int currentQuestion = 0;
    private int totalQuestions = 5;

    public QuizClient() {
        // GUI Setup 
        setTitle("Quiz Client");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        questionArea = new JTextArea();
        questionArea.setEditable(false);
        add(new JScrollPane(questionArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        answerField = new JTextField();
        submitButton = new JButton("Submit");
        inputPanel.add(answerField, BorderLayout.CENTER);
        inputPanel.add(submitButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        feedbackLabel = new JLabel(" ");
        scoreLabel = new JLabel(" ");
        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        statusPanel.add(feedbackLabel);
        statusPanel.add(scoreLabel);
        add(statusPanel, BorderLayout.NORTH);

        // Server Connection
        connectToServer();

        // Button GUI
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendAnswer();
            }
        });

        // Load first question
        loadQuestion();
    }

    private void connectToServer() {
        String serverIP = "127.0.0.1"; // Default IP
        int serverPort = 1234;        // Default Port

        File configFile = new File("info_server.dat");
        if (configFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
                serverIP = br.readLine().trim();
                serverPort = Integer.parseInt(br.readLine().trim());
                System.out.println("Loaded configuration: " + serverIP + ":" + serverPort);
            } catch (IOException e) {
                System.out.println("Error reading configuration file. Using default settings.");
            }
        }

        try {
            socket = new Socket(serverIP, serverPort); // Confirmnig request connected

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("Connected to server!");
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unable to connect to server.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void loadQuestion() {
        try {
            String question = in.readLine();
            if (question != null) {
                questionArea.setText("Q" + (currentQuestion + 1)+"." + question);
                feedbackLabel.setText(" ");
                answerField.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAnswer() {
        try {
            String userAnswer = answerField.getText().trim();
            out.write(userAnswer + "\n");
            out.flush();

            String feedback = in.readLine();
            System.out.println("Feedback from server: " + feedback); // Add for to see result(Correct/Incorrect)

            currentQuestion++;
            if (currentQuestion < totalQuestions) {
                loadQuestion();
            } else {
                String finalScore = in.readLine();
                scoreLabel.setText(finalScore);
                submitButton.setEnabled(false);
                JOptionPane.showMessageDialog(this, finalScore, "Quiz Completed", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new QuizClient().setVisible(true);
            }
        });
    }
}
