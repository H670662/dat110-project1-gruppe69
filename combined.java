
// Start of ./src/test/java/no/hvl/dat110/system/tests/TestSystem.java
package no.hvl.dat110.system.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import no.hvl.dat110.system.controller.Controller;
import no.hvl.dat110.system.display.DisplayDevice;
import no.hvl.dat110.system.sensor.SensorDevice;
import java.util.concurrent.atomic.AtomicBoolean;

class TestSystem {

	@Test
	void test() {

		System.out.println("System starting ...");

		AtomicBoolean failure = new AtomicBoolean(false);
		
		Thread displaythread = new Thread() {

			public void run() {
				
				try {
					DisplayDevice.main(null);
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				}
			}
			
		};
		
		Thread sensorthread = new Thread() {
			
			public void run() {
				
				try {
				SensorDevice.main(null);
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				}
			}
			
		};
		
		
		Thread controllerthread = new Thread() {
			
			public void run() {
				
				try {
				Controller.main(null);
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				}
			}
			
		};

		try {
			
			displaythread.start();
			sensorthread.start();
		
			// let the servers start first
			Thread.sleep(2000);
			
			controllerthread.start();
			
			displaythread.join();
			sensorthread.join();
			controllerthread.join();

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			System.out.println("System stopping ...");
			
			if (failure.get()) {
				fail();
			}
		}
		
		// we check only termination here
		assertTrue(true);
			
	
	}

}

// Start of ./src/test/java/no/hvl/dat110/rpc/tests/TestBooleanBooleanStub.java
package no.hvl.dat110.rpc.tests;

import no.hvl.dat110.rpc.RPCClient;
import no.hvl.dat110.rpc.RPCLocalStub;
import no.hvl.dat110.rpc.RPCUtils;

public class TestBooleanBooleanStub extends RPCLocalStub {
	
	public TestBooleanBooleanStub(RPCClient rpcclient) {
		super(rpcclient);
	}
	
	public boolean m(boolean b) {
		
		byte[] request = RPCUtils.marshallBoolean(b);
		
		byte[] reply = rpcclient.call((byte)4,request);
		
		boolean bres = RPCUtils.unmarshallBoolean(reply);
		
		return bres;
	}
	
}

// Start of ./src/test/java/no/hvl/dat110/rpc/tests/TestStringStringImpl.java
package no.hvl.dat110.rpc.tests;

import no.hvl.dat110.rpc.RPCRemoteImpl;
import no.hvl.dat110.rpc.RPCUtils;
import no.hvl.dat110.rpc.RPCServer;

public class TestStringStringImpl extends RPCRemoteImpl {

	public TestStringStringImpl(byte rpcid, RPCServer rpcserver) {
		super(rpcid, rpcserver);
	}
	
	public byte[] invoke(byte[] request) {
	
		String str = RPCUtils.unmarshallString(request);
		
		String resstr = m(str);
		
		byte[] reply = RPCUtils.marshallString(resstr);
		
		return reply;
	}
	
	public String m(String str) {
		System.out.println("String m("+str+") executed");
		return str+str;
	}
}

// Start of ./src/test/java/no/hvl/dat110/rpc/tests/TestVoidVoidImpl.java
package no.hvl.dat110.rpc.tests;

import no.hvl.dat110.rpc.RPCRemoteImpl;
import no.hvl.dat110.rpc.RPCUtils;
import no.hvl.dat110.rpc.RPCServer;

public class TestVoidVoidImpl extends RPCRemoteImpl {

	public TestVoidVoidImpl(byte rpcid, RPCServer rpcserver) {
		super(rpcid,rpcserver);
	}
	
	public void m() {
		System.out.println("void m() executed");
	}
	
	public byte[] invoke(byte[] request) {
		
		RPCUtils.unmarshallVoid(request);
		
		m();
		
		byte[] reply = RPCUtils.marshallVoid();
		
		return reply;
	}
}

// Start of ./src/test/java/no/hvl/dat110/rpc/tests/TestIntIntStub.java
package no.hvl.dat110.rpc.tests;

import no.hvl.dat110.rpc.RPCClient;
import no.hvl.dat110.rpc.RPCLocalStub;
import no.hvl.dat110.rpc.RPCUtils;

public class TestIntIntStub extends RPCLocalStub {
	
	public TestIntIntStub(RPCClient rpcclient) {
		super(rpcclient);
	}
	
	public int m(int x) {
				
		byte[] request = RPCUtils.marshallInteger(x);
		
		byte[] reply = rpcclient.call((byte)3,request);
		
		int xres = RPCUtils.unmarshallInteger(reply);
		
		return xres;
	}
}

// Start of ./src/test/java/no/hvl/dat110/rpc/tests/TestRPC.java
package no.hvl.dat110.rpc.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import no.hvl.dat110.rpc.RPCClient;
import no.hvl.dat110.rpc.RPCServer;
import no.hvl.dat110.rpc.RPCClientStopStub;

import java.util.concurrent.atomic.AtomicBoolean;

@TestMethodOrder(OrderAnnotation.class)
public class TestRPC {

	private static int PORT = 8080;
	private static String SERVER = "localhost";

