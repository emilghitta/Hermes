import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;


public class UpdateInfo extends JFrame {
    private JEditorPane infoPane;
    private JScrollPane scp;
    private JButton ok;
    private JPanel pan1;
    private JPanel pan2;
    private String OS = System.getProperty("os.name");

    public UpdateInfo(String info){
        initComponents();
        infoPane.setText(info);
    }

    private void initComponents(){
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setTitle("New Update found!");

        pan1 = new JPanel();
        pan1.setLayout(new BorderLayout());

        pan2 = new JPanel();
        pan2.setLayout(new FlowLayout());

        infoPane = new JEditorPane();
        infoPane.setContentType("text/html");

        scp = new JScrollPane();
        scp.setViewportView(infoPane);

        ok = new JButton("Update");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    update();
                } catch (IOException | InterruptedException ioException) {
                    ioException.printStackTrace();
                }
            }
        });


        pan2.add(ok);

        pan1.add(pan2,BorderLayout.SOUTH);
        pan1.add(scp,BorderLayout.CENTER);

        this.add(pan1);
        pack();
        show();

        this.setSize(300,200);

    }

    private void update() throws IOException, InterruptedException {
        if(OS.contains("Linux")){
            String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            path = path.replace("Hermes.jar","");
            File location = new File(path);

            String[] run = {"java", "-jar", "Updater_Hermes.jar"};
            System.out.println("We are here");

            Runtime.getRuntime().exec(run,null,location);

        }else{

            String[] run = {"java", "-jar", "Updater_Hermes.jar"};

            try {

                Runtime.getRuntime().exec(run);

            } catch (Exception ex) {

                ex.printStackTrace();
            }
        }


       System.exit(0);

    }



}
