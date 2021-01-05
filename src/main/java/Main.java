import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        //We are setting a User-Agent in the HTTP request header in order to avoid the unknown client download error from archives.mozilla. org
        System.setProperty("http.agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");

        final BuildGiver build = new BuildGiver();
        new Thread(build).start();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                build.createGUI();
            }
        });

    }


}
