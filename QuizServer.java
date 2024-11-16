import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizServer {
    
    public static void main(String[] args) {
        String serverIP = "127.0.0.1"; // Default IP
        int serverPort = 1234;        // Default Port

        // Read server ip address and port number from info_server.dat
        File configFile = new File("info_server.dat");
        if (configFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
                serverIP = br.readLine().trim();
                serverPort = Integer.parseInt(br.readLine().trim());
                System.out.println("Loaded configuration: " + serverIP + ":" + serverPort);
            } catch (IOException | NumberFormatException e) {
                System.out.println("Error reading configuration file. Using default settings.");
            }
        }

        // Store quiz with List
        List<String> questions = Arrays.asList(
                "What is the capital of France?",
                "What is 5 + 3?",
                "What is 121 - 31?",
                "Who is the most popular in the Christmas? ",
                "What is 123 + 15?",
                "Who was born in 2002, how old are she is now?(now: 2024)",
                "What is 35 - 23?",
                "What is 3 * 4?",
                "What is 11 * 11?",
                "What is 12 * 2?"
        );

        List<String> answers = Arrays.asList("Paris", "8", "90", 
        "Santa", "138", "22", "12", "12", "121", "24");

        ExecutorService pool = Executors.newFixedThreadPool(20);
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server started at " + serverIP + ":" + serverPort);
            System.out.println("Waiting for a client connection...");

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept client connection
                pool.execute(new QuizHandler(clientSocket, questions, answers)); // Handle client in a new thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Handler class to handle individual client (Multi Thread)
    private static class QuizHandler implements Runnable {
        private Socket socket;
        private List<String> questions;
        private List<String> answers;
        //If thread is useable in thread pool, then QuizHandler is available
        QuizHandler(Socket socket, List<String> questions, List<String> answers) {
            this.socket = socket;
            this.questions = questions;
            this.answers = answers;
        }

        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

                Random rand = new Random();
                List<Integer> questionIndices = new ArrayList<>();
                while (questionIndices.size() < 5) {
                    int index = rand.nextInt(questions.size());
                    if (!questionIndices.contains(index)) {
                        questionIndices.add(index);
                    }
                }

                int totalScore = 0;

                // Send questions one by one
                for (int index : questionIndices) {
                    String question = questions.get(index);
                    String correctAnswer = answers.get(index);
                    out.write(question + "\n");
                    out.flush();

                    // Get the client's answer
                    String clientAnswer = in.readLine().trim();
                    System.out.println("Client answered: " + clientAnswer);

                    // Send Correct|Incorrect to client
                    if (clientAnswer.equalsIgnoreCase(correctAnswer)) {
                        out.write("Correct\n"); 
                        totalScore += 20; // 20 points per correct answer(Total 100 points)
                    } else {
                        out.write("Incorrect\n");
                    }
                    out.flush();
                }

                // Send feedback(Incorrect|Correct) and score to the client
                out.write("Your score: " + totalScore + "/100 \n");
                out.flush();

                System.out.println("Session complete. Closing connection.");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Closed: " + socket);
            }
        }
    }
}
