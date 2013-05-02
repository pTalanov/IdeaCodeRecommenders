import java.lang.Object;
import java.lang.String;

public class ForClass {
    public void forMethod() {
        String s = "Test string";
        for (char c : s.toCharArray()) {
            s.charAt(2);
        }
        s.length();
    }
}