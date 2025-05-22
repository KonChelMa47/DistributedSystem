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
            socket = new Socket("0.0.0.0", 5000);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            writer.println(name);

            new Thread(() -> {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String msg = line;
                        System.out.println("[受信] " + msg);

                        if (msg.endsWith("joined the chat!") || msg.endsWith("has left the chat.")) {
                            displayCenterNotice(msg);
                        } else {
                            String sender = msg.contains(":") ? msg.substring(0, msg.indexOf(":")) : "";
                            String content = msg.contains(":") ? msg.substring(msg.indexOf(":") + 2) : msg;
                            boolean isSelf = sender.equals(name);
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
            // メインコンテナ - 横幅いっぱいを使用
            JPanel mainContainer = new JPanel(new BorderLayout());
            mainContainer.setOpaque(false);
            mainContainer.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

            // 名前とバブルを含むコンテナ
            JPanel bubbleContainer = new JPanel();
            bubbleContainer.setLayout(new BoxLayout(bubbleContainer, BoxLayout.Y_AXIS));
            bubbleContainer.setOpaque(false);

            // 名前ラベル
            JLabel nameLabel = new JLabel(sender);
            nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
            nameLabel.setForeground(Color.GRAY);
            nameLabel.setAlignmentX(isSelf ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

            // バブル作成
            JTextArea bubble = new JTextArea();
            bubble.setEditable(false);
            bubble.setLineWrap(false); // 自動折り返しを無効にして手動制御
            bubble.setWrapStyleWord(false);
            bubble.setFont(new Font("SansSerif", Font.PLAIN, 13));
            bubble.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            bubble.setBackground(isSelf ? new Color(194, 255, 181) : Color.WHITE);
            bubble.setForeground(Color.BLACK);
            bubble.setOpaque(true);

            // 文字数制限に基づいて改行処理
            String processedText = processTextWithLineBreaks(content);
            bubble.setText(processedText);
            
            // 行数を計算
            String[] lines = processedText.split("\n");
            int lineCount = lines.length;
            
            // 最も長い行の幅を計算
            FontMetrics fm = bubble.getFontMetrics(bubble.getFont());
            int maxLineWidth = 0;
            for (String line : lines) {
                int lineWidth = fm.stringWidth(line);
                maxLineWidth = Math.max(maxLineWidth, lineWidth);
            }
            
            // バブルのサイズを設定
            int bubbleWidth = Math.max(maxLineWidth + 24, 60); // パディング24px + 最小幅60px
            int lineHeight = fm.getHeight();
            int bubbleHeight = lineCount * lineHeight + 16; // 上下パディング16px
            
            bubble.setPreferredSize(new Dimension(bubbleWidth, bubbleHeight));
            bubble.setMaximumSize(new Dimension(bubbleWidth, bubbleHeight));
            bubble.setMinimumSize(new Dimension(bubbleWidth, bubbleHeight));
            bubble.setAlignmentX(isSelf ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);

            // バブルコンテナに名前とバブルを追加
            bubbleContainer.add(nameLabel);
            bubbleContainer.add(Box.createVerticalStrut(2));
            bubbleContainer.add(bubble);

            // メインコンテナに配置（自分のメッセージは右、相手のメッセージは左）
            if (isSelf) {
                mainContainer.add(bubbleContainer, BorderLayout.EAST);
            } else {
                mainContainer.add(bubbleContainer, BorderLayout.WEST);
            }

            chatPanel.add(mainContainer);
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

    // 文字数制限に基づいてテキストを改行処理するメソッド
    private String processTextWithLineBreaks(String text) {
        StringBuilder result = new StringBuilder();
        int currentLineLength = 0;
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            
            // 文字の幅を判定（全角文字は2、半角文字は1として計算）
            int charWidth = isFullWidth(c) ? 2 : 1;
            
            // 現在の行の長さが16を超える場合は改行
            if (currentLineLength + charWidth > 16) {
                result.append('\n');
                currentLineLength = 0;
            }
            
            result.append(c);
            currentLineLength += charWidth;
        }
        
        return result.toString();
    }
    
    // 全角文字かどうかを判定するメソッド
    private boolean isFullWidth(char c) {
        // 日本語文字（ひらがな、カタカナ、漢字）や全角記号の判定
        return (c >= 0x3040 && c <= 0x309F) || // ひらがな
               (c >= 0x30A0 && c <= 0x30FF) || // カタカナ
               (c >= 0x4E00 && c <= 0x9FAF) || // 漢字
               (c >= 0xFF01 && c <= 0xFF5E) || // 全角英数記号
               (c >= 0x3000 && c <= 0x303F);   // 全角記号・句読点
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClient::new);
    }
}