package MyUtils.GUITools;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Виталий on 12.10.2016.
 */
public class GUITools {
    /*
    * метод принимает массив ссылок на кнопки JButton
    * и придаёт им нужный отступ от границ слева и справа
    */
    public static void createRocommendedMargin(JButton[] buttons){
        for(JButton jb : buttons){
            Insets margin = jb.getMargin();
            margin.left = 12;
            margin.right = 12;
            jb.setMargin(margin);
        }
    }

    /*
    * инструмент для придания группе компонентов
    * одинаковых размеров (минимальных, предопочтительных
    * и максимальных). Компоненты принимают размер
    * самого большого (по ширине) компонента в группе
    */

    public static void makeSameSize(JComponent[] components){
        //получение ширины компонентов
        int[] sizes = new int[components.length];
        for(int i = 0; i < sizes.length; i++){
            sizes[i] = components[i].getPreferredSize().width;
        }
        //определение макимального размера
        int maxSizePos = maximumElementPosition(sizes);
        Dimension maxSize = components[maxSizePos].getPreferredSize();
        //придание одинаковых размеров
        for(int i = 0; i<components.length; i++){
            components[i].setPreferredSize(maxSize);
            components[i].setMinimumSize(maxSize);
            components[i].setMaximumSize(maxSize);
        }
    }

    /*
    * помогает исправить оплошность в размерах текстового поля JTextField
    * */
    public static void fixTextFieldSize(JTextField field){
        if(field != null) {
            Dimension size = field.getPreferredSize();
            //чтобы текстовое поле по-прежнему могло увеличивать свой размер в длину
            size.width = field.getMaximumSize().width;
            //теперь текстовое поле не станет выше своей оптимальной высоты
            field.setMaximumSize(size);
        }
    }

    /*
    * вспомогательный метод для определения позиции максимального
    * элемента массива
    * */
    private static int maximumElementPosition(int[] array){
        int maxPos = 0;
        for(int i = 1; i<array.length; i++){
            if(array[i] > array[maxPos]) maxPos = i;
        }
        return maxPos;
    }
}
