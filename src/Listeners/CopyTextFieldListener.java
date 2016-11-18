package Listeners;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

/**
 * Created by Виталий on 18.10.2016.
 */
public class CopyTextFieldListener implements ActionListener{
    private JFrame parentFrame;
    private JTextField copyTextField;

    public CopyTextFieldListener(JFrame parent, JTextField copyText){
        this.copyTextField = copyText;
        this.parentFrame = parent;
    }


    public void actionPerformed(ActionEvent e){
        String text = copyTextField.getText();
        StringSelection textFieldBuffered = new StringSelection(text);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(textFieldBuffered, null);
        if (!text.equals("")) {

            //Задаем время показа диалога 1 секунду
            new Thread(new Runnable(){
                public void run() {
                    try {
                        Thread.sleep(1000);
                        new Robot().keyPress(KeyEvent.VK_ENTER); // or KeyEvent.VK_ESCAPE
                    } catch(Exception e) {}
                }
            }).start();

            String message = "<html>Запись <i>" + text + "</i> скопирована в буфер.";
            JOptionPane.showMessageDialog(parentFrame, message);
        }
    }
}
