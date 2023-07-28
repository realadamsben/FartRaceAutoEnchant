package im.adams.frae;

import com.google.gson.Gson;
import net.sourceforge.tess4j.util.LoadLibs;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainFrame extends JFrame implements ActionListener, NativeKeyListener, NativeMouseInputListener {

    static Config config;
    private JScrollPane logScrollPane;
    private JTextArea logTextArea;



    public JPanel mainPanel;
    public JTextField tfXCoordEnchant;
    public JTextField tfYCoordEnchant;
    public JTextField tfXCoordConfirm;
    public JTextField tfYCoordConfirm;
    public JButton btnSetEnchant;
    public JButton btnSetConfirm;
    public JTextField tfTargetVal;
    public JButton btnSaveCfg;
    public JTextField tfWebhookURL;
    private JButton btnSetTopLeft;
    private JButton btnSetTopRight;
    private JTextField tfXCoordBottomRight;
    private JTextField tfXCoordTopLeft;
    private JTextField tfYCoordTopLeft;
    private JTextField tfYCoordBottomRight;

    String waitingOn = "";

    static JFrame frame;

    static boolean run = false;

    public MainFrame() throws NativeHookException {
        tfXCoordEnchant.setText(String.valueOf(config.xPosEnchant));
        tfYCoordEnchant.setText(String.valueOf(config.yPosEnchant));
        tfXCoordConfirm.setText(String.valueOf(config.xPosConfirm));
        tfYCoordConfirm.setText(String.valueOf(config.yPosConfirm));
        tfXCoordTopLeft.setText(String.valueOf(config.xPosTopLeft));
        tfYCoordTopLeft.setText(String.valueOf(config.yPosTopLeft));
        tfXCoordBottomRight.setText(String.valueOf(config.xPosBottomRight));
        tfYCoordBottomRight.setText(String.valueOf(config.yPosBottomRight));
        tfTargetVal.setText(String.valueOf(config.targetNum));
        tfWebhookURL.setText(config.webhookURI);

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage()
                .getName());
        logger.setLevel(Level.OFF);
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(this);
        GlobalScreen.addNativeMouseListener(this);

        btnSetEnchant.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log("Now waiting for click on Enchant button!");
                waitingOn = "enchant";
            }
        });
        btnSetConfirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log("Now waiting for click on Confirm button!");
                waitingOn = "confirm";
            }
        });
        btnSetTopLeft.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log("Now waiting for click on TopLeft button!");
                waitingOn = "topleft";
            }
        });
        btnSetTopRight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log("Now waiting for click on TopRight button!");
                waitingOn = "bottomright";
            }
        });
        btnSaveCfg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                config.webhookURI = tfWebhookURL.getText();
                config.xPosEnchant = Integer.parseInt(tfXCoordEnchant.getText());
                config.yPosEnchant = Integer.parseInt(tfYCoordEnchant.getText());
                config.xPosConfirm = Integer.parseInt(tfXCoordConfirm.getText());
                config.yPosConfirm = Integer.parseInt(tfYCoordConfirm.getText());

                config.xPosTopLeft = Integer.parseInt(tfXCoordTopLeft.getText());
                config.yPosTopLeft = Integer.parseInt(tfYCoordTopLeft.getText());
                config.xPosBottomRight = Integer.parseInt(tfXCoordBottomRight.getText());
                config.yPosBottomRight = Integer.parseInt(tfYCoordBottomRight.getText());
                config.targetNum = Integer.parseInt(tfTargetVal.getText());


                File configFile = new File("frae.cfg");
                PrintStream write = null;
                try {
                    write = new PrintStream(configFile);
                } catch (FileNotFoundException ex) {
                    new JOptionPane("There was an error saving!");
                }
                write.print(new Gson().toJson(config));
                write.close();
                log("Config saved!");
            }
        });

    }



    static void initConfig() throws FileNotFoundException {
        File configFile = new File("frae.cfg");

        if(configFile.exists()){
            Reader reader = new FileReader(configFile);
            config = new Gson().fromJson(reader, Config.class);
        } else {
            config = new Config();
        }
    }

    public static void main(String[] args){
        try{
            initConfig();


            File tmpFolder = LoadLibs.extractTessResources("win32-x86-64");
            System.setProperty("java.library.path", tmpFolder.getPath());

            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

            frame = new JFrame("FRAE 1.0 - Not running!");
            frame.setContentPane(new MainFrame().mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setAlwaysOnTop(true);
            frame.pack();
            frame.setVisible(true);

        } catch (Exception e){
            JOptionPane.showMessageDialog(null, "There was an error!\n" + e.toString());
            throw new RuntimeException();
        }
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        if(nativeKeyEvent.paramString().contains("keyText=Equals,keyChar=Undefined,modifiers=Shift")){
            run = !run;
            if(run){
                log("Now running!");
                frame.setTitle("FRAE 1.0 - Running!");
                new CoreThread().start();
            } else {
                log("Stopping!");
                frame.setTitle("FRAE 1.0 - Not running!");
            }
        }
    }
    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeMouseEvent) {
        if(!waitingOn.equals("")){
            if(waitingOn.equals("enchant")){
                tfXCoordEnchant.setText(String.valueOf(nativeMouseEvent.getX()));
                tfYCoordEnchant.setText(String.valueOf(nativeMouseEvent.getY()));
            }
            if(waitingOn.equals("confirm")){
                tfXCoordConfirm.setText(String.valueOf(nativeMouseEvent.getX()));
                tfYCoordConfirm.setText(String.valueOf(nativeMouseEvent.getY()));

            }
            if(waitingOn.equals("topleft")){
                tfXCoordTopLeft.setText(String.valueOf(nativeMouseEvent.getX()));
                tfYCoordTopLeft.setText(String.valueOf(nativeMouseEvent.getY()));

            }
            if(waitingOn.equals("bottomright")){
                tfXCoordBottomRight.setText(String.valueOf(nativeMouseEvent.getX()));
                tfYCoordBottomRight.setText(String.valueOf(nativeMouseEvent.getY()));

            }
            waitingOn = "";
        }
    }




    public void log(String s){
        System.out.println(s);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }



    @Override
    public void nativeMousePressed(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeMouseEvent) {

    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeMouseEvent) {

    }
    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
