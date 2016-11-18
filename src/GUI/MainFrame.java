package GUI;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Vector;

import Listeners.CopyTextFieldListener;
import MyUtils.BoxLayoutUtils.*;
import MyUtils.GUITools.*;
import DataBase.Objects.*;
import lib.Configuration;

/**
 * Created by Виталий on 16.10.2016.
 */
public class MainFrame extends JFrame {
    private DefaultListModel sourcesDLM;
    private DefaultComboBoxModel usersDLM;

    private String currentUserName, currentSourceName;

    private JList sourcesList;

    private JTextField loginField, passwordField, noteField;

    private PasswordsDB pDB;
    private UserDB userDB;

    private final String title = "Password Manager v1.0";
    private JMenuBar menuBar;

    private JButton addNewSourceButton;
    private JButton remove;
    private JButton edit;



    public MainFrame() throws HeadlessException {
        setTitle(title);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        try {
            pDB = new PasswordsDB(Configuration.NONE_USER);
            userDB = new UserDB();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setContentPane(createGUI());
        pack();
        setResizable(false);
        setLocation(200, 200);
        setVisible(true);
    }

    //этот метод будет возвращать панель созданным расположением
    private JPanel createGUI(){
        //0.Создается меню
        menuBar = new JMenuBar();

        //0.1.Меню пользователи
        JMenu usersMenu = new JMenu("Пользователи");
        usersDLM = new DefaultComboBoxModel();
        Vector<String> names = userDB.getNames();
        for (String name : names) usersDLM.addElement(name);

        JComboBox users = new JComboBox(usersDLM);
        users.setPrototypeDisplayValue("Длинный элемент");
        users.setMaximumRowCount(12);
        users.addActionListener(new UserSelectListener());

        usersMenu.addSeparator();
        JMenuItem newUser = new JMenuItem("Создать пользователя");
        newUser.addActionListener(new AddUserListener());
        usersMenu.add(newUser);
        usersMenu.addSeparator();
        JMenuItem removeUser = new JMenuItem("Удалить пользователя");
        removeUser.addActionListener(new RemoveUserListener());
        usersMenu.add(removeUser);

        menuBar.add(users);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(usersMenu);
        JMenu help = new JMenu("Справка");
        JMenuItem about = new JMenuItem("О программе");
        about.addActionListener(new AboutListener());
        help.add(about);
        menuBar.add(help);
        menuBar.add(Box.createHorizontalStrut(12));
        setJMenuBar(menuBar);

        //1.Создается панель, которая будет содержать
        //все остальные элементы и панели расположения

        JPanel main = BoxLayoutUtils.createHorizontalPanel();

        //чтобы интерфейс отвечал требованиям Java
        //необходимо отделить его содержимое от границ окна на 12 пикселов.
        //для этого используем пустую рамку

        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel loginPasswordPanel = BoxLayoutUtils.createVerticalPanel();
        loginPasswordPanel.setBorder(BorderFactory.createCompoundBorder(new EtchedBorder(), new EmptyBorder(12, 12, 12, 12)));

        //1.1Создается панель содержащая список ресурсов

        JPanel sourcePanel = BoxLayoutUtils.createVerticalPanel();
        sourcesDLM = new DefaultListModel();
        getSources();
        sourcesList = new JList(sourcesDLM);
        sourcesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourcesList.addMouseListener(new SourceListListener());
        sourcePanel.add(new JScrollPane(sourcesList));
        sourcePanel.setPreferredSize(new Dimension(150, 150));

        //загружаем пользователя по дефолту

        loadUser(Configuration.DEFAULT_NUM_USER);

        //1.2.Создается кнопка добавления ресурсов

        addNewSourceButton = new JButton("Создать ресурс");
        if (pDB.getUserID().equals(Configuration.NONE_USER)){
            addNewSourceButton.setEnabled(false);
        }
        addNewSourceButton.grabFocus();
        addNewSourceButton.addActionListener(new CreateNewSourceListener());
        sourcePanel.add(Box.createVerticalStrut(12));
        sourcePanel.add(addNewSourceButton);



        //2. Поочерёдно создаются "полосы", на которые был разбит интерфейс на этапе анализа
        //а) первое текстовое поле и надпись к нему
        JPanel login = BoxLayoutUtils.createHorizontalPanel();
        JLabel loginLabel = new JLabel("Логин:");
        login.add(loginLabel);
        login.add(Box.createHorizontalStrut(12));
        loginField = new JTextField(15);
        login.add(loginField);
        login.add(Box.createHorizontalStrut(12));
        JButton copyLogin = new JButton("Копировать");
        copyLogin.addActionListener(new CopyTextFieldListener(this, loginField));
        login.add(copyLogin);

        //б)второе текстовое поле и надпись к нему
        JPanel password = BoxLayoutUtils.createHorizontalPanel();
        JLabel passwordLabel = new JLabel("Пароль:");
        password.add(passwordLabel);
        password.add(Box.createHorizontalStrut(12));
        passwordField = new JTextField(15);
        password.add(passwordField);
        password.add(Box.createHorizontalStrut(12));
        JButton copyPasswordButton = new JButton("Копировать");
        copyPasswordButton.addActionListener(new CopyTextFieldListener(this, passwordField));
        password.add(copyPasswordButton);

        //в)заметка
        JPanel note = BoxLayoutUtils.createHorizontalPanel();
        JLabel noteLabel = new JLabel("Заметка:");
        note.add(noteLabel);
        note.add(Box.createHorizontalStrut(12));
        noteField = new JTextField(15);
        note.add(noteField);
        JButton copyNote = new JButton("Копировать");
        copyNote.addActionListener(new CopyTextFieldListener(this, noteField));
        note.add(Box.createHorizontalStrut(12));
        note.add(copyNote);


        //г) ряд кнопок

        JPanel editButtons = BoxLayoutUtils.createHorizontalPanel();

        //кнопка удалить
        remove = new JButton("Удалить ресурс");
        remove.setEnabled(false);
        remove.addActionListener(new RemoveSourceListener());

        //кнопка редактировать
        edit = new JButton("Редактировать");
        edit.setEnabled(false);
        edit.addActionListener(new EditSourceListener());

        editButtons.add(remove);
        editButtons.add(Box.createHorizontalGlue());
        editButtons.add(edit);
        editButtons.setAlignmentX(editButtons.RIGHT_ALIGNMENT);

        //3.Проводятся необходимые действия по выравниванию компонентов,
        //уточнению их размеров, приданию одинаковых размеров
        //а)согласованное выравнивание вложенных панелей
        BoxLayoutUtils.setGroupAlignmentX(new JComponent[]{login, password, note, main, editButtons},
                Component.LEFT_ALIGNMENT);
        //б)центральное выравнивание надписей и текстовых полей
        BoxLayoutUtils.setGroupAlignmentY(new JComponent[]{loginField, passwordField, noteField, loginLabel, passwordLabel},
                Component.CENTER_ALIGNMENT);
        //в)одинаковые размеры надписей к текстовым полям
        GUITools.makeSameSize(new JComponent[]{loginLabel, passwordLabel, noteLabel});
        //г)стандартный вид для кнопок
        GUITools.createRocommendedMargin(new JButton[]{edit});
        //д)устранение "бесконечной" высоты текстовых полей
        GUITools.fixTextFieldSize(loginField);
        GUITools.fixTextFieldSize(passwordField);
        GUITools.fixTextFieldSize(noteField);

        //4.Сбор панели логина и пароля
        loginPasswordPanel.add(login);
        loginPasswordPanel.add(Box.createVerticalStrut(12));
        loginPasswordPanel.add(password);
        loginPasswordPanel.add(Box.createVerticalStrut(12));
        loginPasswordPanel.add(note);
        loginPasswordPanel.add(Box.createVerticalStrut(17));
        loginPasswordPanel.add(editButtons);



        //5.Окончательный сбор панели
        main.add(sourcePanel);
        main.add(Box.createHorizontalStrut(12));
        main.add(loginPasswordPanel);


        return main;
    }

    private void getSources(){
        sourcesDLM.removeAllElements();
        Vector<String> vectorSources = pDB.getSources();
        Collections.sort(vectorSources);
        for (String vectorSource : vectorSources)
            sourcesDLM.addElement(vectorSource);
    }

    private void loadUser(int userNumInList){

        if(usersDLM.getSize() > 0 && userNumInList >= 0 && userNumInList < usersDLM.getSize()) {
            currentUserName = (String) usersDLM.getElementAt(userNumInList);
            pDB.setUser(currentUserName);
            getSources();
            if (sourcesDLM.size() > 0) {
                currentSourceName = (String) sourcesDLM.get(0);
            }
        }
    }

    private void setTextFields(){
        if(sourcesDLM.size() > 0) {
            loginField.setText(pDB.getLogin(currentSourceName));
            passwordField.setText(pDB.getPassword(currentSourceName));
            noteField.setText(pDB.getNote(currentSourceName));
        }
        else{
            loginField.setText("");
            passwordField.setText("");
            noteField.setText("");
        }
    }

//----------------------------LISTENERS---------------------------------------------------

    class RemoveSourceListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if (currentSourceName != null) {
                int res = JOptionPane.showConfirmDialog(MainFrame.this, "Удалить ресурс "
                                + currentSourceName + " ?", "Удаление ресурса",
                        JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {
                    pDB.deleteSource(currentSourceName);
                    sourcesDLM.removeElement(currentSourceName);
                    if (sourcesDLM.size() > 0) {
                        currentSourceName = (String)sourcesDLM.get(0);
                    }
                    if(sourcesDLM.size() <= 0) remove.setEnabled(false);
                }
            }
        }
    }

