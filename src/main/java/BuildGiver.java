import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BuildGiver implements ActionListener, Runnable{
    private JButton downloadButton;
    private JProgressBar progressBar1;
    private JTextField betaBuild;
    private JCheckBox nightlyBuilds;
    private JCheckBox unarchiveBuilds;
    private JCheckBox openBuilds;
    private JTextField releaseBuild;
    private JTextField esrBuild;
    private JTextField devedBuild;
    private JPanel apanel;
    static BuildGiver _this;

    protected HashMap<String,String> builds = new HashMap<String, String>();
    protected HashMap<String,Boolean> checkboxes = new HashMap<String, Boolean>();


    //Maybe add those inside an HashMap?

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

        //Unarchive Builds?
        apanel.add(new JLabel("Unarchive downloaded builds?"));
        unarchiveBuilds = new JCheckBox();
        apanel.add(unarchiveBuilds);

        //Open Builds?
        apanel.add(new JLabel("Open Downloaded Builds?"));
        openBuilds = new JCheckBox();
        apanel.add(openBuilds);

        //Download Progress Bar

        apanel.add(new JLabel("Downloading Build Status:"));
        //progressBar1 = new JProgressBar(0,100);
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


        while(true){
            // wait for the signal from the GUI
            try{synchronized(this){wait();}}
            catch (InterruptedException e){}
            // simulate some long-running process like parsing a large file
                try {
                    Engine eng = new Engine();

                    builds.put("betaInputField",betaBuild.getText());
                    builds.put("releaseInputField",releaseBuild.getText());
                    builds.put("esrInputField",esrBuild.getText());
                    builds.put("devedInputField",devedBuild.getText());

                    checkboxes.put("nightlyBuildCheckbox",nightlyBuilds.isSelected());
                    checkboxes.put("unarchiveBuildsCheckbox",unarchiveBuilds.isSelected());
                    checkboxes.put("openBuildsCheckbox",openBuilds.isSelected());


                    eng.pathFoundation(builds,checkboxes);


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



