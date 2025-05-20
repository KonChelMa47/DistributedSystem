import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ChatClient extends JFrame {
    private JTextField inputField;
    private JButton sendButton;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private String name;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ChatClient() {
        setTitle("LINE風チャット");
        setSize(420, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        name = JOptionPane.showInputDialog(this, "あなたの名前を入力してください：");
        if (name == null || name.trim().isEmpty()) {
            System.exit(0);
        }

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(230, 242, 255));

        JPanel outerPanel = new JPanel(new BorderLayout());
        outerPanel.setBackground(new Color(230, 242, 255));
        outerPanel.add(chatPanel, BorderLayout.NORTH);  // ← 常に上から詰める

        scrollPane = new JScrollPane(outerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("送信");

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        connectToServer();
        setVisible(true);
    }

    private void connectToServer() {
        try {
            socket = new Socket("127.0.0.1", 5000);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            writer.println(name);

            new Thread(() -> {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String msg = line;
                        System.out.println("[受信] " + msg);

                        boolean isSelf = msg.startsWith(name + ":");
                        if (msg.endsWith("joined the chat!") || msg.endsWith("has left the chat.")) {
                            displayCenterNotice(msg);
                        } else {
                            displayLineStyleBubble(msg, isSelf);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("[受信エラー] " + e.getMessage());
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "接続失敗: " + e.getMessage());
            System.exit(1);
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            String fullMessage = name + ": " + text;
            writer.println(fullMessage);
            writer.flush();
            inputField.setText("");
            if (text.equalsIgnoreCase("exit")) {
                System.exit(0);
            }
        }
    }

    private void displayLineStyleBubble(String msg, boolean isSelf) {
        SwingUtilities.invokeLater(() -> {
            JPanel wrapper = new JPanel(new BorderLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

            JTextArea bubble = new JTextArea(msg);
            bubble.setEditable(false);
            bubble.setLineWrap(true);
            bubble.setWrapStyleWord(true);
            bubble.setFont(new Font("SansSerif", Font.PLAIN, 13));
            bubble.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            bubble.setBackground(isSelf ? new Color(194, 255, 181) : Color.WHITE);
            bubble.setForeground(Color.BLACK);
            bubble.setMaximumSize(new Dimension(280, Short.MAX_VALUE));

            JPanel flow = new JPanel(new FlowLayout(isSelf ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
            flow.setOpaque(false);
            flow.add(bubble);

            wrapper.add(flow, isSelf ? BorderLayout.EAST : BorderLayout.WEST);

            chatPanel.add(wrapper);
            chatPanel.revalidate();
            chatPanel.repaint();

            SwingUtilities.invokeLater(() ->
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum())
            );
        });
    }

    private void displayCenterNotice(String msg) {
        SwingUtilities.invokeLater(() -> {
            JLabel notice = new JLabel(msg, SwingConstants.CENTER);
            notice.setFont(new Font("SansSerif", Font.ITALIC, 12));
            notice.setForeground(Color.DARK_GRAY);

            JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            container.setBackground(new Color(230, 242, 255));
            container.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
            container.add(notice);

            chatPanel.add(container);
            chatPanel.revalidate();
            chatPanel.repaint();

            SwingUtilities.invokeLater(() ->
                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum())
            );
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }
}