	@Test
	@Order(1)
	public void testStartStop() {

		AtomicBoolean failure = new AtomicBoolean(false);

		RPCClient client = new RPCClient(SERVER, PORT);
		RPCServer server = new RPCServer(PORT);

		Thread serverthread = new Thread() {

			public void run() {

				try {
					server.run();
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);

				} finally {
					server.stop();
				}
			}
		};

		Thread clientthread = new Thread() {

			public void run() {

				try {
					client.connect();

					RPCClientStopStub stub = new RPCClientStopStub(client);

					stub.stop();

					client.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				}
			}
		};

		System.out.println("System starting ... ");

		try {

			serverthread.start();
			clientthread.start();

			serverthread.join();
			clientthread.join();

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {

			System.out.println("System stopping ... ");
			
			if (failure.get()) {
				fail();
			}
		}

	}

	@Test
	@Order(2)
	public void testVoidCall() {

		RPCClient client = new RPCClient(SERVER, PORT);
		RPCServer server = new RPCServer(PORT);

		AtomicBoolean failure = new AtomicBoolean(false);
		
		Thread serverthread = new Thread() {

			public void run() {

				try {
					TestVoidVoidImpl voidvoidimpl = new TestVoidVoidImpl((byte) 1, server);

					server.run();
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				} finally {
					server.stop();
				}
			}
		};

		Thread clientthread = new Thread() {

			public void run() {

				try {
					client.connect();

					RPCClientStopStub stopstub = new RPCClientStopStub(client);
					TestVoidVoidStub voidvoidstub = new TestVoidVoidStub(client);

					// void test case
					voidvoidstub.m();

					assertTrue(true); // just check that we complete call
					stopstub.stop();

					client.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				}

			}
		};

		System.out.println("System starting ... ");

		try {

			serverthread.start();
			clientthread.start();

			serverthread.join();
			clientthread.join();

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			
			System.out.println("System stopping ... ");

			if (failure.get()) {
				fail();
			}
		}
	}

