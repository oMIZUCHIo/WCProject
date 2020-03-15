import org.junit.Test;

/**
 * @Description
 * @Author Zhou
 * @Date 2020/3/15
 * @Version 1.0
 */
public class WCTest {

    @Test
    public void TestAll(){

        System.out.println("________TestBase________");
        TestBase();
        System.out.println("________TestEx________");
        TestEx();
        System.out.println("________TestDir________");
        TestDir();
        System.out.println("________TestMatch________");
        TestMatch();
    }

    @Test
    public void TestBase(){

        String param = "wc.exe -c -w -l D:\\tmp\\WCTest.txt";

        Main.main(param.split("\\s+"));
    }

    @Test
    public void TestEx(){

        String param = "wc.exe -c -w -l -a D:\\tmp\\WCTest.txt";

        Main.main(param.split("\\s+"));
    }

    @Test
    public void TestDir(){

        String param = "wc.exe -c -w -l -a -s D:\\tmp";

        Main.main(param.split("\\s+"));
    }

    @Test
    public void TestMatch(){

        String param = "wc.exe -c -w -l -a -s D:\\tmp\\WC*.txt";

        Main.main(param.split("\\s+"));
    }

    @Test
    public void TestFrame(){

        String param = "wc.exe -x";

        Main.main(param.split("\\s+"));
    }
}
