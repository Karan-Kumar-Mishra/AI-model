import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.*;
import java.net.*;
import java.net.http.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.*;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.*;
class Animation {
    public void alert(String msg) {
        Font customFont = new Font("Arial", Font.BOLD, 20);
        JFrame mainFrame = new JFrame(msg);
        mainFrame.setBounds(10, 10, 600, 200);
        mainFrame.setLocation(500, 300);
        mainFrame.setLayout(new java.awt.FlowLayout());
        mainFrame.setTitle("Alert ...");
        JTextArea textArea = new JTextArea(msg);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(customFont);
        textArea.setBackground(Color.white);
        textArea.setForeground(Color.red);
        textArea.setSize(300, 100);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 250));
        mainFrame.add(scrollPane);
        mainFrame.add(scrollPane, BorderLayout.CENTER);
        mainFrame.getContentPane().setBackground(Color.red);
        mainFrame.setVisible(true);
        mainFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    public void StartAnimation(String message) throws Exception {
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
                    alert(ex.getMessage());
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

class Button extends JButton {
    private int radius;

    public Button(String label, int radius) {
        super(label);
        this.radius = radius;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (getModel().isArmed()) {
            g2d.setColor(getBackground().darker());
        } else {
            g2d.setColor(getBackground());
        }
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2d.setColor(getForeground());
        FontMetrics metrics = g2d.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(getText())) / 2;
        int y = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.drawString(getText(), x, y);
        g2d.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        
    }
}

class RoundedBorder implements Border {
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

class AImodel implements ActionListener {
    public JFrame MainFrame = new JFrame("AI");
    public JTextField textField = new JTextField("Message ...... ");
    public JTextArea textoutput = new JTextArea(20, 30);
    public Button b = new Button("Send", 50);
    public Button b2 = new Button("Image", 50);
    public ImageIcon icon = new ImageIcon("icon.ico");
    public JScrollPane outputJScrollPane = new JScrollPane(textoutput, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    Animation a1 = new Animation();
    Color color1 = new Color(70, 74, 64);
    Color color2 = new Color(70, 80, 80);
    public String UserInput;
    public String apifileName = "text-api.txt";
    public String imageApi = "image-api.txt";
    public String fileContent;
    public String imageApifileContent;
    public String imageUrl = " ";

    public void getApiKey() throws Exception {
        FileReader fileReader = new FileReader(apifileName);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        fileContent = stringBuilder.toString();
    }

    public AImodel() {
        try {
            getApiKey();
        } catch (Exception ex) {

            Animation a = new Animation();
            a.alert(ex.getMessage() + " api file is not found !");

        }
    }
    public void chatGpt(String UserInput) throws Exception {
        String errorMsg,res=" ";
      
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://chatgpt-42.p.rapidapi.com/conversationgpt4"))
                .header("content-type", "application/json")
                .header("X-RapidAPI-Key", fileContent)
                .header("X-RapidAPI-Host", "chatgpt-42.p.rapidapi.com")
                .method("POST", HttpRequest.BodyPublishers.ofString(
                        "{\r\n    \"messages\": [\r\n        {\r\n            \"role\": \"user\",\r\n            \"content\": \""
                                + UserInput
                                + "\"\r\n        }\r\n    ],\r\n    \"web_access\": false,\r\n    \"system_prompt\": \"\",\r\n    \"temperature\": 0.9,\r\n    \"top_k\": 5,\r\n    \"top_p\": 0.9,\r\n    \"max_tokens\": 256\r\n}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        res = response.body();
        Object obj = JSONValue.parse(res);
        JSONObject jsonObject = (JSONObject) obj;
        res = (String) jsonObject.get("result");
        
        errorMsg=   (String) jsonObject.get("message");
        if(Boolean.parseBoolean(errorMsg)){
            Animation a= new Animation();
            a.alert(errorMsg);
        }
        textoutput.setText(res);
    }

    public void GetIput() {
        UserInput = textField.getText();
    }

    public String SecondImage(String UserInput) throws Exception
    {

        FileReader fileReader = new FileReader("second-image-api.txt");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        String  SecondImageApifileContent = stringBuilder.toString();


        String imageUrlSecond=" " ;
        String text = UserInput;
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://open-ai21.p.rapidapi.com/texttoimage2"))
            .header("content-type", "application/json")
            .header("X-RapidAPI-Key", SecondImageApifileContent)
            .header("X-RapidAPI-Host", "open-ai21.p.rapidapi.com")
            .method("POST", HttpRequest.BodyPublishers.ofString("{\"text\": \"" + text + "\"}"))
            .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()); 
             String res= response.body();
             Object obj = JSONValue.parse(res);
             JSONObject jsonObject = (JSONObject) obj;
            imageUrlSecond = (String) jsonObject.get("generated_image");
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }
        return imageUrlSecond;
    }
    public void get_image() throws Exception {
        String  requestUri;
        GetIput();
        FileReader fileReader = new FileReader(imageApi);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        imageApifileContent = stringBuilder.toString();
        String userPrompt = UserInput;
        String encodedPrompt = URLEncoder.encode(userPrompt, StandardCharsets.UTF_8.toString());

        requestUri = "https://text-to-image7.p.rapidapi.com/?prompt=" + encodedPrompt +
                "&batch_size=1&negative_prompt=ugly%2C%20duplicate%2C%20morbid%2C%20mutilated%2C%20%5Bout%20of%20frame%5D%2C%20extra%20fingers%2C%20mutated%20hands%2C%20poorly%20drawn%20hands%2C%20poorly%20drawn%20face%2C%20mutation%2C%20deformed%2C%20blurry%2C%20bad%20anatomy%2C%20bad%20proportions";
   
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUri))
                .header("X-RapidAPI-Key", imageApifileContent)
                .header("X-RapidAPI-Host","text-to-image7.p.rapidapi.com" )
               
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        Object obj = JSONValue.parse(response.body());
        JSONObject jsonObject = (JSONObject) obj;

