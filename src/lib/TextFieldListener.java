package lib;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Виталий on 24.10.2016.
 */
public class TextFieldListener extends KeyAdapter {
    private JComponent component;

    public TextFieldListener(JComponent component){
        this.component = component;
    }
    @Override public void keyPressed(KeyEvent k){

        if(k.getKeyCode() == k.VK_ENTER){
            component.grabFocus();
        }
    }
}