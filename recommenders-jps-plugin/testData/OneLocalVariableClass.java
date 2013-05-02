package tests;

import java.lang.String;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Osipov Stanislav
 */
public class OneLocalVariableClass {

    public StringBuilder oneLocalVariableMethod() {
        String s = "Test string";
        s.lastIndexOf("Simple string", 1);
        s.length();
        s.charAt(6);
        return new StringBuilder(s);
    }

}
