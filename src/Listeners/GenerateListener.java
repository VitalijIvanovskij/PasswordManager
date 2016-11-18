package Listeners;

import MyUtils.MD5;
import lib.Configuration;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Created by Виталий on 19.10.2016.
 */
public class GenerateListener implements ActionListener {
    private JTextField field;

    public GenerateListener(JTextField f) {
        field = f;
    }

    public void actionPerformed(ActionEvent e){
         Random r = new Random();
        int rand = r.nextInt(999999);
        String result = "A";
        result += MD5.getMD5(Integer.toString(rand) + Configuration.SECRET);
        result = result.substring(0, 10);
        result += "_";
        result += Integer.toString(r.nextInt(10));
        field.setText(result);
    }
}
