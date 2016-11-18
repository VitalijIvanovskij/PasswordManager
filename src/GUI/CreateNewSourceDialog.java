package GUI;

import DataBase.Objects.PasswordsDB;
import Listeners.*;
import MyUtils.BoxLayoutUtils.BoxLayoutUtils;
import MyUtils.GUITools.GUITools;
import lib.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Виталий on 19.10.2016.
 */
public class CreateNewSourceDialog extends JDialog {
    protected PasswordsDB pDB;
    protected DefaultListModel sourcesDLM;

    protected JTextField nameField;
    protected JTextField passwordField;
    protected JTextField noteField;
    protected JTextField sourceField;

    protected CreateEditCmd cmd;

    public JButton ok;
    public JButton cancel;


    public CreateNewSourceDialog(JFrame parentFrame, PasswordsDB pDB, DefaultListModel sourcesDLM) {


        super(parentFrame, "Создать ресурс", true);
        this.sourcesDLM = sourcesDLM;
        this.pDB = pDB;
        cmd = getCommand();

        //добавляем расположение в центр окна
        getContentPane().add(createGUI());
        //задаем предпочтительный размер
        pack();
        //располагаем по центру родительского окна
        Point parentLeftConer = parentFrame.getLocation();
        double dialogFrameLocationX = parentLeftConer.getX();
        double dialogFrameLocationY = parentLeftConer.getY();
        setLocation((int) dialogFrameLocationX + 100, (int) dialogFrameLocationY + 20);

        setResizable(false);

    }


    //этот метод будет возвращать панель созданным расположением
    private JPanel createGUI() {
        //1.Создается панель, которая будет содержать
        //все остальные элементы и панели расположения

        JPanel main = BoxLayoutUtils.createVerticalPanel();

        //чтобы интерфейс отвечал требованиям Java
        //необходимо отделить его содержимое от границ окна на 12 пикселов.
        //для этого r(используем пустую рамку

        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        //2. Поочерёдно создаются "полосы", на которые был разбит интерфейс на этапе анализа
        //2.1 ресурс
        JPanel source = BoxLayoutUtils.createHorizontalPanel();
        JLabel sourceLabel = new JLabel("Ресурс:");
        source.add(sourceLabel);
        source.add(Box.createHorizontalStrut(12));
        sourceField = new JTextField(15);

        source.add(sourceField);
        source.add(Box.createHorizontalStrut(128));

        //а) логин
        JPanel name = BoxLayoutUtils.createHorizontalPanel();
        JLabel nameLabel = new JLabel("Логин:");
        name.add(nameLabel);
        name.add(Box.createHorizontalStrut(12));
        nameField = new JTextField(15);

        name.add(nameField);
        name.add(Box.createHorizontalStrut(12));
        JButton generateLogin = new JButton("Генерировать");
        generateLogin.addActionListener(new GenerateListener(nameField));
        name.add(generateLogin);

        //б)пароль
        JPanel password = BoxLayoutUtils.createHorizontalPanel();
        JLabel passwordLabel = new JLabel("Пароль:");
        password.add(passwordLabel);
        password.add(Box.createHorizontalStrut(12));
        passwordField = new JTextField(15);

        password.add(passwordField);
        password.add(Box.createHorizontalStrut(12));
        JButton generatePassword = new JButton("Генерировать");
        generatePassword.addActionListener(new GenerateListener(passwordField));
        password.add(generatePassword);

        //в)заметка
        JPanel note = BoxLayoutUtils.createHorizontalPanel();
        JLabel noteLabel = new JLabel("Заметка:");
        note.add(noteLabel);
        note.add(Box.createHorizontalStrut(12));
        noteField = new JTextField(15);

        note.add(noteField);
        note.add(Box.createHorizontalStrut(128));

        GUITools.makeSameSize(new JComponent[]{sourceLabel, nameLabel, passwordLabel, noteLabel});


        //г) ряд кнопок
        JPanel buttons = BoxLayoutUtils.createHorizontalPanel();
        ok = new JButton("OK");
        ok.addActionListener(new CreateNewSourceDialogOKButton());
        ok.addKeyListener(new OKCancelListener(ok));
        cancel = new JButton("Cancel");
        cancel.addKeyListener(new OKCancelListener(cancel));
        cancel.addActionListener(new ActionListener(){
                                    public void actionPerformed(ActionEvent e){
                                        dispose();
                                    }
                                });
        GUITools.makeSameSize(new JComponent[]{ok, cancel});
        buttons.add(Box.createHorizontalGlue());
        buttons.add(ok);
        buttons.add(Box.createHorizontalStrut(5));
        buttons.add(cancel);
        buttons.add(Box.createHorizontalGlue());


        //3.Проводятся необходимые действия по выравниванию компонентов,
        //уточнению их размеров, приданию одинаковых размеров
        //а)согласованное выравнивание вложенных панелей
        BoxLayoutUtils.setGroupAlignmentX(new JComponent[]{source, name, password, note, main, buttons}, Component.LEFT_ALIGNMENT);
        //б)центральное выравнивание надписей и текстовых полей
        BoxLayoutUtils.setGroupAlignmentY(new JComponent[]{sourceField, nameField, passwordField, noteField, nameLabel, passwordLabel}, Component.CENTER_ALIGNMENT);
        //в)одинаковые размеры надписей к текстовым полям
        GUITools.makeSameSize(new JComponent[]{nameLabel, passwordLabel});
        //г)стандартный вид для кнопок
        GUITools.createRocommendedMargin(new JButton[]{ok, cancel});
        //д)устранение "бесконечной" высоты текстовых полей
        GUITools.fixTextFieldSize(sourceField);
        GUITools.fixTextFieldSize(nameField);
        GUITools.fixTextFieldSize(passwordField);
        GUITools.fixTextFieldSize(noteField);

        //4.Окончательный "сбор" полос в интерфейс
        if (isCreateNewDialog()) {
            main.add(source);
            main.add(Box.createVerticalStrut(12));
        }
        main.add(name);
        main.add(Box.createVerticalStrut(12));
        main.add(password);
        main.add(Box.createVerticalStrut(12));
        main.add(note);
        main.add(Box.createVerticalStrut(17));
        main.add(buttons);

        //установить слушателей на текстовые поля
        setGrabFocus();

        return main;
    }