    class SourceListListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 1 && sourcesDLM.size() > 0) {
                if(!remove.isEnabled()) remove.setEnabled(true);
                if(!edit.isEnabled()) edit.setEnabled(true);
                int selected = ((JList) e.getSource()).getSelectedIndex();
                currentSourceName = (String)sourcesDLM.elementAt(selected);
                setTextFields();
                validate();
            }
            else if (sourcesDLM.size() == 0){
                remove.setEnabled(false);
                edit.setEnabled(false);
            }
        }
    }

    class UserSelectListener implements ActionListener{

            public void  actionPerformed(ActionEvent e) {
                currentUserName = (String)usersDLM.getSelectedItem();
                int selected = usersDLM.getIndexOf(currentUserName);
                loadUser(selected);
                if(sourcesDLM.size() == 0) {
                    remove.setEnabled(false);
                    edit.setEnabled(false);

                }
                else {
                    remove.setEnabled(true);
                    edit.setEnabled(true);
                }

                setTextFields();
                addNewSourceButton.setEnabled(true);
                sourcesList.grabFocus();
            }
        }

    class AddUserListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            new AddUserDialog(MainFrame.this, userDB, usersDLM);
            if(!remove.isEnabled() && sourcesDLM.size() > 0){
                remove.setEnabled(true);
                edit.setEnabled(true);
            }
        }
    }


    class RemoveUserListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            if (currentUserName != null) {
                int res = JOptionPane.showConfirmDialog(MainFrame.this, "Удалить пользователя " + currentUserName + " ?", "Удаление пользователя", JOptionPane.YES_NO_OPTION);
                if (res == JOptionPane.YES_OPTION) {

                   //удаление ресурсов пользователя
                    for (int i=0; i < sourcesDLM.size(); i++ ) {
                        pDB.deleteSource((String)sourcesDLM.getElementAt(i));
                    }

                    userDB.deleteAccount(currentUserName);
                    usersDLM.removeElement(currentUserName);
                    usersDLM.setSelectedItem(usersDLM.getElementAt(Configuration.DEFAULT_NUM_USER));
                    loadUser(Configuration.DEFAULT_NUM_USER);
                    JOptionPane.showMessageDialog(MainFrame.this, "Запись удалена!");
                }
            }
        }
    }

    class CreateNewSourceListener implements ActionListener {

        public void actionPerformed(ActionEvent e){

            if(!pDB.getUserID().equals("0")) {
                CreateNewSourceDialog dialog = new CreateNewSourceDialog(MainFrame.this, pDB, sourcesDLM);
                dialog.setVisible(true);
            }

            if(!remove.isEnabled() && sourcesDLM.size() > 0){
                remove.setEnabled(true);
                edit.setEnabled(true);
            }
        }
    }

    class EditSourceListener implements ActionListener{
        public void actionPerformed(ActionEvent e){

            if(!pDB.getUserID().equals(Configuration.NONE_USER)) {
                EditSourceDialog dialog =  new EditSourceDialog(MainFrame.this, pDB, sourcesDLM, currentSourceName);
                dialog.setTitle("Редактировать ресурс");
                dialog.setVisible(true);
            }

            setTextFields();
            if(!remove.isEnabled()){
                remove.setEnabled(true);
                edit.setEnabled(true);
            }
        }

    }

    class AboutListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            JOptionPane.showMessageDialog(MainFrame.this, title + "\n\n" +
                    "Автор: Виталий Ивановский\n" +
                    "ivanovskij.vitalij@yandex.ua");
        }
    }

//----------------------------------------------------------------------------------------


    public static void main(String[] args) {

        //Создание файла базы данных
        Path DBPath = Paths.get(Configuration.DB_PATH);

        if(!Files.exists(DBPath)){
            try {
                Files.createDirectory(Paths.get("db"));
            } catch (IOException e) {
                System.out.println("Ошибка создания файла!");
                e.printStackTrace();
            }
        }

        if(!Files.exists(Paths.get("db/Readme.txt"))) {
            try {
                FileOutputStream readmeFile = new FileOutputStream("db/Readme.txt");
                String readmeText = "Файл PassManager.db содержит список всех сохранённых ресурсов," +
                        " при удалении будут потеряны все данные!!! \n\n" +
                        "С отзывами и пожеланиями пишите: ivanovskij.vitalij@yandex.ua";
                byte buf[] = readmeText.getBytes();
                readmeFile.write(buf);
                readmeFile.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        new MainFrame();
    }
}
