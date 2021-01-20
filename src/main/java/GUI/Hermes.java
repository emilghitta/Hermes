package GUI;

import AppEngine.Engine;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Hermes extends JFrame{
    private JPanel mainPanel;
    private JComboBox fileType;
    private JComboBox buildDropdown;
    private JTextField buildVersion;
    private JButton downloadButton;
    private JLabel specifyBuildLabel;
    private JComboBox osVersion;
    private JTextField buildLocale;
    private JCheckBox unarchiveBuilds;
    private JCheckBox autoOpenBuilds;
    private JButton saveToButton;
    private JLabel downloadPathLabel;
    private JLabel errorMessage;
    private JButton downloadMultipleBuildsViaButton;
    private JTextArea batchDownload;
    private JScrollPane scrollbar;
    private JComboBox buildNumberDrop;
    private JLabel enterConfigLabel;
    private JCheckBox unarchiveBuild;
    private JCheckBox autoOpen;
    private String dropdownInput;
    private String typeOfFile;
    private String osSelection;
    private JTextField filename = new JTextField(), dir = new JTextField();
    public String fileNN, dirNN;
    public String buildNumber;


    protected HashMap<String,String> builds = new HashMap<>();

    public Hermes(String title){
        super(title);
        Image iconURL = Toolkit.getDefaultToolkit().getImage("src/main/resources/Hermes.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);


        scrollbar.getVerticalScrollBar().setUnitIncrement(16);

        specifyBuildLabel.setVisible(false);
        buildVersion.setVisible(false);
        enterConfigLabel.setVisible(false);
        batchDownload.setVisible(false);

        buildNumberDrop.addItem("build1");
        buildVersion.setText("eg. 82.0b4");
        buildLocale.setText("eg. en-US");

        batchDownload.setLineWrap(true);


        downloadButton.setEnabled(false);
        downloadPathLabel.setVisible(false);
        errorMessage.setVisible(false);

        fileType.removeAllItems();
        fileType.addItem("archive");
        fileType.addItem("Firefox Setup exe");
        fileType.addItem("Firefox Setup msi");


        this.pack();
        this.setLocationRelativeTo(null);
        this.setResizable(true);

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                if(!batchDownload.getText().isEmpty()){
//                    batchDownload();
//                }

                errorMessage.setVisible(false);
                Engine eng = new Engine();
                dropdownInput = (String) buildDropdown.getSelectedItem();
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                   switch (dropdownInput){
                       case "Latest Nightly":
                           builds.put("latestNightly", "Nightly");
                           fileNN = "LatestNightly" + "-" + buildLocale.getText();
                           break;

                       case "Beta":
                           builds.put("betaVersion", buildVersion.getText());
                           fileNN = buildVersion.getText() + "-" + buildNumberDrop.getSelectedItem() + "-" + buildLocale.getText();
                           break;

                       case "Release" :
                           builds.put("releaseVersion", buildVersion.getText());
                           fileNN = buildVersion.getText() + "-" + buildNumberDrop.getSelectedItem() + "-" + buildLocale.getText();
                           break;

                       case "ESR":
                           builds.put("esrVersion", buildVersion.getText());
                           fileNN = buildVersion.getText() + "-" + buildNumberDrop.getSelectedItem() + "-" + buildLocale.getText();
                           break;

                       case "DevEd":
                           builds.put("devedVersion",buildVersion.getText());
                           fileNN = buildVersion.getText() + "-" + buildNumberDrop.getSelectedItem() + "-" + buildLocale.getText();
                           break;
                   }

                   downloadPathLabel.setText(dirNN + "\\" +fileNN);
                   osSelection = (String) osVersion.getSelectedItem();
                   typeOfFile = (String) fileType.getSelectedItem();
                   buildNumber = (String) buildNumberDrop.getSelectedItem();

                   eng.checkboxes.put("unarchiveBuilds", unarchiveBuilds.isSelected());
                   eng.checkboxes.put("autoOpenBuilds",autoOpenBuilds.isSelected());

                System.out.println(buildNumber);
                System.out.println(fileNN);
                System.out.println(dirNN);

                try {
                    eng.pathFoundation(builds,buildNumber,osSelection,buildLocale.getText(),typeOfFile,fileNN,dirNN);
                    errorMessage.setText("SUCCESS!!");
                    errorMessage.setForeground(Color.green);
                    errorMessage.setVisible(true);
                } catch (IOException ioException) {
                    System.out.println(ioException);
                    errorMessage.setText("Build Download Error: Please double check you Build Version or Locale");
                    errorMessage.setForeground(Color.red);
                    errorMessage.setVisible(true);


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
                    fileType.addItem("archive");
                    fileType.addItem("Firefox Setup exe");
                    fileType.addItem("Firefox Setup msi");
                }else if(osVersion.getSelectedItem() == "win64-aarch64"){
                    fileType.removeAllItems();
                    fileType.addItem("archive");
                    fileType.addItem("Firefox Setup exe");

                }else{
                    fileType.removeAllItems();
                    fileType.addItem("archive");
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
                c.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int rval = c.showSaveDialog(Hermes.this);

                if(rval == JFileChooser.APPROVE_OPTION){
                    if(c.getSelectedFile() ==null){
                        dir.setText(c.getCurrentDirectory().toString());
                    }else if(c.getSelectedFile() != null){
                        dir.setText(c.getSelectedFile().toString());
                    }

                    dirNN = dir.getText();
                    downloadPathLabel.setText(dirNN);
                    downloadPathLabel.setVisible(true);
                    downloadButton.setEnabled(true);
                }
                if(rval == JFileChooser.CANCEL_OPTION){
                    filename.setText("");
                    dir.setText("");
                }

            }
        });

        fileType.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(fileType.getSelectedItem() == "archive" || fileType.getSelectedItem() == "tar.bz2"){
                    unarchiveBuilds.setEnabled(true);
                }else{
                    unarchiveBuilds.setSelected(false);
                    unarchiveBuilds.setEnabled(false);
                }
            }
        });
        buildVersion.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if(buildVersion.getText().contains("eg. 82.0b4")){
                    buildVersion.setText("");
                }

            }
        });
        buildLocale.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                super.focusGained(e);
                if(buildLocale.getText().contains("eg. en-US")){
                    buildLocale.setText("");
                }
            }
        });


        buildVersion.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                System.out.println("entered");
                try {
                    parseHtmlBuildVersion(buildPathForBuildVersion(Objects.requireNonNull(buildDropdown.getSelectedItem()).toString()));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        downloadMultipleBuildsViaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterConfigLabel.setVisible(true);
                batchDownload.setVisible(true);

            }
        });
    }

    private String buildPathForBuildVersion(String build){
        if(!build.contains("DevEd") || !build.contains("Latest Nightly")){
            return "https://archive.mozilla.org/pub/firefox/candidates/" + buildVersion.getText() + "-candidates/";
        }else if(build.contains("DevEd")){
            return "https://archive.mozilla.org/pub/devedition/candidates/" + buildVersion.getText() + "-candidates/";
        }else{
            return "";
        }

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

    public void parseHtmlBuildVersion(String build) throws IOException {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try{
            buildNumberDrop.removeAllItems();

            /*
            If a timeout occurs, an SocketTimeoutException will be thrown. The default timeout is 30 seconds.
            The timeout specifies the combined maximum duration of the connection time and the time to read the full response.

             */
            Document buildNumber = Jsoup.connect(build).timeout(4000).get();
            Elements con = buildNumber.select("a");

            String builds = con.text();
            String item[] = builds.replace("..", "").split("/");

            for(int i = 0; i < item.length; i++){
                //We are trimming the added item so that no trailing or leading space is found. This is needed for our path builder.
                buildNumberDrop.addItem(item[i].trim());
            }
        }catch (HttpStatusException e){
            System.out.println("Resource not found");

        }
        setCursor(Cursor.getDefaultCursor());


    }

//    private void batchDownload(){
//        String text = batchDownload.getText();
//        System.out.println(text);
//    }
}
