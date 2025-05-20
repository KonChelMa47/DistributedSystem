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
    private final int MAX_BUBBLE_WIDTH = 160;

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
        outerPanel.add(chatPanel, BorderLayout.NORTH);

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
            socket = new Socket("172.18.5.130", 5000);
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
                            String sender = msg.contains(":") ? msg.substring(0, msg.indexOf(":")) : "";
                            String content = msg.contains(":") ? msg.substring(msg.indexOf(":") + 2) : msg;
                            displayBubbleWithName(sender, content, isSelf);
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

    private void displayBubbleWithName(String sender, String content, boolean isSelf) {
        SwingUtilities.invokeLater(() -> {
            JPanel wrapper = new JPanel();
            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
            wrapper.setOpaque(false);
            wrapper.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

            JLabel nameLabel = new JLabel(sender);
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            nameLabel.setForeground(Color.GRAY);
            nameLabel.setAlignmentX(isSelf ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

            JTextArea bubble = new JTextArea(content);
            bubble.setEditable(false);
            bubble.setLineWrap(true);
            bubble.setWrapStyleWord(true);
            bubble.setFont(new Font("SansSerif", Font.PLAIN, 13));
            bubble.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
            bubble.setBackground(isSelf ? new Color(194, 255, 181) : Color.WHITE);
            bubble.setForeground(Color.BLACK);

            // 文字幅を測定
            FontMetrics fm = bubble.getFontMetrics(bubble.getFont());
            int textWidth = fm.stringWidth(content) + 20;
            int width = Math.min(textWidth, MAX_BUBBLE_WIDTH);

            // JTextArea に幅を設定（自動高さ）
            bubble.setMaximumSize(new Dimension(width, Integer.MAX_VALUE));
            bubble.setPreferredSize(new Dimension(width, bubble.getPreferredSize().height));
            bubble.setPreferredSize(null);  // 高さは自動で任せる
            bubble.setAlignmentX(isSelf ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

            wrapper.add(nameLabel);
            wrapper.add(Box.createVerticalStrut(2));
            wrapper.add(bubble);

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
            container.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
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
