import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Server extends Thread {
    Vector<XuLy> vec = new Vector<XuLy>();
    int count = 0;
    int numClient = 0;
    private ServerSocket server;

    public Server(int port, int numClient) {
        try {
            server = new ServerSocket(port);
            this.numClient = numClient;
            this.start();
            while (true) {
                System.out.println("waiting for connection...");
                Socket soc = server.accept();
                System.out.println("new connection");
                XuLy x = new XuLy(soc, this);
                vec.add(x);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
                while (count < this.numClient) {
                    XuLy x = vec.remove(0);
                    if (x == null)
                        break;
                    count++;
                    x.start();
                    System.err.println(count);
                }
            } catch (Exception e) {

            }
        }
    }

    public static void main(String[] args) {
        while (true) {
            JTextField xField = new JTextField("9999");
            JTextField yField = new JTextField("10");

            JPanel myPanel = new JPanel();
            myPanel.add(new JLabel("Nhập cổng:"));
            myPanel.add(xField);
            myPanel.add(Box.createHorizontalStrut(5));
            myPanel.add(new JLabel("Nhập số client:"));
            myPanel.add(yField);

            int result = JOptionPane.showConfirmDialog(null, myPanel, "DNS Server", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {

                try {
                    int port = Integer.parseInt(xField.getText());
                    System.out.println("PORT = " + port);
                    try {
                        int numClient = Integer.parseInt(yField.getText());
                        new Server(port, numClient);
                        break;
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Số Client là một số nguyên");
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Cổng là một số nguyên");
                }
            } else
                System.exit(0);

        }

    }
}

class XuLy extends Thread {
    Socket soc;
    Server ser;
    private Database data;
    private DataInputStream dis;
    private DataOutputStream dos;
    private String message;
    private String answer;
    private boolean listening = true;

    public XuLy(Socket soc, Server ser) {
        this.soc = soc;
        this.ser = ser;

        try {
            dis = new DataInputStream(soc.getInputStream());
            dos = new DataOutputStream(soc.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (listening) {
                data = new Database();
                message = dis.readUTF();
                System.out.println(message);
                if (message.equals("Stop")) {
                    listening = false;
                } else
                    answer = data.get(message);
                System.out.println(answer);
                dos.writeUTF(answer);
            }
            soc.close();
            ser.count--;
            System.err.println(ser.count);
        } catch (UnknownHostException e) {
            e.getMessage();
            System.err.println("Không tìm thấy:" + message + e.getMessage());
            try {
                dos.writeUTF("Không tìm thấy " + message);
            } catch (IOException ex) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (IOException ex) {
            // TODO Auto-generated catch block
            ex.getMessage();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
