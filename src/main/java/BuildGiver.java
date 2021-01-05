import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class BuildGiver implements ActionListener, Runnable{
    private JButton downloadButton;
    private JProgressBar progressBar1;
    private JTextField betaBuild;
    private JCheckBox nightlyBuilds;
    private JTextField releaseBuild;
    private JTextField esrBuild;
    private JTextField devedBuild;
    private JPanel apanel;
    static BuildGiver _this;

    public BuildGiver (){
        _this = this;
    }

    protected void createGUI(){
        //Creating the main frame
        JFrame frame = new JFrame("BuildGiver");
        frame.getContentPane().add(downloadButton,BorderLayout.SOUTH);
        downloadButton.addActionListener(_this);

        apanel = new JPanel();

        //Nightly Builds
        apanel.add(new JLabel("Latest Nightly Build: "));
        nightlyBuilds = new JCheckBox();
        apanel.add(nightlyBuilds);

        //Beta Builds

        apanel.add(new JLabel("Beta Build:"));
        betaBuild = new JTextField(10);
        apanel.add(betaBuild);

        //Release
        apanel.add(new JLabel("Release Build:"));
        releaseBuild = new JTextField(10);
        apanel.add(releaseBuild);

        //ESR
        apanel.add(new JLabel("ESR Build:"));
        esrBuild = new JTextField(10);
        apanel.add(esrBuild);

        //DevEdition
        apanel.add(new JLabel("DevEdition build:"));
        devedBuild = new JTextField(10);
        apanel.add(devedBuild);


        //Download Progress Bar

        apanel.add(new JLabel("Downloading Build Status:"));
        progressBar1 = new JProgressBar(0,100);
        apanel.add(progressBar1);


        frame.setLocationRelativeTo(null);
        frame.getContentPane().add(apanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        synchronized (this){
            notifyAll();
        }
    }

    @Override
    public void run() {
        boolean nightlyBuild;
        String betaInputField;
        String releaseInputField;
        String esrInputField;
        String devedInputField;

        while(true){
            // wait for the signal from the GUI
            try{synchronized(this){wait();}}
            catch (InterruptedException e){}
            // simulate some long-running process like parsing a large file
                try {
                    Engine eng = new Engine();
                    String input = betaBuild.getText();


                    //eng.initiateDownload(eng.targetURL, eng.targetURL);
                    progressBar1.setValue(100);




                    eng.unzip();
                    eng.launchFirefox();
                    eng.quit();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



