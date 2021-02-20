package nicola.modugno.socketdescryptexample;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ClientSocketDecrypter {

	//private final int PORT = 9876;

	public static void main(String[] args) {
		if(args.length!=3) {
			throw new IllegalArgumentException("Usage: java ClientSocketDecrypter <IP_SERVER> <PORT> <SECRET_KEY>");
		}
		String ipServer=null;
		String port=null;
		String secretKey=null;
		ipServer=args[0];
		port=args[1];
		secretKey=args[2];
		
		int portInt=0;
		try {
			portInt=Integer.parseInt(port);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("Port must be a number from 1 to 65535");
		}
		ClientSocketDecrypter c=new ClientSocketDecrypter();
		c.start(ipServer, portInt, secretKey);
	}
	
	private void start(final String ipServer, final int port, final String secretKey) {
		try {
			//InetAddress host = InetAddress.getLocalHost();
			Socket socket = null;
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			try {
				//socket = new Socket(host.getHostName(), PORT);
				socket=new Socket(ipServer, port);
				oos = new ObjectOutputStream(socket.getOutputStream());
				System.out.println("[Client] Sending request to Socket Server");
				String messageToSend="pippo";
				oos.writeObject(messageToSend);
				System.out.println("[Client] Message sended: "+messageToSend);
				ois = new ObjectInputStream(socket.getInputStream());
				try {
					String messageCrypted = (String) ois.readObject();
					System.out.println("[Client] Message crypted received from Server: "+messageCrypted);
					String decrypted = decryptMessage(messageCrypted, secretKey);
					System.out.println("[Client] Message decrypted: "+decrypted);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (ois != null)
					try {
						ois.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if (oos != null)
					try {
						oos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
//		catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private String decryptMessage(final String encryptedText, final String secretKey) {
		String decryptedText=null;
		try {
			// generate secret key using DES algorithm
			//key = KeyGenerator.getInstance("DES").generateKey();
			//SecretKey key=new SecretKeySpec("qwerty1!".getBytes(), "DES"); //la chiave deve essere lunga 64 bit (8 Byte)
			SecretKey key=new SecretKeySpec(secretKey.getBytes(), "DES");
			
			Cipher dcipher = Cipher.getInstance("DES"); //Altri algoritmi: DESede/CBC/PKCS5Padding, Blowfish

			// initialize the ciphers with the given key
			dcipher.init(Cipher.DECRYPT_MODE, key);
			
			// decode with base64 to get bytes
			byte[] encrypted=Base64.getDecoder().decode(encryptedText);
			byte[] decrypted=dcipher.doFinal(encrypted);
			// create new string based on the specified charset
			decryptedText=new String(decrypted);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return decryptedText;
	}

}
