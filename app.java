import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.*;
import java.net.*;
import java.net.http.*;
import org.json.simple.*;
import java.util.Scanner;
import javax.swing.border.*;
import javax.swing.border.Border;
class WeatherApp implements ActionListener {
    public JFrame MainFrame = new JFrame("AI");
    public JTextField textField = new JTextField("Message ...... ");
    public JTextArea textoutput = new JTextArea(20,30);
    public JButton b = new JButton("Send");
    public ImageIcon icon = new ImageIcon("icon.png");
    public JScrollPane outputJScrollPane =new JScrollPane(textoutput,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    Color color1= new Color(70,74,64);
    Color color2= new Color(70,80,80);
    public String UserInput;
    public void chatGpt(String UserInput) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://open-ai21.p.rapidapi.com/conversationgpt35"))
                .header("content-type", "application/json")
                .header("X-RapidAPI-Key", "59e9459c0cmsh043662e8782d258p18e217jsn68b075e55873")
                .header("X-RapidAPI-Host", "open-ai21.p.rapidapi.com")
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
    public void RunGUI() {
        b.setBounds(1250, 740, 100, 50);
        textField.setBounds(340, 740, 800, 50);
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
        b.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GetIput();
                try {
                    chatGpt(UserInput);
                } catch (Exception ex) {
                }
            }
        });
        outputJScrollPane.setBounds(0, 0, 1530, 730); 
        b.setForeground(Color.white);
        b.setBackground(color2);
        textField.setBackground(color2);
        textField.setForeground(Color.white);
        textoutput.setSize(1000, 550);
        textoutput.setVisible(true);
        textoutput.setFont(new Font("cursive", Font.PLAIN, 30));
        textoutput.setForeground(Color.WHITE);
        textoutput.setBackground(color1);
        textoutput.setLineWrap(true);
        textoutput.setWrapStyleWord(true);
        outputJScrollPane.setBackground(Color.white);
        MainFrame.add(b);
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
        WeatherApp obj1 = new WeatherApp();
        obj1.RunGUI();
    }
}