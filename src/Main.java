import javax.rmi.CORBA.Util;
import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static final String LOGIN = "login";
    public static final String PASSWORD = "password";
    public static final String ACTION = "action";

    public static void main(String[] args) {
        String login = null;
        String pass = null;
        boolean logIn = false;

        try (Scanner scanner = new Scanner(System.in)) {
            while (!logIn) {
                if (login == null || pass == null || !authorization(Utils.getURL() + "/authorization", login, pass)) {
                    System.out.println("Enter your login: ");
                    login = scanner.nextLine();
                    System.out.println("Enter your password: ");
                    pass = scanner.nextLine();
                } else {
                    logIn = true;
                    Thread th = new Thread(new GetThread(login));
                    th.setDaemon(true);
                    th.start();

                    System.out.println("Enter your message: ");
                    while (true) {
                        String text = scanner.nextLine();
                        if (text.isEmpty()) break;
                        if (text.startsWith("/")) {
                            if (text.substring(1).equals("getList")) {
                                getUserList();
                            }
                        } else {
                            Message m = new Message(login, text);
                            int res = m.send(Utils.getURL() + "/add");

                            if (res != 200) { // 200 OK
                                System.out.println("HTTP error occured: " + res);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static boolean authorization(String url, String login, String pass) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("POST");
        String requestParameters = ACTION + "=logIn" + "&" + LOGIN + "=" + login + "&" + PASSWORD + "=" + pass;
        byte[] postData = requestParameters.getBytes(StandardCharsets.UTF_8);
        conn.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }

        int responseCode = conn.getResponseCode();
        return responseCode == 200;
    }

    private static void getUserList() throws IOException {
        URL obj = new URL(Utils.getURL() + "/authorization");
        HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
        conn.setRequestMethod("POST");
        String requestParameters = ACTION + "=getList";
        byte[] postData = requestParameters.getBytes(StandardCharsets.UTF_8);
        conn.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }
        if (conn.getResponseCode() == 200) {
            try (InputStream is = conn.getInputStream()) {
                if (is != null) {
                    byte[] buf = Utils.requestBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);
                    System.out.println("User online: " + strBuf);
                }
            }
        }
    }
}