    public JTextField getNameField(){return nameField;}
    public JTextField getPasswordField(){return passwordField;}
    public JTextField getNoteField(){return noteField;}
    public JButton getOKbutton(){return ok;}

    protected boolean isCreateNewDialog(){return true;}

    protected CreateEditCmd getCommand(){return new CreateNewSourceCmd();}

    protected void setGrabFocus(){
        sourceField.addKeyListener(new TextFieldListener(getNameField()));
        nameField.addKeyListener(new TextFieldListener(getPasswordField()));
        passwordField.addKeyListener(new TextFieldListener(getNoteField()));
        noteField.addKeyListener(new TextFieldListener(getOKbutton()));
    }

//-----------------------------LISTENERS--------------------------------------------------
    class CreateNewSourceDialogOKButton implements ActionListener {

    public void actionPerformed(ActionEvent e) {

            boolean success = cmd.execute();

            if(success)
                CreateNewSourceDialog.this.dispose();
        }
    }



//-------------------------COMMAND---------------------------------------------------------------


    class CreateNewSourceCmd implements CreateEditCmd {
        public boolean execute() {
            String error = "<html> <font color=red>";
            String source = sourceField.getText();
            if (source.length() == 0) error += "Имя ресурса должно содержать хотя бы один символ\n\n";
            String login = nameField.getText();
            if (login.length() == 0) error += "<html><font color=red>Логин должен содержать хотя бы один символ\n\n";
            String password = passwordField.getText();
            if (password.length() < 4) error += "<html><font color=red>Пароль должен содержать хотя бы четыре символа\n";
            String note = noteField.getText();
            if (error.equals("<html> <font color=red>")) {
                pDB.createNewSource(source, login, password, note);
                sourcesDLM.add(0, sourceField.getText());
                return true;
            } else {
                JOptionPane.showMessageDialog(CreateNewSourceDialog.this, error, "Ошибка!", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
    }

}