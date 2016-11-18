package MyUtils.BoxLayoutUtils;

import javax.swing.*;

/**
 * Created by Виталий on 11.10.2016.
 */

public class BoxLayoutUtils {
    //метод создаёт панель с вертикальным расположением элементов
    public static JPanel createVerticalPanel(){
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        return p;
    }
    //метод создаёт панель с горизонтальным расположением элементов
    public static JPanel createHorizontalPanel(){
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        return p;
    }
    //метод придает массиву объектов одинаковое выравнивание по оси Х
    public static void setGroupAlignmentX(JComponent[] cs,
                                         float alignment){
        for(JComponent jc : cs){
            if(jc != null)
                jc.setAlignmentX(alignment);
        }
    }

    //метод придает массиву объектов одинаковое выравнивание по оси Y
    public static void setGroupAlignmentY(JComponent[] cs,
                                          float alignment){
        for(JComponent jc : cs){
            if(jc != null)
                jc.setAlignmentY(alignment);
        }
    }
}
