
    import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import javax.swing.JProgressBar;

    public class Player {

        private static EmbeddedMediaPlayerComponent mediaPlayerComponent;
        private static final String HOST = "127.0.0.1";
    	private static final int PORT = 8080;
    	static Socket sock;
    	public static JProgressBar progressBar;

        public static void main(final String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new Player(args);
                }
            });
            try {
            	sock = new Socket(HOST, PORT);
            	DataInputStream in = new DataInputStream(sock.getInputStream());

            	//System.out.println("Enter commands: (play or pause)");

            	//Scanner in = new Scanner(System.in);
            	String command = "";

            	while(true)
            	{
            		//command = in.nextLine();
            		command = in.readUTF();
            		if(command.equalsIgnoreCase("pause"))
            		{
            			System.out.println("Pausing...");
            			mediaPlayerComponent.getMediaPlayer().pause();
            		}
            		else if(command.equalsIgnoreCase("play"))
            		{
            			System.out.println("Playing..");
            			mediaPlayerComponent.getMediaPlayer().play();
            		}
//            		else if(command.equalsIgnoreCase("skip"))
//            		{
//            			System.out.println("Skipping 10 seconds...");
//            			mediaPlayerComponent.getMediaPlayer().skip(100000);
//            		}
            		else if(command.equalsIgnoreCase("volup"))
            		{
            			int vol = mediaPlayerComponent.getMediaPlayer().getVolume() + 20;
            			System.out.println("Increasing volume to "+vol);
            			mediaPlayerComponent.getMediaPlayer().setVolume(vol);
            		}
            		else if(command.equalsIgnoreCase("voldown"))
            		{
            			int vol = mediaPlayerComponent.getMediaPlayer().getVolume() - 20;
            			System.out.println("Decreasing volume to "+vol);
            			mediaPlayerComponent.getMediaPlayer().setVolume(vol);
            		}   
            		else if(command.equalsIgnoreCase("open"))
            		{

            			//System.out.println("Please enter the location of the file:");
            			//command = in.nextLine();
            			String url = in.readUTF();
            			//mediaPlayerComponent.getMediaPlayer().stop();

            			String fileName = url.substring(url.lastIndexOf('/')+1, url.length());
            			saveFromURL(fileName, url);//download file
            			
            			if(mediaPlayerComponent.getMediaPlayer().playMedia(fileName))
            			{
            				System.out.println("Now playing "+fileName);
            			}
            			else
            			{
            				System.out.println("Failed to open "+fileName);
            			}
            		}
            		else if(command.equalsIgnoreCase("stop"))
            		{
            			System.out.println("Stopping media...");
            			mediaPlayerComponent.getMediaPlayer().stop();
            			//mediaPlayerComponent.release();//cleanup for exiting
            		}
            		else if(command.equalsIgnoreCase("exit"))
            		{
            			System.out.println("Exiting media...");
            			mediaPlayerComponent.getMediaPlayer().stop();
            			mediaPlayerComponent.release();//cleanup for exiting
            			System.exit(0);
            		}
            		else
            		{
            			System.out.println("Invalid command.");
            		}

            	}

            } catch (UnknownHostException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            } catch (IOException e) {
            	// TODO Auto-generated catch block
            	e.printStackTrace();
            }
        }
        
        public static void saveFromURL(final String filename, final String urlString)
                throws MalformedURLException, IOException {
            BufferedInputStream in = null;
            FileOutputStream fout = null;
            int size = getFileSize(urlString);
            int progress = 0;
            
            try {
                in = new BufferedInputStream(new URL(urlString).openStream());
                fout = new FileOutputStream(filename);

                final byte data[] = new byte[1024];
                int count;
                progressBar.setStringPainted(true);
                while ((count = in.read(data, 0, 1024)) != -1) 
                {
                	progress += count;
                    fout.write(data, 0, count);
                    //System.out.println("Progress is "+progress+" and size is "+size);
                    
                    progressBar.setString("Downloading file... ("+(100 * progress) / size+"%)");
                    progressBar.setValue((100 * progress) / size);
                }
                progressBar.setStringPainted(false);
            } finally {
                if (in != null) {
                    in.close();
                }
                if (fout != null) {
                    fout.close();
                }
            }
        }
        
        private static int getFileSize(String address) 
        {
        	URL url;
			HttpURLConnection conn = null;
            try {
            	url = new URL(address);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("HEAD");
                conn.getInputStream();
                return conn.getContentLength();
            } catch (IOException e) {
                return -1;
            }finally {
                conn.disconnect();
            }
        }

        private Player(String[] args) {
        	progressBar = new JProgressBar();
            JFrame frame = new JFrame("Player");
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());

            mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

            panel.add(mediaPlayerComponent, BorderLayout.CENTER);
            
            //frame.setContentPane(mediaPlayerComponent);
            frame.getContentPane().add(panel);
            
            
            panel.add(progressBar, BorderLayout.SOUTH);
            frame.setLocation(100, 100);
            frame.setSize(1050, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            //mediaPlayerComponent.getMediaPlayer().playMedia(args[0]);
            
//            mediaPlayerComponent.release();
        }
    }