package lib;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Виталий on 24.10.2016.
 */
public class OKCancelListener extends KeyAdapter {
    private JButton button;

    public OKCancelListener(JButton button) {
        this.button = button;
    }

    @Override public void keyPressed(KeyEvent k){

        if(k.getKeyCode() == k.VK_ENTER){
            button.doClick();
        }
    }
}