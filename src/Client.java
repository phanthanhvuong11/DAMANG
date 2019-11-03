import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class Client {
    private JFrame PhanThanhVuong;
    private JTextField textDiaChiMC;
    private JTextField textIP;
    private JTextField textPort;
    private static Socket socket;
    final String HOST = "localhost";
    private DataOutputStream dos;
    private DataInputStream dis;

    public Client() {
        GUI();
    }

    private void GUI() {
        PhanThanhVuong = new JFrame();
        PhanThanhVuong.getContentPane().setForeground(Color.BLUE);
        PhanThanhVuong.setTitle("Phan Thanh Vương Lớp 16T1");
        PhanThanhVuong.setBounds(100, 100, 450, 319);
        PhanThanhVuong.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        PhanThanhVuong.getContentPane().setLayout(null);

        JLabel lblNewLabel = new JLabel("DNS Client\r\n");
        lblNewLabel.setForeground(Color.BLUE);
        lblNewLabel.setFont(new Font("Times New Roman", Font.BOLD, 18));
        lblNewLabel.setBounds(163, 0, 149, 29);
        PhanThanhVuong.getContentPane().add(lblNewLabel);

        JSeparator separator = new JSeparator();
        separator.setBounds(10, 27, 414, 2);
        PhanThanhVuong.getContentPane().add(separator);

        JLabel lblIP = new JLabel("Nhập IP hoặc tên miền:");
        lblIP.setFont(new Font("Times New Roman", Font.BOLD, 15));
        lblIP.setBounds(10, 91, 158, 24);
        PhanThanhVuong.getContentPane().add(lblIP);

        JLabel lblaDiaChiMayChu = new JLabel("Ðịa chỉ máy chủ:");
        lblaDiaChiMayChu.setFont(new Font("Times New Roman", Font.BOLD, 15));
        lblaDiaChiMayChu.setBounds(10, 56, 135, 24);
        PhanThanhVuong.getContentPane().add(lblaDiaChiMayChu);

        textDiaChiMC = new JTextField();
        textDiaChiMC.setText("localhost");
        textDiaChiMC.setBounds(178, 59, 134, 20);
        PhanThanhVuong.getContentPane().add(textDiaChiMC);
        textDiaChiMC.setColumns(10);

        textIP = new JTextField();
        textIP.setColumns(10);
        textIP.setBounds(178, 90, 246, 24);
        PhanThanhVuong.getContentPane().add(textIP);

        JLabel lblPort = new JLabel("Port:");
        lblPort.setFont(new Font("Times New Roman", Font.BOLD, 15));
        lblPort.setBounds(322, 56, 41, 24);
        PhanThanhVuong.getContentPane().add(lblPort);

        textPort = new JTextField();
        textPort.setText("9999");
        textPort.setColumns(10);
        textPort.setBounds(376, 59, 48, 20);
        PhanThanhVuong.getContentPane().add(textPort);

        JSeparator separator_1 = new JSeparator();
        separator_1.setBounds(10, 127, 414, 2);
        PhanThanhVuong.getContentPane().add(separator_1);

        JLabel lblNewLabel_1 = new JLabel("Kết quả:");
        lblNewLabel_1.setForeground(Color.GREEN);
        lblNewLabel_1.setFont(new Font("Times New Roman", Font.BOLD, 15));
        lblNewLabel_1.setBounds(179, 125, 88, 29);
        PhanThanhVuong.getContentPane().add(lblNewLabel_1);

        JTextArea textArea = new JTextArea();
        textArea.setBounds(10, 153, 414, 70);
        PhanThanhVuong.getContentPane().add(textArea);

        JButton btnTmKim = new JButton("Tìm kiếm");
        btnTmKim.setFont(new Font("Times New Roman", Font.BOLD, 15));
        btnTmKim.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket == null)
                    Connect();
                String sSend = textIP.getText();
                try {
                    dos.writeUTF(sSend);
                    System.out.println("Text = " + sSend);
                    String sReceive = dis.readUTF();
                    if (sReceive.equals(""))
                        textArea.setText("Không tìm thấy!!!");
                    else
                        textArea.setText(sReceive);
                } catch (IOException ex) {
                    ex.getMessage();
                    System.out.println(ex);
                    JOptionPane.showMessageDialog(null, "Can't send to Server", "Error", JOptionPane.INFORMATION_MESSAGE);
                    System.exit(0);
                }
            }
        });
        btnTmKim.setBounds(43, 234, 102, 35);
        PhanThanhVuong.getContentPane().add(btnTmKim);

        JButton btn_reset = new JButton("Reset");
        btn_reset.setFont(new Font("Times New Roman", Font.BOLD, 15));
        btn_reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                textIP.setText("");
                textArea.setText("");
            }
        });
        btn_reset.setBounds(179, 234, 102, 35);
        PhanThanhVuong.getContentPane().add(btn_reset);

        JButton btnExit = new JButton("Exit");
        btnExit.setFont(new Font("Times New Roman", Font.BOLD, 15));
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    dos.writeUTF("Stop");
                    System.exit(0);
                } catch (IOException ex) {
                    ex.getMessage();
                    JOptionPane.showMessageDialog(null, "Can't send to Server", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        btnExit.setBounds(322, 234, 102, 35);
        PhanThanhVuong.getContentPane().add(btnExit);
		btnExit.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    PhanThanhVuong.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void Connect() {
        int port;
        try {
            port = Integer.parseInt(textPort.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "cổng là một số nguyên");
            return;
        }
        try {
            socket = new Socket(textDiaChiMC.getText(), port);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Server chưa được mở", "Error", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}


