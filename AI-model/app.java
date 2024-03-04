import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.*;
import java.net.*;
import java.net.http.*;
import org.json.simple.*;
import javax.swing.border.*;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.util.*; 
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
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
        // Do not paint border
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
    public JTextArea textoutput = new JTextArea(20,30);
    public Button b = new Button("Send", 50);
    public Button b2 = new Button("Image", 50);
    public ImageIcon icon = new ImageIcon("icon.ico");
    public JScrollPane outputJScrollPane =new JScrollPane(textoutput,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    Color color1= new Color(70,74,64);
    Color color2= new Color(70,80,80);
    public String UserInput;
    public String apifileName = "text-api.txt"; 
    public String imageApi="image-api.txt";
    public String fileContent;
    public String imageApifileContent;

    public void getApiKey() throws Exception
    {
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
    public AImodel() 
    {
        try{
            getApiKey();
        }catch(Exception ex)
        {
            System.out.println("api file is not found !");
            System.out.println(ex.getMessage());
        }
    }
    public void chatGpt(String UserInput) throws Exception {
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
        String res = response.body();
        Object obj = JSONValue.parse(res);
        JSONObject jsonObject = (JSONObject) obj;
        res = (String) jsonObject.get("result");
        textoutput.setText(res);
    }
    public void GetIput() {
        UserInput = textField.getText();
    }
    public void get_image() throws Exception{

    
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
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("https://chatgpt-42.p.rapidapi.com/texttoimagetv"))
        .header("content-type", "application/json")
        .header("X-RapidAPI-Key", imageApifileContent)
        .header("X-RapidAPI-Host", "chatgpt-42.p.rapidapi.com")
        .method("POST", HttpRequest.BodyPublishers.ofString("{\r\n    \"text\": \"" + UserInput + "\"\r\n}"))
        .build();

    // Send the request and retrieve the response
    HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
     String  resUrlOfimage =response.body();
		
        Object obj = JSONValue.parse(resUrlOfimage);
        JSONObject jsonObject = (JSONObject) obj;
        resUrlOfimage = (String) jsonObject.get("generated_image");
       try{

           BufferedImage image = ImageIO.read(new URL(resUrlOfimage));
           JFrame frame = new JFrame();
           JLabel label = new JLabel(new ImageIcon(image));
           frame.getContentPane().add(label);
           frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
           frame.pack();
           frame.setVisible(true);
        }catch(Exception ex)
        {
            System.out.println("Cant not open the image!");
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
                }
            }
        });
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                b2.setBackground(Color.black);
                b2.setForeground(Color.white);
                try{
                   get_image();
                }catch(Exception ex)
                {
                  System.out.println("Can not get the image !");
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
        textoutput.setSize(1000, 1000);
        textoutput.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        textoutput.setVisible(true);
        textoutput.setFont(new Font("cursive", Font.PLAIN, 30));
        textoutput.setForeground(Color.WHITE);
        textoutput.setBackground(Color.black);
        textoutput.setLineWrap(true);
        textoutput.setWrapStyleWord(true);
        textoutput.setLayout(new FlowLayout());
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
