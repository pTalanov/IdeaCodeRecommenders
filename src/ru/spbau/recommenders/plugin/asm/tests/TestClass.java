package ru.spbau.recommenders.plugin.asm.tests;

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


    public StringBuilder oneLocalVariableMethod() {
        String s = "Test string";
        s.lastIndexOf("Simple string", 1);
        s.length();
        s.charAt(6);
        return new StringBuilder(s);
    }

}
