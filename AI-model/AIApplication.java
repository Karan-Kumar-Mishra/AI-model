import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import org.json.simple.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class AIApplication {
    private static class Animation {
        public void startAnimation(String message) throws Exception {
            JFrame frame = new JFrame();
            JProgressBar progressBar = new JProgressBar(0, 100);
            progressBar.setSize(20, 30);
            progressBar.setForeground(Color.gray);
            progressBar.setBackground(Color.black);
            progressBar.setVisible(true);
            Thread progressThread = new Thread(() -> {
                int start = 2;
                progressBar.setValue(start);
                while (start < 100) {
                    try {
                        Thread.sleep(200);
                        start += 1;
                        progressBar.setValue(start);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
                if (start == 100) {
                    frame.dispose();
                }
            });
            progressThread.start();
            progressBar.setVisible(true);
            progressBar.setStringPainted(true);
            frame.setBounds(10, 10, 700, 100);
            frame.setLocation(400, 400);
            frame.add(progressBar);
            frame.setTitle(message);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    private static class RoundedBorder implements Border {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius + 1, radius + 1, radius + 2, radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private static class AIModel implements ActionListener {
        private JFrame mainFrame = new JFrame("AI");
        private JTextField textField = new JTextField("Message ...... ");
        private JTextArea textOutput = new JTextArea(20, 30);
        private Button sendButton = new Button("Send", 50);
        private Button imageButton = new Button("Image", 50);
        private ImageIcon icon = new ImageIcon("icon.ico");
        private JScrollPane outputJScrollPane = new JScrollPane(textOutput,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        private Animation animation = new Animation();
        private Color color1 = new Color(70, 74, 64);
        private Color color2 = new Color(70, 80, 80);
        private String userInput;
        private String apiKeyFileName = "text-api.txt";
        private String imageApiFileName = "image-api.txt";
        private String fileContent;
        private String imageApiFileContent;

        public void getApiKey() throws Exception {
            FileReader fileReader = new FileReader(apiKeyFileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            fileContent = stringBuilder.toString();
        }

        public AIModel() {
            try {
                getApiKey();
            } catch (Exception ex) {
                System.out.println("api file is not found !");
                System.out.println(ex.getMessage());
            }
        }

        public void chatGpt(String userInput) throws Exception {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://chatgpt-42.p.rapidapi.com/conversationgpt4"))
                    .header("content-type", "application/json")
                    .header("X-RapidAPI-Key", fileContent)
                    .header("X-RapidAPI-Host", "chatgpt-42.p.rapidapi.com")
                    .method("POST", HttpRequest.BodyPublishers.ofString(
                            "{\r\n    \"messages\": [\r\n        {\r\n            \"role\": \"user\",\r\n            \"content\": \""
                                    + userInput
                                    + "\"\r\n        }\r\n    ],\r\n    \"web_access\": false,\r\n    \"system_prompt\": \"\",\r\n    \"temperature\": 0.9,\r\n    \"top_k\": 5,\r\n    \"top_p\": 0.9,\r\n    \"max_tokens\": 256\r\n}"))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String res = response.body();
            Object obj = JSONValue.parse(res);
            JSONObject jsonObject = (JSONObject) obj;
            res = (String) jsonObject.get("result");
            textOutput.setText(res);
        }

        public void getInput() {
            userInput = textField.getText();
        }

        public void getImage() throws Exception {
            getInput();
            FileReader fileReader = new FileReader(imageApiFileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            imageApiFileContent = stringBuilder.toString();
            String userPrompt = userInput;
            String imageUrl = " ";
            String encodedPrompt = URLEncoder.encode(userPrompt, StandardCharsets.UTF_8.toString());
            String requestUri = "https://text-to-image7.p.rapidapi.com/?prompt=" + encodedPrompt +
                    "&batch_size=1&negative_prompt=ugly%2C%20duplicate%2C%20morbid%2C%20mutilated%2C%20%5Bout%20of%20frame%5D%2C%20extra%20fingers%2C%20mutated%20hands%2C%20poorly%20drawn%20hands%2C%20poorly%20drawn%20face%2C%20mutation%2C%20deformed%2C%20blurry%2C%20bad%20anatomy%2C%20bad%20proportions";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(requestUri))
                    .header("X-RapidAPI-Key", imageApiFileContent)
                    .header("X-RapidAPI-Host", "text-to-image7.p.rapidapi.com")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            Object obj = JSONValue.parse(response.body());
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray dataArray = (JSONArray) jsonObject.get("data");
            String errorMsg = (String) jsonObject.get("message");
            System.out.println(errorMsg);
            if (dataArray != null && !dataArray.isEmpty()) {
                imageUrl = (String) dataArray.get(0);
            } else {
                textOutput.setText(errorMsg);
            }
            try {
                BufferedImage image = ImageIO.read(new URL(imageUrl));
                JFrame frame = new JFrame();
                JLabel label = new JLabel(new ImageIcon(image));
                frame.getContentPane().add(label);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setVisible(true);
            } catch (Exception ex) {
                System.out.println("Can't not open the image!");
            }
        }

        public void runGUI() {
            sendButton.setBounds(1250, 740, 100, 60);
            imageButton.setBounds(100, 740, 100, 60);
            textField.setBounds(340, 740, 800, 60);
            sendButton.setVisible(true);
            imageButton.setVisible(true);
            textField.setVisible(true);
            textField.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    textField.setText(" ");
                }
            });
            textField.setHorizontalAlignment(JTextField.CENTER);
            textField.setFont(new Font("Cursive", Font.PLAIN, 20));
            sendButton.setFont(new Font("Cursive", Font.PLAIN, 25));
            imageButton.setFont(new Font("Cursive", Font.PLAIN, 25));

            sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getInput();
                    try {
                        chatGpt(userInput);
                    } catch (Exception ex) {
                    }
                }
            });

            imageButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    imageButton.setBackground(Color.black);
                    imageButton.setForeground(Color.white);
                    try {
                        getImage();
                    } catch (Exception ex) {
                        System.out.println("Can not get the image !");
                    }
                }
            });

            outputJScrollPane.setBounds(0, 0, 1530, 730);
            sendButton.setForeground(Color.white);
            imageButton.setForeground(Color.white);
            sendButton.setBackground(color2);
            imageButton.setBackground(Color.gray);
            textField.setBackground(color2);
            textField.setBorder(new RoundedBorder(15));
            textField.setForeground(Color.white);
            textOutput.setSize(2000, 2000);
            textOutput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            textOutput.setVisible(true);
            textOutput.setFont(new Font("cursive", Font.PLAIN, 30));
            textOutput.setForeground(Color.WHITE);
            textOutput.setBackground(Color.black);
            textOutput.setWrapStyleWord(true);
            textOutput.setLayout(new FlowLayout());
            textOutput.setLineWrap(true);
            outputJScrollPane.setBackground(Color.white);

            mainFrame.add(sendButton);
            mainFrame.add(imageButton);
            mainFrame.add(textField);
            mainFrame.setSize(1800, 1000);
            mainFrame.setLayout(null);
            mainFrame.setVisible(true);
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.getContentPane().setBackground(color1);
            mainFrame.setIconImage(icon.getImage());
            mainFrame.setResizable(false);
            mainFrame.add(outputJScrollPane);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            textField.setText(" ");
        }
    }

    public static void main(String args[]) {
        AIModel obj1 = new AIModel();
        obj1.runGUI();
    }
}
