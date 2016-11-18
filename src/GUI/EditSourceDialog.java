package GUI;

import DataBase.Objects.PasswordsDB;
import lib.CreateEditCmd;

import javax.swing.*;

/**
 * Created by Виталий on 22.10.2016.
 */
public class EditSourceDialog extends CreateNewSourceDialog {
    private String source;

    public EditSourceDialog(JFrame parentFrame, PasswordsDB pDB, DefaultListModel sourcesDLM, String sourceName) {
        super(parentFrame, pDB, sourcesDLM);
        source = sourceName;
        nameField.setText(pDB.getLogin(sourceName));
        passwordField.setText(pDB.getPassword(sourceName));
        noteField.setText(pDB.getNote(sourceName));
    }

    @Override
    protected boolean isCreateNewDialog(){return false;}

    @Override
    protected CreateEditCmd getCommand(){return new EditSourceCmd();}

    class EditSourceCmd implements CreateEditCmd{
        public boolean execute(){
            String error = "<html> <font color=red>";

            String login = nameField.getText();
            if (login.length() == 0) error += "<html><font color=red>Логин должен содержать хотя бы один символ\n\n";
            String password = passwordField.getText();
            if (password.length() < 4) error += "<html><font color=red>Пароль должен содержать хотя бы четыре символа\n";
            String note = noteField.getText();
            if (error.equals("<html> <font color=red>")) {
                pDB.updateSource(source, login, password, note);
                return true;
            } else {
                JOptionPane.showMessageDialog(EditSourceDialog.this, error, "Ошибка!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }
}
