package networks_lab5;

public class HelloMessageTest {

	public static void main(String[] args) {
		try {
			HelloMessage hm = new HelloMessage("HeLLO;bastien;1;25;2;Bar;Foo");
			System.out.println(hm.toString());
			System.out.println("=============");
			System.out.println("getHelloMessageEncoded : " + hm.getHelloMessageAsEncodedString());
			System.out.println("=============");
			hm.addPeer("Bob");
			System.out.println("getPeers : " + hm.getPeers());
			System.out.println("After added Bob : " + hm.getHelloMessageAsEncodedString() );
			System.out.println("After added Bob : " + hm.toString());
			System.out.println("=============");
			System.out.println("=============");
			System.out.println("=============");
	    }catch(Exception e ) {
	    		System.err.println(e.getMessage());
	    }
	}
}
