import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class NetworksServer 
{
	private static final String TEST_URL = "192.168.1.127:8888/movies/big_buck_bunny_720p_surround.avi";
	
	public static void main(String[] args) 
	{
		try {
			ServerSocket serverSocket = new ServerSocket(8080);
			
			//while(true)
			//{
				Socket socket = serverSocket.accept();

				DataInputStream inputFromClient = new DataInputStream(
						socket.getInputStream());
				DataOutputStream outputToClient = new DataOutputStream(
						socket.getOutputStream());
				
				outputToClient.writeUTF("open");
				outputToClient.writeUTF(TEST_URL);
				
				Scanner input = new Scanner(System.in);
				String command = "";
				
				while(true)
				{
					System.out.println("Please enter command to send to client");
					command = input.nextLine();					
					outputToClient.writeUTF(command);
				}
				
			//}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
