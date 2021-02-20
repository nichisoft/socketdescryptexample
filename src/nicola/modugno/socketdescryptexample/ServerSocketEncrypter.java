package nicola.modugno.socketdescryptexample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ServerSocketEncrypter {
	
	private ServerSocket server;
	//private final int PORT = 9876;

	public static void main(String[] args) {
		if(args.length!=2) {
			throw new IllegalArgumentException("Usage: java ServerSocketEncrypter <PORT> <SECRET_KEY>");
		}
		String port=null;
		String secretKey=null;
		port=args[0];
		secretKey=args[1];
		
		int portInt=0;
		try {
			portInt=Integer.parseInt(port);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Port must be a number from 1 to 65535");
		}
		ServerSocketEncrypter s=new ServerSocketEncrypter();
		s.start(portInt, secretKey);
	}
	
	private void start(final int port, final String secretKey) {
		try {
			//server=new ServerSocket(PORT);
			server=new ServerSocket(port);
			
			while(true) {
				System.out.println("[Server] Waiting for the client request");
				Socket socket=server.accept();
				
				ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
				ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
				try {
					String message=(String)ois.readObject();
					System.out.println("[Server] Message received: "+message);
					
					String encrtpted=cryptMessage(message, secretKey);
					System.out.println("[Server] Encypted message: "+encrtpted);
					
					oos.writeObject(encrtpted);
					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} finally {
					if(oos!=null)
						oos.close();
					if(ois!=null)
						ois.close();
					socket.close();
				}
				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String cryptMessage(final String plainText, final String secretKey) {
		String encryptedText=null;
		try {
			// generate secret key using DES algorithm
			//key = KeyGenerator.getInstance("DES").generateKey();
			//SecretKey key=new SecretKeySpec("qwerty1!".getBytes(), "DES"); //la chiave deve essere lunga 64 bit (8 Byte) 8 bit sono di controllo 56 bit restanti
			SecretKey key=new SecretKeySpec(secretKey.getBytes(), "DES");
			Cipher ecipher = Cipher.getInstance("DES");

			// initialize the ciphers with the given key
			ecipher.init(Cipher.ENCRYPT_MODE, key);
			
			// encode the string into a sequence of bytes using the named charset
			// storing the result into a new byte array.
			byte[] encrypted = ecipher.doFinal(plainText.getBytes());
			encryptedText=new String(Base64.getEncoder().encodeToString(encrypted));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedText;
	}

}
