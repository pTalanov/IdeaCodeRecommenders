package tests;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Osipov Stanislav
 */
public class TestClass {

    private String myStringField;
    private int myIntField;

    public TestClass() {
        this.myStringField = "Default Test String";
        myIntField = 666;
    }


    public String twoLocalVariables() {
        String var1 = "Var 1";
        var1.endsWith("1");
        List<String> var2 = new ArrayList<String>();
        var2.add(var1);
        var1.concat(var1);
        var2.clear();
        return var1;
    }


    public StringBuilder oneLocalVariableMethod() {
        String s = "Test string";
        s.lastIndexOf("Simple string", 1);
        s.length();
        s.charAt(6);
        return new StringBuilder(s);
    }

}
