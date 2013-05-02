package tests;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Osipov Stanislav
 */
public class IfOnLocalVariableClass {

    public StringBuilder ifMethod() {
        String s = "Test string";
        if(s.length() < 3) {
            s.lastIndexOf("Simple string", 1);
        } else {
            s.charAt(3);
        }
        s.getBytes();
        return new StringBuilder(s);
    }

}