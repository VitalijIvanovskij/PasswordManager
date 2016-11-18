package GUI;

import DataBase.Objects.UserDB;
import MyUtils.BoxLayoutUtils.BoxLayoutUtils;
import MyUtils.GUITools.GUITools;
import lib.Configuration;
import lib.OKCancelListener;
import lib.TextFieldListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Виталий on 24.10.2016.
 */
public class AddUserDialog extends JDialog {
    private UserDB uDB;
    private DefaultComboBoxModel usersDLM;

    //открытые ссылки на компоненты для присоединения слушателей событий
    public JTextField nameField, passwordField, repeatField;
    public JButton ok, cancel;

    public AddUserDialog(JFrame parentFrame, UserDB uDB, DefaultComboBoxModel usersDLM) {
        super(parentFrame, "Добавить пользователя", true);
        this.uDB = uDB;
        this.usersDLM = usersDLM;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //добавляем расположение в центр окна
        getContentPane().add(createGUI());
        //задаем предпочтительный размер
        pack();
        //располагаем по центру родительского окна
        Point parentLeftConer = parentFrame.getLocation();
        double dialogFrameLocationX = parentLeftConer.getX();
        double dialogFrameLocationY = parentLeftConer.getY();
        setLocation((int) dialogFrameLocationX + 140, (int) dialogFrameLocationY + 40);

        setResizable(false);
        setVisible(true);
    }

    //этот метод будет возвращать панель созданным расположением
    private JPanel createGUI(){
        //1.Создается панель, которая будет содержать
        //все остальные элементы и панели расположения

        JPanel main = BoxLayoutUtils.createVerticalPanel();

        //чтобы интерфейс отвечал требованиям Java
        //необходимо отделить его содержимое от границ окна на 12 пикселов.
        //для этого r(используем пустую рамку

        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        //2. Поочерёдно создаются "полосы", на которые был разбит интерфейс на этапе анализа
        //а) первое текстовое поле и надпись к нему
        JPanel name = BoxLayoutUtils.createHorizontalPanel();
        JLabel nameLabel = new JLabel("Имя:");
        name.add(nameLabel);
        name.add(Box.createHorizontalStrut(12));
        nameField = new JTextField(15);
        name.add(nameField);

        //б)поле для ввода пароля
        JPanel password = BoxLayoutUtils.createHorizontalPanel();
        JLabel passwordLabel = new JLabel("Пароль:");
        password.add(passwordLabel);
        password.add(Box.createHorizontalStrut(12));
        passwordField = new JTextField(15);

        password.add(passwordField);

        //в)поле для повтора пароля
        JPanel repeat = BoxLayoutUtils.createHorizontalPanel();
        JLabel repeatLabel = new JLabel("Повтор:");
        repeat.add(repeatLabel);
        repeat.add(Box.createHorizontalStrut(12));
        repeatField = new JTextField(15);

        repeat.add(repeatField);


        //Панель кнопок
        JPanel buttons = BoxLayoutUtils.createHorizontalPanel();
        ok = new JButton("OK");
        ok.addActionListener(new AddUserListener());
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
        buttons.setAlignmentX(buttons.RIGHT_ALIGNMENT);

        //3.Проводятся необходимые действия по выравниванию компонентов,
        //уточнению их размеров, приданию одинаковых размеров
        //а)согласованное выравнивание вложенных панелей
        BoxLayoutUtils.setGroupAlignmentX(new JComponent[]{name, password, repeat, main, buttons}, Component.LEFT_ALIGNMENT);
        //б)центральное выравнивание надписей и текстовых полей
        BoxLayoutUtils.setGroupAlignmentY(new JComponent[]{nameField, passwordField, repeatField, nameLabel, passwordLabel, repeatLabel}, Component.CENTER_ALIGNMENT);
        //в)одинаковые размеры надписей к текстовым полям
        GUITools.makeSameSize(new JComponent[]{nameLabel, passwordLabel, repeatLabel});
        //г)стандартный вид для кнопок
        GUITools.createRocommendedMargin(new JButton[]{ok, cancel});
        //д)устранение "бесконечной" высоты текстовых полей
        GUITools.fixTextFieldSize(nameField);
        GUITools.fixTextFieldSize(passwordField);

        //4.Окончательный "сбор" полос в интерфейс
        main.add(name);
        main.add(Box.createVerticalStrut(12));
        main.add(password);
        main.add(Box.createVerticalStrut(12));
        main.add(repeat);
        main.add(Box.createVerticalStrut(17));
        main.add(buttons);

        setGrabFocus();
        return main;
    }

    public void setGrabFocus(){
        nameField.addKeyListener(new TextFieldListener(getPasswordField()));
        passwordField.addKeyListener(new TextFieldListener(getRepeatField()));
        repeatField.addKeyListener(new TextFieldListener(getOKbutton()));
    }

    public JTextField getPasswordField(){return passwordField;}
    public JTextField getRepeatField(){return repeatField;}
    public JButton getOKbutton(){return ok;}

    //-----------------------------LISTENERS----------------------------------------------
    class AddUserListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String error = "<html> <font color=red>";
            String name = nameField.getText();
            if (name.length() == 0) error += "Имя должно содержать хотя бы один символ\n\n";
            String password = passwordField.getText();
            String repeat = repeatField.getText();

            if(!error.equals("<html> <font color=red>")){
                JOptionPane.showMessageDialog(AddUserDialog.this, error, "Ошибка!", JOptionPane.ERROR_MESSAGE);
                nameField.grabFocus();
            }
            else if(password.equals(repeat)){
                Integer success = uDB.createAccount(name, password);
                if(success == Configuration.USERNAME_EXISTS){
                    JOptionPane.showMessageDialog(AddUserDialog.this, "Пользователь с таким именем уже существует", "Ошибка!", JOptionPane.ERROR_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(AddUserDialog.this, "Готово!");
                    AddUserDialog.this.dispose();
                    usersDLM.addElement(name);
                }
            }
            else{
                JOptionPane.showMessageDialog(AddUserDialog.this, "Пароли не совпадают!","Ошибка!" , JOptionPane.ERROR_MESSAGE);

            }
        }
    }
}
