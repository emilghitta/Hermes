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


        scrollbar.getVerticalScrollBar().setUnitIncrement(16);

        specifyBuildLabel.setVisible(false);
        buildVersion.setVisible(false);

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
                errorMessage.setVisible(false);
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
                buildNumberDrop.addItem(item[i]);
            }
        }catch (HttpStatusException e){
            System.out.println("Resource not found");

        }
        setCursor(Cursor.getDefaultCursor());


    }
}
