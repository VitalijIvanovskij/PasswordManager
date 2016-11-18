package MyUtils;

import java.util.Vector;

/**
 * Created by Виталий on 04.10.2016.
 */
public class AssocItem{
    private String value;
    private String key;


    public AssocItem(){}

    public AssocItem(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    public String getKey() {
        return key;
    }

    public AssocItem getValueByKey(String key) {
        if(key.equals(this.getKey())) return this;
        else return null;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public void setKey(String key) {
        this.key = key;
    }

}