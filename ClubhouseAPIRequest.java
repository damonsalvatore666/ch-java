import java.io.*;
import java.net.*;
import java.util.*;

public class ClubhouseAPIRequest {

    public static void main(String[] args) {
        boolean continueExecution = true;
        while (continueExecution) {
            try {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Enter Room ID : ");
                String channel = scanner.nextLine();

                Map<String, String> initialHeaders = prepareInitialRequestHeaders();

                if (initialHeaders != null) {
                    HttpURLConnection initialConn = sendInitialRequest(initialHeaders, channel);

                    if (initialConn != null) {
                        while (true) {
                            sendActivePingRequest(initialHeaders, "{\r\n  \"channel\": \"" + channel + "\"\r\n}");
                            Thread.sleep(20000); 
                        }
                    } else {
                        System.out.println("Connection couldn't be established. Exiting.");
                        continueExecution = false;
                        break;
                    }
                } else {
                    System.out.println("Error preparing initial headers. Exiting.");
                    continueExecution = false;
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
                continueExecution = false;
                break;
            }
        }
    }

    private static Map<String, String> prepareInitialRequestHeaders() {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Ch-Languages", "en-US");
            headers.put("Ch-Locale", "en_US");
            headers.put("Accept", "application/json");
            headers.put("Accept-Encoding", "gzip, deflate");
            headers.put("Ch-Appbuild", "304");
            headers.put("Ch-Appversion", "0.1.28");
            headers.put("Ch-Userid", "1387526936");
            headers.put("User-Agent", "clubhouse/304 (iPhone; iOS 14.4; Scale/2.00)");
            headers.put("Content-Type", "application/json; charset=utf-8");
            headers.put("Authorization", "Token b2950fc48c344dd73b37bb0f275ced8f34d53368");
            return headers;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static HttpURLConnection sendInitialRequest(Map<String, String> headers, String channel) {
        try {
            URI url = new URI("https://www.clubhouseapi.com/api/join_channel");
            HttpURLConnection conn = (HttpURLConnection) url.toURL().openConnection();
            conn.setRequestMethod("POST");

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            conn.setDoOutput(true);

            String requestBody = "{\r\n  \"channel\": \"" + channel + "\"\r\n}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Join Room Response Code: " + responseCode);

            if (responseCode == 200) {
                return conn;
            } else {
                conn.disconnect();
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void sendActivePingRequest(Map<String, String> headers, String requestBody) {
        try {
            URI url = new URI("https://www.clubhouseapi.com/api/active_ping");
            HttpURLConnection conn = (HttpURLConnection) url.toURL().openConnection();

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            conn.setRequestMethod("POST");

            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Active Ping Response Code: " + responseCode);
            System.out.println("waiting for next 20 seconds...");

            conn.disconnect();
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
