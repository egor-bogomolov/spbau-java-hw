import JUnit.Annotations.BeforeClass;
import JUnit.Annotations.Test;

public class TestClass {

    private int i = 0;

    @BeforeClass
    public void runBefore() {
        i = 1;
    }

    @Test(ignore = "this method should be ignored")
    public void ignoredTest() {
        System.out.println("Not okay");
    }

    @Test
    public void badTest() throws Exception {
        throw new Exception("this method should fail");
    }

    @Test
    public void normalTest() throws Exception {
        if (i == 0) {
            throw new Exception("before didn't run");
        }
    }

    @Test(expected = Exception.class)
    public void expectedExceptionTest() throws Exception {
        throw new Exception("some text");
    }
}
