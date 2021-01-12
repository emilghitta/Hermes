package GUI;

import AppEngine.Engine;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class BuildGiver extends JFrame{
    private JPanel mainPanel;
    private JComboBox fileType;
    private JTextField textField2;
    private JComboBox buildDropdown;
    private JTextField buildVersion;
    private JButton downloadButton;
    private JTextArea jsonText;
    private JTextArea userJSText;
    private JLabel specifyBuildLabel;
    private JComboBox osVersion;
    private JTextField buildLocale;
    private String dropdownInput;
    private String typeOfFile;
    private String osSelection;

    protected HashMap<String,String> builds = new HashMap<>();

    public BuildGiver(String title){
        super(title);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);


         specifyBuildLabel.setVisible(false);
         buildVersion.setVisible(false);
         jsonText.setLineWrap(true);
         userJSText.setLineWrap(true);

        fileType.removeAllItems();
        fileType.addItem("zip");
        fileType.addItem("exe");
        fileType.addItem("Firefox Setup exe");
        fileType.addItem("Firefox Setup msi");

         this.setLocationRelativeTo(null);
         this.setSize(900,400);
         this.setResizable(false);


        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                   Engine eng = new Engine();
                   dropdownInput = (String) buildDropdown.getSelectedItem();

                   switch (dropdownInput){
                       case "Latest Nightly":
                           builds.put("latestNightly", "nightly/latest-mozilla-central/");
                           break;

                       case "Beta":
                           builds.put("betaVersion", buildVersion.getText());
                           break;

                       case "Release" :
                           builds.put("releaseVersion", buildVersion.getText());
                           break;

                       case "ESR":
                           builds.put("esrVersion", buildVersion.getText());
                           break;

                       case "DevEd":
                           builds.put("devedVersion",buildVersion.getText());
                           break;
                   }

                   osSelection = (String) osVersion.getSelectedItem();
                   typeOfFile = (String) fileType.getSelectedItem();


                try {
                    eng.pathFoundation(builds,osSelection,typeOfFile);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }


            }
        });


        buildDropdown.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if(buildDropdown.getSelectedItem() == "Latest Nightly"){
                    specifyBuildLabel.setVisible(false);
                    buildVersion.setVisible(false);
                }else{
                    specifyBuildLabel.setVisible(true);
                    buildVersion.setVisible(true);
                }
            }
        });
        osVersion.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {


                if(osVersion.getSelectedItem() == "linux-i686" || osVersion.getSelectedItem() == "linux-x86_64"){
                    fileType.removeAllItems();
                    fileType.addItem("tar.bz2");
                }else if(osVersion.getSelectedItem() == "mac"){
                    fileType.removeAllItems();
                    fileType.addItem("dmg");
                    fileType.addItem("pkg");
                }else if(osVersion.getSelectedItem() == "win64"){
                    fileType.removeAllItems();
                    fileType.addItem("zip");
                    fileType.addItem("exe");
                    fileType.addItem("Firefox Setup exe");
                    fileType.addItem("Firefox Setup msi");
                }else if(osVersion.getSelectedItem() == "win64-aarch64"){
                    fileType.removeAllItems();
                    fileType.addItem("zip");
                    fileType.addItem("exe");

                }else{
                    fileType.removeAllItems();
                    fileType.addItem("zip");
                    fileType.addItem("Firefox Installer.exe");
                    fileType.addItem("Firefox Setup exe");
                    fileType.addItem("Firefox Setup msi");
                }
            }
        });
    }


    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new BuildGiver("Build Giver");
                frame.setVisible(true);
            }
        });


    }

}