	@Test
	@Order(3)
	public void testStringCall() {

		RPCClient client = new RPCClient(SERVER, PORT);
		RPCServer server = new RPCServer(PORT);

		AtomicBoolean failure = new AtomicBoolean(false);
		
		Thread serverthread = new Thread() {

			public void run() {

				try {

					TestStringStringImpl stringstringimpl = new TestStringStringImpl((byte) 2, server);

					server.run();
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				} finally {
					server.stop();
				}
			}
		};

		Thread clientthread = new Thread() {

			public void run() {

				try {
					client.connect();

					RPCClientStopStub stopstub = new RPCClientStopStub(client);
					TestStringStringStub stringstringstub = new TestStringStringStub(client);

					// string test case
					String teststr = "string";
					String resstr = stringstringstub.m(teststr);

					assertEquals(teststr + teststr, resstr);

					stopstub.stop();

					client.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				}

			}
		};

		System.out.println("System starting ... ");

		try {

			serverthread.start();
			clientthread.start();

			serverthread.join();
			clientthread.join();

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {

			System.out.println("System stopping ... ");
			
			if (failure.get()) {
				fail();
			}
		}
	}

	@Test
	@Order(4)
	public void testIntCall() {

		RPCClient client = new RPCClient(SERVER, PORT);
		RPCServer server = new RPCServer(PORT);

		AtomicBoolean failure = new AtomicBoolean(false);
		
		Thread serverthread = new Thread() {

			public void run() {

				try {
					TestIntIntImpl intintimpl = new TestIntIntImpl((byte) 3, server);

					server.run();
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				} finally {
					server.stop();
				}
			}
		};

		Thread clientthread = new Thread() {

			public void run() {

				try {
					client.connect();

					RPCClientStopStub stopstub = new RPCClientStopStub(client);
					TestIntIntStub intintstub = new TestIntIntStub(client);

					// int test case
					int x = 42;
					int resx = intintstub.m(x);

					assertEquals(x, resx);

					stopstub.stop();

					client.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				}

			}
		};

		System.out.println("System starting ... ");

		try {

			serverthread.start();
			clientthread.start();

			serverthread.join();
			clientthread.join();

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {

			System.out.println("System stopping ... ");
			
			if (failure.get()) {
				fail();
			}
		}
	}

	@Test
	@Order(5)
	public void testBoolCall() {

		RPCClient client = new RPCClient(SERVER, PORT);
		RPCServer server = new RPCServer(PORT);

		AtomicBoolean failure = new AtomicBoolean(false);
		
		Thread serverthread = new Thread() {

			public void run() {

				try {

					TestBooleanBooleanImpl boolboolimpl = new TestBooleanBooleanImpl((byte) 4, server);

					server.run();

				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				} finally {
					server.stop();
				}
			}
		};

		Thread clientthread = new Thread() {

			public void run() {

				RPCClientStopStub stopstub = null;

				try {
					client.connect();

					stopstub = new RPCClientStopStub(client);
					TestBooleanBooleanStub boolboolstub = new TestBooleanBooleanStub(client);

					// boolean test case

					boolean testb = true;
					boolean resb = boolboolstub.m(testb);

					assertEquals(!testb, resb);

					testb = false;
					resb = boolboolstub.m(testb);
					assertEquals(!testb, resb);

					stopstub.stop();

					client.disconnect();

				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				}
			}
		};

		System.out.println("System starting ... ");

		try {

			serverthread.start();
			clientthread.start();

			serverthread.join();
			clientthread.join();

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {

			System.out.println("System stopping ... ");
			
			if (failure.get()) {
				fail();
			}
		}
	}

	@Test
	@Order(6)
	public void testAllCalls() {

		RPCClient client = new RPCClient(SERVER, PORT);
		RPCServer server = new RPCServer(PORT);

		AtomicBoolean failure = new AtomicBoolean(false);
		
		Thread serverthread = new Thread() {

			public void run() {

				try {
					TestVoidVoidImpl voidvoidimpl = new TestVoidVoidImpl((byte) 1, server);
					TestStringStringImpl stringstringimpl = new TestStringStringImpl((byte) 2, server);
					TestIntIntImpl intintimpl = new TestIntIntImpl((byte) 3, server);
					TestBooleanBooleanImpl boolboolimpl = new TestBooleanBooleanImpl((byte) 4, server);

					server.run();
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				} finally {
					server.stop();
				}
			}
		};

		Thread clientthread = new Thread() {

			public void run() {

				try {
					client.connect();

					RPCClientStopStub stopstub = new RPCClientStopStub(client);
					TestVoidVoidStub voidvoidstub = new TestVoidVoidStub(client);
					TestStringStringStub stringstringstub = new TestStringStringStub(client);
					TestIntIntStub intintstub = new TestIntIntStub(client);
					TestBooleanBooleanStub boolboolstub = new TestBooleanBooleanStub(client);

					// void test case
					voidvoidstub.m();

					// string test case
					String teststr = "string";
					String resstr = stringstringstub.m(teststr);

					assertEquals(teststr + teststr, resstr);

					// int test case
					int x = 42;
					int resx = intintstub.m(x);

					assertEquals(x, resx);
					// boolean test case

					boolean testb = true;
					boolean resb = boolboolstub.m(testb);

					assertEquals(!testb, resb);

					testb = false;
					resb = boolboolstub.m(testb);
					assertEquals(!testb, resb);

					stopstub.stop();

					client.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				}

			}
		};

		System.out.println("System starting ... ");

		try {

			serverthread.start();
			clientthread.start();

			serverthread.join();
			clientthread.join();

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {

			System.out.println("System stopping ... ");
			
			if (failure.get()) {
				fail();
			}
		}
	}
}

// Start of ./src/test/java/no/hvl/dat110/rpc/tests/TestBooleanBooleanImpl.java
package no.hvl.dat110.rpc.tests;

import no.hvl.dat110.rpc.RPCRemoteImpl;
import no.hvl.dat110.rpc.RPCUtils;
import no.hvl.dat110.rpc.RPCServer;

public class TestBooleanBooleanImpl extends RPCRemoteImpl {

	public TestBooleanBooleanImpl(byte rpcid, RPCServer rpcserver) {
		super(rpcid,rpcserver);
	}
	
	public byte[] invoke(byte[] request) {

		boolean b = RPCUtils.unmarshallBoolean(request);

		boolean resb = m(b);

		byte[] reply = RPCUtils.marshallBoolean(resb);

		return reply;
	}

	public boolean m(boolean b) {
		System.out.println("boolean m(" + b + ") executed");
		return (!b);
	}
}

// Start of ./src/test/java/no/hvl/dat110/rpc/tests/TestRPCUtils.java
package no.hvl.dat110.rpc.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import no.hvl.dat110.rpc.RPCUtils;

class TestRPCUtils {

	@Test
	void testDecapsulate() {
		
		byte[] rpcrequest = {0,1,2};
		
		byte[] payload = RPCUtils.decapsulate(rpcrequest);
		
		assertEquals(2,payload.length);
		assertEquals(1,payload[0]);
		assertEquals(2,payload[1]);
		
	}
	
	@Test
	void testEncapsulate() {
		
		byte[] payload = {1,2};
		
		byte[] rpcrequest = RPCUtils.encapsulate((byte)0, payload);
		
		assertEquals(0,rpcrequest[0]);
		assertEquals(1,rpcrequest[1]);
		assertEquals(2,rpcrequest[2]);
		
	}
	

	
	@Test
	void testMarshallString() {
		
		String str = "teststring";
		
		byte[] encoded = RPCUtils.marshallString(str);
		String decoded = RPCUtils.unmarshallString(encoded);
		
		assertEquals(str,decoded);
	}
	
	@Test
	void testMarshallInteger() {
		
		int testint = 255;
		
		byte[] encoded = RPCUtils.marshallInteger(testint);
		int decoded = RPCUtils.unmarshallInteger(encoded);
		
		assertEquals(testint,decoded);
	}
	
	@Test
	void testMarshallBoolean( ) {
		
		byte[] encoded = RPCUtils.marshallBoolean(true);
		boolean decoded = RPCUtils.unmarshallBoolean(encoded);
		
		assertTrue(decoded);
		
		encoded = RPCUtils.marshallBoolean(false);
		decoded = RPCUtils.unmarshallBoolean(encoded);
		
		assertFalse(decoded);
		
	}
}

// Start of ./src/test/java/no/hvl/dat110/rpc/tests/TestVoidVoidStub.java
package no.hvl.dat110.rpc.tests;

import no.hvl.dat110.rpc.RPCClient;
import no.hvl.dat110.rpc.RPCLocalStub;
import no.hvl.dat110.rpc.RPCUtils;

public class TestVoidVoidStub extends RPCLocalStub {

	public TestVoidVoidStub (RPCClient rpcclient) {
		super(rpcclient);
	}
	
	public void m() {
		
		byte[] request = RPCUtils.marshallVoid();
		
		byte[] reply = rpcclient.call((byte)1,request);
		
		RPCUtils.unmarshallVoid(reply);
		
	}
}

// Start of ./src/test/java/no/hvl/dat110/rpc/tests/TestStringStringStub.java
package no.hvl.dat110.rpc.tests;

import no.hvl.dat110.rpc.RPCClient;
import no.hvl.dat110.rpc.RPCLocalStub;
import no.hvl.dat110.rpc.RPCUtils;

public class TestStringStringStub extends RPCLocalStub {

	public TestStringStringStub(RPCClient rpcclient) {
		super(rpcclient);
	}
	
	public String m(String str) {
		
		byte[] request = RPCUtils.marshallString(str);
		
		byte[] reply = rpcclient.call((byte)2,request);
		
		String strres = RPCUtils.unmarshallString(reply);
		
		return strres;
	}
}

// Start of ./src/test/java/no/hvl/dat110/rpc/tests/TestIntIntImpl.java
package no.hvl.dat110.rpc.tests;

import no.hvl.dat110.rpc.RPCRemoteImpl;
import no.hvl.dat110.rpc.RPCUtils;
import no.hvl.dat110.rpc.RPCServer;

public class TestIntIntImpl extends RPCRemoteImpl {

	public TestIntIntImpl(byte rpcid, RPCServer rpcserver) {
		super(rpcid,rpcserver);
	}
	
	public byte[] invoke(byte[] request) {
		
		int x = RPCUtils.unmarshallInteger(request);
		
		int resx = m(x);
		
		byte[] reply = RPCUtils.marshallInteger(resx);
		
		return reply;
	}
	
	public int m(int x) {
		System.out.println("int m("+x+") executed");
		return x;
	} 
}

// Start of ./src/test/java/no/hvl/dat110/messaging/tests/TestMessaging.java
package no.hvl.dat110.messaging.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import no.hvl.dat110.messaging.MessageConnection;
import no.hvl.dat110.messaging.Message;
import no.hvl.dat110.messaging.MessageUtils;
import no.hvl.dat110.messaging.MessagingClient;
import no.hvl.dat110.messaging.MessagingServer;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestMessaging {

	@Test
	public void test() {

		byte[] clientsent = { 1, 2, 3, 4, 5 };

		AtomicBoolean failure = new AtomicBoolean(false);

		Thread server = new Thread() {

			public void run() {

				MessagingServer server = null;

				try {

					System.out.println("Messaging server - start");

					server = new MessagingServer(MessageUtils.MESSAGINGPORT);

					MessageConnection connection = server.accept();

					Message request = connection.receive();

					byte[] serverreceived = request.getData();

					Message reply = new Message(serverreceived);

					connection.send(reply);

					connection.close();

					assertTrue(Arrays.equals(clientsent, serverreceived));

				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				} finally {
					server.stop();

					System.out.println("Messaging server - stop");
				}

			}
		};

		Thread client = new Thread() {

			public void run() {

				try {

					System.out.println("Messaging client - start");

					MessagingClient client = new MessagingClient(MessageUtils.MESSAGINGHOST,
							MessageUtils.MESSAGINGPORT);

					MessageConnection connection = client.connect();

					Message message1 = new Message(clientsent);

					connection.send(message1);

					Message message2 = connection.receive();

					byte[] clientreceived = message2.getData();

					connection.close();

					System.out.println("Messaging client - stop");

					assertTrue(Arrays.equals(clientsent, clientreceived));
				} catch (Exception e) {
					e.printStackTrace();
					failure.set(true);
				}
			}

		};

		try {
			server.start();
			client.start();

			server.join();
			client.join();

		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			if (failure.get()) {
				fail();
			}
		}

	}
}

// Start of ./src/test/java/no/hvl/dat110/messaging/tests/TestMessage.java
package no.hvl.dat110.messaging.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

// import org.junit.Test;
import org.junit.jupiter.api.Test;

import no.hvl.dat110.messaging.Message;
import no.hvl.dat110.messaging.MessageUtils;

class TestMessage {

	private byte[] createData (int size) {
	
		byte[] data = new byte[size];
		
		for (int i = 0; i<data.length;i++) {
			data[i] = (byte)i;
		}
		
		return data;
	}
	
	@Test
	void testEncapsulate() {
		
		int size = 56;
		byte[] data = createData(size);
		
		Message message = new Message(data);
		
		byte[] encoded = MessageUtils.encapsulate(message);
		
		assertEquals(size,encoded[0]);
		
		assertEquals(MessageUtils.SEGMENTSIZE,encoded.length);
		
		for (int i = 0; i<data.length;i++) {
			assertEquals(data[i],encoded[i+1]);
		}
	}
		
	@Test
	void testDecapsulate() {
		
		byte[] encoded = new byte[MessageUtils.SEGMENTSIZE];
		
		encoded[0] = 5;
		encoded[1] = 1;
		encoded[2] = 2;
		encoded[3] = 3;
		encoded[4] = 4;
		encoded[5] = 5;
		
		Message message = MessageUtils.decapsulate(encoded);
		
		byte[] data = message.getData();
		
		assertEquals(5,data.length);
		
		for (int i = 0;i<5;i++) {
			assertEquals(encoded[i+1],data[i]);
		}
	}

	@Test
	void EncapsulateDecapsulate () {
	
		for (int size = 0;size <= MessageUtils.SEGMENTSIZE-1;size++) {
			
			byte[] data = createData(size);
			
			Message message1 = new Message(data);
			
			byte[] encoded = MessageUtils.encapsulate(message1);
			
			Message message2 = MessageUtils.decapsulate(encoded);
			
			byte[] decoded = message2.getData();
			
			assertTrue(Arrays.equals(data, decoded));
		}
		
	}	
}
// Start of ./src/main/java/no/hvl/dat110/system/controller/SensorStub.java
package no.hvl.dat110.system.controller;

import no.hvl.dat110.TODO;
import no.hvl.dat110.rpc.*;

public class SensorStub extends RPCLocalStub {

	public SensorStub(RPCClient rpcclient) {
		super(rpcclient);
	}

	public int read() {

		// marshall parameter to read call (void parameter)
		byte[] request = RPCUtils.marshallVoid();

		// make remote procedure call for read
		byte[] response = rpcclient.call((byte)Common.READ_RPCID, request);

		// unmarshall the return value from the call (an integer)
		int temp = RPCUtils.unmarshallInteger(response);

		return temp;
	}
}

// Start of ./src/main/java/no/hvl/dat110/system/controller/DisplayStub.java
package no.hvl.dat110.system.controller;

import no.hvl.dat110.TODO;
import no.hvl.dat110.rpc.*;

public class DisplayStub extends RPCLocalStub {

	public DisplayStub(RPCClient rpcclient) {
		super(rpcclient);
	}
	
	public void write (String message) {
		
		// TODO - START
		
		// implement marshalling, call and unmarshalling for write RPC method
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
	}
}

// Start of ./src/main/java/no/hvl/dat110/system/controller/Controller.java
package no.hvl.dat110.system.controller;

import no.hvl.dat110.TODO;
import no.hvl.dat110.rpc.RPCClient;
import no.hvl.dat110.rpc.RPCClientStopStub;

public class Controller  {
	
	private static int N = 5;
	
	public static void main (String[] args) {
		
		DisplayStub display;
		SensorStub sensor;
		
		RPCClient displayclient,sensorclient;
		
		System.out.println("Controller starting ...");
				
		// create RPC clients for the system
		displayclient = new RPCClient(Common.DISPLAYHOST,Common.DISPLAYPORT);
		sensorclient = new RPCClient(Common.SENSORHOST,Common.SENSORPORT);
		
		// setup stop methods in the RPC middleware
		RPCClientStopStub stopdisplay = new RPCClientStopStub(displayclient);
		RPCClientStopStub stopsensor = new RPCClientStopStub(sensorclient);
				
		// TODO - START
		
		// create local display and sensor stub objects
		// connect to sensor and display RPC servers - using the RPCClients
		// read value from sensor using RPC and write to display using RPC
			
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		stopdisplay.stop();
		stopsensor.stop();
	
		displayclient.disconnect();
		sensorclient.disconnect();
		
		System.out.println("Controller stopping ...");
		
	}
}

// Start of ./src/main/java/no/hvl/dat110/system/controller/Common.java
package no.hvl.dat110.system.controller;

public class Common {

	public static int DISPLAYPORT = 8080;
	public static String DISPLAYHOST = "localhost";
	public static int WRITE_RPCID = 2;

	public static int SENSORPORT = 8081;
	public static String SENSORHOST = "localhost";
	public static int READ_RPCID = 1;
	
}

// Start of ./src/main/java/no/hvl/dat110/system/sensor/SensorDevice.java
package no.hvl.dat110.system.sensor;

import no.hvl.dat110.rpc.RPCServer;
import no.hvl.dat110.system.controller.Common;

public class SensorDevice {

	public static void main(String[] args) {

		System.out.println("Sensor server starting ...");
		
		RPCServer sensorserver = new RPCServer(Common.SENSORPORT);

		SensorImpl sensor = new SensorImpl((byte)Common.READ_RPCID,sensorserver);
		
		sensorserver.run();
		
		sensorserver.stop();
		
		System.out.println("Sensor server stopping ...");
		
	}
}

// Start of ./src/main/java/no/hvl/dat110/system/sensor/SensorImpl.java
package no.hvl.dat110.system.sensor;

import no.hvl.dat110.rpc.RPCRemoteImpl;
import no.hvl.dat110.rpc.RPCUtils;
import no.hvl.dat110.rpc.RPCServer;

public class SensorImpl extends RPCRemoteImpl {

	static final int RANGE = 20;

	public SensorImpl(byte rpcid, RPCServer rpcserver) {
		super(rpcid,rpcserver);
	}

	// implementation of the RPC method
	public int read() {

		long seconds = System.currentTimeMillis();

		double temp = RANGE * Math.sin(seconds / 1000);

		System.out.println("READ:" + temp);
		
		return (int) Math.ceil(temp);
	}

	// called by RPC server on rpc identifier corresponding to read
	public byte[] invoke(byte[] param) {
				
		RPCUtils.unmarshallVoid(param);
		
		int temp = read();
				
		byte[] returnval = RPCUtils.marshallInteger(temp); 
		
		return returnval;
	}
}

// Start of ./src/main/java/no/hvl/dat110/system/display/DisplayImpl.java
package no.hvl.dat110.system.display;

import no.hvl.dat110.TODO;
import no.hvl.dat110.rpc.RPCRemoteImpl;
import no.hvl.dat110.rpc.RPCUtils;
import no.hvl.dat110.rpc.RPCServer;

public class DisplayImpl extends RPCRemoteImpl {

	public DisplayImpl(byte rpcid, RPCServer rpcserver) {
		super(rpcid,rpcserver);
	}

	public void write(String message) {
		System.out.println("DISPLAY:" + message);
	}
	
	public byte[] invoke(byte[] param) {
		
		byte[] returnval = null;
		
		// TODO - START: 
		// implement unmarshalling, call, and marshall for write RPC method
		// look at how this is done in the SensorImpl class for the read method
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		return returnval;
	}
}

// Start of ./src/main/java/no/hvl/dat110/system/display/DisplayDevice.java
package no.hvl.dat110.system.display;

import no.hvl.dat110.TODO;
import no.hvl.dat110.rpc.RPCServer;
import no.hvl.dat110.system.controller.Common;


public class DisplayDevice {
	
	public static void main(String[] args) {
		
		System.out.println("Display server starting ...");
		
		// TODO - START
		// implement the operation of the display RPC server
		// see how this is done for the sensor RPC server in SensorDevice
				
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		System.out.println("Display server stopping ...");
		
	}
}

// Start of ./src/main/java/no/hvl/dat110/rpc/RPCClient.java
package no.hvl.dat110.rpc;

import no.hvl.dat110.TODO;
import no.hvl.dat110.messaging.*;

public class RPCClient {

	// underlying messaging client used for RPC communication
	private MessagingClient msgclient;

	// underlying messaging connection used for RPC communication
	private MessageConnection connection;
	
	public RPCClient(String server, int port) {
	
		msgclient = new MessagingClient(server,port);
	}
	
	public void connect() {
		
		// TODO - START
		// connect using the RPC client
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
	}
	
	public void disconnect() {
		
		// TODO - START
		// disconnect by closing the underlying messaging connection
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
	}

	/*
	 Make a remote call om the method on the RPC server by sending an RPC request message and receive an RPC reply message

	 rpcid is the identifier on the server side of the method to be called
	 param is the marshalled parameter of the method to be called
	 */

	public byte[] call(byte rpcid, byte[] param) {
		
		byte[] returnval = null;
		
		// TODO - START

		/*

		The rpcid and param must be encapsulated according to the RPC message format

		The return value from the RPC call must be decapsulated according to the RPC message format

		*/
				
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		return returnval;
		
	}

}

// Start of ./src/main/java/no/hvl/dat110/rpc/RPCClientStopStub.java
package no.hvl.dat110.rpc;

public class RPCClientStopStub extends RPCLocalStub {

	public RPCClientStopStub(RPCClient rpcclient) {
		super(rpcclient);
	}
	
	// client-side implementation of the built-in server stop RPC method
	public void stop () {
		
		byte[] request = RPCUtils.marshallVoid();
		
		byte[] response = rpcclient.call(RPCCommon.RPIDSTOP,request);
		
		RPCUtils.unmarshallVoid(response);
	
	}
}

// Start of ./src/main/java/no/hvl/dat110/rpc/RPCLocalStub.java
package no.hvl.dat110.rpc;

// RPC client-side (local) stubs must extend this class

public abstract class RPCLocalStub {

	protected RPCClient rpcclient;
	
	public RPCLocalStub(RPCClient rpcclient) {
		this.rpcclient = rpcclient;
	}
	
}

// Start of ./src/main/java/no/hvl/dat110/rpc/RPCUtils.java
package no.hvl.dat110.rpc;

import java.nio.ByteBuffer;
import java.util.Arrays;
import no.hvl.dat110.TODO;

public class RPCUtils {
	
	public static byte[] encapsulate(byte rpcid, byte[] payload) {
		
		byte[] rpcmsg = null;
		
		// TODO - START
		
		// Encapsulate the rpcid and payload in a byte array according to the RPC message syntax / format
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		return rpcmsg;
	}
	
	public static byte[] decapsulate(byte[] rpcmsg) {
		
		byte[] payload = null;
		
		// TODO - START
		
		// Decapsulate the rpcid and payload in a byte array according to the RPC message syntax
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		return payload;
		
	}

	// convert String to byte array
	public static byte[] marshallString(String str) {
		
		byte[] encoded = null;
		
		// TODO - START 
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		return encoded;
	}

	// convert byte array to a String
	public static String unmarshallString(byte[] data) {
		
		String decoded = null; 
		
		// TODO - START 
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		return decoded;
	}
	
	public static byte[] marshallVoid() {
		
		byte[] encoded = null;
		
		// TODO - START 
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
				
		// TODO - END
		
		return encoded;
		
	}
	
	public static void unmarshallVoid(byte[] data) {
		
		// TODO
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
	}

	// convert boolean to a byte array representation
	public static byte[] marshallBoolean(boolean b) {
		
		byte[] encoded = new byte[1];
				
		if (b) {
			encoded[0] = 1;
		} else
		{
			encoded[0] = 0;
		}
		
		return encoded;
	}

	// convert byte array to a boolean representation
	public static boolean unmarshallBoolean(byte[] data) {
		
		return (data[0] > 0);
		
	}

	// integer to byte array representation
	public static byte[] marshallInteger(int x) {
		
		byte[] encoded = null;
		
		// TODO - START 
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		return encoded;
	}
	
	// byte array representation to integer
	public static int unmarshallInteger(byte[] data) {
		
		int decoded = 0;
		
		// TODO - START 
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		return decoded;
		
	}
}

// Start of ./src/main/java/no/hvl/dat110/rpc/RPCCommon.java
package no.hvl.dat110.rpc;

public class RPCCommon {

	// RPCID for default stop method on the RPC server
	// no other RPC methods should use this 0 as RPC id
	public static byte RPIDSTOP = 0;
}

// Start of ./src/main/java/no/hvl/dat110/rpc/RPCServerStopImpl.java
package no.hvl.dat110.rpc;

public class RPCServerStopImpl extends RPCRemoteImpl {

	public RPCServerStopImpl(byte rpcid, RPCServer rpcserver) {
		super(rpcid,rpcserver);
	}
	
	// RPC server-side implementation of the built-in stop RPC method
	// params - marshalled parameter for the method
	// return value - marshalled return value
	public byte[] invoke(byte[] param) {
		
		RPCUtils.unmarshallVoid(param);
		
		byte[] returnval = RPCUtils.marshallVoid();
		
		stop(); 
		
		return returnval;
	}
	
	public void stop() {
		
		System.out.println("RPC server executing stop");
		
	}
}

// Start of ./src/main/java/no/hvl/dat110/rpc/RPCRemoteImpl.java
package no.hvl.dat110.rpc;

// RPC server-side method implementations must extend this class

public abstract class RPCRemoteImpl {
	
	public RPCRemoteImpl(byte rpcid, RPCServer rpcserver) {
		rpcserver.register(rpcid, this);
	}

	// method that will be invoked by the server
	// params
	public abstract byte[] invoke(byte[] params);
	
}

// Start of ./src/main/java/no/hvl/dat110/rpc/RPCServer.java
package no.hvl.dat110.rpc;

import java.util.HashMap;

import no.hvl.dat110.TODO;
import no.hvl.dat110.messaging.MessageConnection;
import no.hvl.dat110.messaging.Message;
import no.hvl.dat110.messaging.MessagingServer;

public class RPCServer {

	private MessagingServer msgserver;
	private MessageConnection connection;
	
	// hashmap to register RPC methods which are required to extend RPCRemoteImpl
	// the key in the hashmap is the RPC identifier of the method
	private HashMap<Byte,RPCRemoteImpl> services;
	
	public RPCServer(int port) {
		
		this.msgserver = new MessagingServer(port);
		this.services = new HashMap<Byte,RPCRemoteImpl>();
		
	}
	
	public void run() {
		
		// the stop RPC method is built into the server
		RPCRemoteImpl rpcstop = new RPCServerStopImpl(RPCCommon.RPIDSTOP,this);
		
		System.out.println("RPC SERVER RUN - Services: " + services.size());
			
		connection = msgserver.accept(); 
		
		System.out.println("RPC SERVER ACCEPTED");
		
		boolean stop = false;
		
		while (!stop) {
	    
		   byte rpcid = 0;
		   Message requestmsg, replymsg;
		   
		   // TODO - START
		   // - receive a Message containing an RPC request
		   // - extract the identifier for the RPC method to be invoked from the RPC request
		   // - extract the method's parameter by decapsulating using the RPCUtils
		   // - lookup the method to be invoked
		   // - invoke the method and pass the param
		   // - encapsulate return value 
		   // - send back the message containing the RPC reply
			
		   if (true)
				throw new UnsupportedOperationException(TODO.method());
		   
		   // TODO - END

			// stop the server if it was stop methods that was called
		   if (rpcid == RPCCommon.RPIDSTOP) {
			   stop = true;
		   }
		}
	
	}
	
	// used by server side method implementations to register themselves in the RPC server
	public void register(byte rpcid, RPCRemoteImpl impl) {
		services.put(rpcid, impl);
	}
	
	public void stop() {

		if (connection != null) {
			connection.close();
		} else {
			System.out.println("RPCServer.stop - connection was null");
		}
		
		if (msgserver != null) {
			msgserver.stop();
		} else {
			System.out.println("RPCServer.stop - msgserver was null");
		}
		
	}
}

// Start of ./src/main/java/no/hvl/dat110/TODO.java
package no.hvl.dat110;

public class TODO {

	public static String method() {
		
		String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		return "Metoden " + methodName + " er ikke implementert";
	}
	
	
	public static String constructor(String className) {
				
	   return "Konstruktøren for klassen " + className + " er ikke implementert";
		
	}

}

// Start of ./src/main/java/no/hvl/dat110/messaging/Message.java
package no.hvl.dat110.messaging;

import no.hvl.dat110.TODO;

public class Message {

	// the up to 127 bytes of data (payload) that a message can hold
	private byte[] data;

	// construction a Message with the data provided
	public Message(byte[] data) {
		
		// TODO - START
		
		if (true)
			throw new UnsupportedOperationException(TODO.constructor("Message"));
			
		// TODO - END
	}

	public byte[] getData() {
		return this.data; 
	}

}

// Start of ./src/main/java/no/hvl/dat110/messaging/MessageConnection.java
package no.hvl.dat110.messaging;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import no.hvl.dat110.TODO;


public class MessageConnection {

	private DataOutputStream outStream; // for writing bytes to the underlying TCP connection
	private DataInputStream inStream; // for reading bytes from the underlying TCP connection
	private Socket socket; // socket for the underlying TCP connection
	
	public MessageConnection(Socket socket) {

		try {

			this.socket = socket;

			outStream = new DataOutputStream(socket.getOutputStream());

			inStream = new DataInputStream (socket.getInputStream());

		} catch (IOException ex) {

			System.out.println("Connection: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void send(Message message) {

		byte[] data;
		
		// TODO - START
		// encapsulate the data contained in the Message and write to the output stream
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
			
		// TODO - END

	}

	public Message receive() {

		Message message = null;
		byte[] data;
		
		// TODO - START
		// read a segment from the input stream and decapsulate data into a Message
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		return message;
		
	}

	// close the connection by closing streams and the underlying socket	
	public void close() {

		try {
			
			outStream.close();
			inStream.close();

			socket.close();
			
		} catch (IOException ex) {

			System.out.println("Connection: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
// Start of ./src/main/java/no/hvl/dat110/messaging/MessagingClient.java
package no.hvl.dat110.messaging;


import java.net.Socket;

import no.hvl.dat110.TODO;

public class MessagingClient {

	// name/IP address of the messaging server
	private String server;

	// server port on which the messaging server is listening
	private int port;
	
	public MessagingClient(String server, int port) {
		this.server = server;
		this.port = port;
	}
	
	// setup of a messaging connection to a messaging server
	public MessageConnection connect () {

		// client-side socket for underlying TCP connection to messaging server
		Socket clientSocket;

		MessageConnection connection = null;
		
		// TODO - START
		// connect to messaging server using a TCP socket
		// create and return a corresponding messaging connection
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		return connection;
	}
}

// Start of ./src/main/java/no/hvl/dat110/messaging/MessageUtils.java
package no.hvl.dat110.messaging;

import java.util.Arrays;

import no.hvl.dat110.TODO;

public class MessageUtils {

	public static final int SEGMENTSIZE = 128;

	public static int MESSAGINGPORT = 8080;
	public static String MESSAGINGHOST = "localhost";

	public static byte[] encapsulate(Message message) {
		
		byte[] segment = null;
		byte[] data;
		
		// TODO - START
		
		// encapulate/encode the payload data of the message and form a segment
		// according to the segment format for the messaging layer
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
			
		// TODO - END
		return segment;
		
	}

	public static Message decapsulate(byte[] segment) {

		Message message = null;
		
		// TODO - START
		// decapsulate segment and put received payload data into a message
		
		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		return message;
		
	}
	
}

// Start of ./src/main/java/no/hvl/dat110/messaging/MessagingServer.java
package no.hvl.dat110.messaging;

import java.io.IOException;
import java.net.ServerSocket;

import no.hvl.dat110.TODO;

public class MessagingServer {

	// server-side socket for accepting incoming TCP connections
	private ServerSocket welcomeSocket;

	public MessagingServer(int port) {

		try {

			this.welcomeSocket = new ServerSocket(port);

		} catch (IOException ex) {

			System.out.println("Messaging server: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	// accept an incoming connection from a client
	public MessageConnection accept() {

		MessageConnection connection = null;

		// TODO - START
		// accept TCP connection on welcome socket and create messaging connection to be returned

		if (true)
			throw new UnsupportedOperationException(TODO.method());
		
		// TODO - END
		
		return connection;

	}

	public void stop() {

		if (welcomeSocket != null) {

			try {
				welcomeSocket.close();
			} catch (IOException ex) {

				System.out.println("Messaging server: " + ex.getMessage());
				ex.printStackTrace();
			}
		}
	}

}
