package GUI;

import AppEngine.Engine;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.HashMap;

public class Hermes extends JFrame{
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
    private JCheckBox unarchiveBuilds;
    private JCheckBox autoOpenBuilds;
    private JButton saveToButton;
    private JLabel downloadPathLabel;
    private JCheckBox unarchiveBuild;
    private JCheckBox autoOpen;
    private String dropdownInput;
    private String typeOfFile;
    private String osSelection;
    private JTextField filename = new JTextField(), dir = new JTextField();
    public String fileNN, dirNN;

    protected HashMap<String,String> builds = new HashMap<>();

    public Hermes(String title){
        super(title);
        Image iconURL = Toolkit.getDefaultToolkit().getImage("src/main/resources/Hermes.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);

         specifyBuildLabel.setVisible(false);
         buildVersion.setVisible(false);
         jsonText.setLineWrap(true);
         userJSText.setLineWrap(true);

         //Enable this when we implement it successfully
        jsonText.setEnabled(false);
        userJSText.setEnabled(false);

        downloadButton.setEnabled(false);
        downloadPathLabel.setVisible(false);

        fileType.removeAllItems();
        fileType.addItem("zip");
        fileType.addItem("Firefox Setup exe");
        fileType.addItem("Firefox Setup msi");


        this.setSize(1100,400);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                   Engine eng = new Engine();
                   dropdownInput = (String) buildDropdown.getSelectedItem();
                   setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));


                   switch (dropdownInput){
                       case "Latest Nightly":
                           builds.put("latestNightly", "Nightly");
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

                   eng.checkboxes.put("unarchiveBuilds", unarchiveBuilds.isSelected());
                   eng.checkboxes.put("autoOpenBuilds",autoOpenBuilds.isSelected());

                System.out.println(fileNN);
                System.out.println(dirNN);


                try {
                    eng.pathFoundation(builds,osSelection,buildLocale.getText(),typeOfFile,fileNN,dirNN);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                setCursor(Cursor.getDefaultCursor());
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
                    fileType.addItem("Firefox Setup exe");
                    fileType.addItem("Firefox Setup msi");
                }else if(osVersion.getSelectedItem() == "win64-aarch64"){
                    fileType.removeAllItems();
                    fileType.addItem("zip");
                    fileType.addItem("Firefox Setup exe");

                }else{
                    fileType.removeAllItems();
                    fileType.addItem("zip");
                    fileType.addItem("Firefox Installer.exe");
                    fileType.addItem("Firefox Setup exe");
                    fileType.addItem("Firefox Setup msi");
                }
            }
        });
        saveToButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser c = new JFileChooser();
                int rVal = c.showSaveDialog(Hermes.this);
                if(rVal == JFileChooser.APPROVE_OPTION){
                    filename.setText(c.getSelectedFile().getName().trim());
                    dir.setText(c.getCurrentDirectory().toString());
                    fileNN = filename.getText();
                    dirNN = dir.getText();
                    downloadPathLabel.setText(dirNN + "\\" +fileNN);
                    downloadPathLabel.setVisible(true);
                    downloadButton.setEnabled(true);
                }
                if(rVal == JFileChooser.CANCEL_OPTION){
                    filename.setText("");
                    dir.setText("");
                }

            }
        });

        fileType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(fileType.getSelectedItem() == "zip" || fileType.getSelectedItem() == "tar.bz2"){
                    unarchiveBuilds.setEnabled(true);
                }else{
                    unarchiveBuilds.setSelected(false);
                    unarchiveBuilds.setEnabled(false);
                }
            }
        });
    }

    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new Hermes("Hermes");
                frame.setVisible(true);
            }
        });

    }
}
