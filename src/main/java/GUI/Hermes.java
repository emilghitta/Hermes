package GUI;

import AppEngine.Engine;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashMap;

public class Hermes extends JFrame{
    private JPanel mainPanel;
    private JComboBox fileType;
    private JTextField buildNumber;
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

        //Remove this when implemented
        buildNumber.setEnabled(false);
        buildNumber.setText("Not yet implemented");
        buildVersion.setText("eg. 82.0b4");

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
