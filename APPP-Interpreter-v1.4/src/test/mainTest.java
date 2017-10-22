package test;
import main.Main;
import org.junit.Test;


public class mainTest {
	@Test
	public void booleanTest() {
		    assert(Main.isBool("tt") == true);
		    assert(Main.isBool("ff") == true);
		    assert(Main.isBool("tftf") == false);
		    assert(Main.isBool("kidfjfftt") == false);
		    assert(Main.isBool("knisl") == false);
	}
	
	@Test
	public void integerTest() {
	    assert(Main.isInt("052") == true);
	    assert(Main.isInt("-052") == true);
	    assert(Main.isInt("52328349") == true);
	    assert(Main.isInt("999999999") == true);
	    assert(Main.isInt("2147483647") == true);
	    assert(Main.isInt("-2147483648") == true);
	    assert(Main.isInt("2147483648") == false);
	    assert(Main.isInt("-hrd7483649") == false);
	    assert(Main.isInt("a052") == false);
	  }
}