        JSONArray dataArray = (JSONArray) jsonObject.get("data");
        String errorMsg = (String) jsonObject.get("message");
        if(dataArray != null && !dataArray.isEmpty()) {
            imageUrl = (String) dataArray.get(0);
        }
         else 
        {
            //pending
           // a1.alert(errorMsg);
            imageUrl=SecondImage(UserInput);
        }
        try {

            BufferedImage image = ImageIO.read(new URL(imageUrl));
            JFrame frame = new JFrame();
            JLabel label = new JLabel(new ImageIcon(image));
            frame.getContentPane().add(label);
            frame.setLocation(20,20);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception ex) {

            // Animation a= new Animation();
            // a.alert(ex.getMessage());
        }
    }

    public void RunGUI() {
        b.setBounds(1250, 740, 100, 60);
        b2.setBounds(100, 740, 100, 60);
        textField.setBounds(340, 740, 800, 60);
        b.setVisible(true);
        b.setVisible(true);
        textField.setVisible(true);
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                textField.setText(" ");
            }
        });
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(new Font("Cursive", Font.PLAIN, 20));
        b.setFont(new Font("Cursive", Font.PLAIN, 25));
        b2.setFont(new Font("Cursive", Font.PLAIN, 25));

        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GetIput();
                try {
                    chatGpt(UserInput);
                } catch (Exception ex) {
                    Animation a = new Animation();
                    a.alert(ex.getMessage());

                }
            }
        });
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                b2.setBackground(Color.black);
                b2.setForeground(Color.white);
                try {
                    if (textoutput.getText() == " ") {
                        a1.StartAnimation("Getting ther response.....");
                    }
                    get_image();
                } catch (Exception ex) {
                    Animation a = new Animation();
                    a.alert(ex.getMessage() + " can not open image !");

                }
            }
        });
        outputJScrollPane.setBounds(0, 0, 1530, 730);
        b.setForeground(Color.white);
        b2.setForeground(Color.white);
        b.setBackground(color2);
        b2.setBackground(Color.gray);
        textField.setBackground(color2);
        textField.setBorder(new RoundedBorder(15));
        textField.setForeground(Color.white);
        textoutput.setSize(2000, 2000);
        textoutput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        textoutput.setVisible(true);
        textoutput.setFont(new Font("cursive", Font.PLAIN, 30));
        textoutput.setForeground(Color.WHITE);
        textoutput.setBackground(Color.black);
        textoutput.setWrapStyleWord(true);
        textoutput.setLayout(new FlowLayout());
        textoutput.setLineWrap(true);
        outputJScrollPane.setBackground(Color.white);
        MainFrame.add(b);
        MainFrame.add(b2);
        MainFrame.add(textField);
        MainFrame.setSize(1800, 1000);
        MainFrame.setLayout(null);
        MainFrame.setVisible(true);
        MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MainFrame.getContentPane().setBackground(color1);
        MainFrame.setIconImage(icon.getImage());
        MainFrame.setResizable(false);
        MainFrame.add(outputJScrollPane);
    }

    public void actionPerformed(ActionEvent e) {
        textField.setText(" ");
    }
}

public class app {
    public static void main(String args[]) {
        AImodel obj1 = new AImodel();
        obj1.RunGUI();
    }
}
