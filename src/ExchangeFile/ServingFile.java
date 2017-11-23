package ExchangeFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import materials.MuxDemuxSimple;
import materials.SimpleMessageHandler;
import materials.SynchronizedQueue;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;


public class ServingFile {
	private int PORT = 4242;
	private int backlog = 3;
	//TODO Set queue's size

	public ServingFile(int port, int backlog) {
		this.PORT = port;
		this.backlog = backlog;
	}
	
	public void send(Socket s, String message) {
		try {
			OutputStream output = s.getOutputStream();
			PrintWriter pw = new PrintWriter(output,true);
			pw.println(message);
		}catch(IOException e) {
			System.err.println(e);
		}
	}
	
	public synchronized String formatMessage(String filename) throws FileNotFoundException {
			String message="";
			//Eventually, throws a FileNotFoundException
			File file = new File("/Users/bastienchevallier/Documents/IoT"+filename);
			if (file.isFile()) {
				message = filename + System.getProperty( "line.separator" ) ;
				message += Long.toString(file.length()) + System.getProperty( "line.separator" );
				FileReader in = null;
				try {
					// Read the content of the file and push it into the message
					in = new FileReader(file);
					BufferedReader bin = new BufferedReader(in);
					String line;
					while (bin.ready()) {
						if (!"".equals(line = bin.readLine())){
							message += line + System.getProperty( "line.separator" ) ;
						}
					}
					//Close FileReader and BufferedReader
					in.close();
					bin.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return message;
	}

	public void handleRequest(Socket s) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			String request = br.readLine();
			String reg_exp = "(get ([^ \\n]*)\\n)";
			Pattern pattern = Pattern.compile(reg_exp);
			Matcher matcher = pattern.matcher(request);

			if (matcher.find()) {
				String filename = matcher.group(1);
				String message = formatMessage(filename);
				this.send(s, message);
			}
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
			try {
				s.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

	public void serveFile() {
		//Create a TCP connection listening on port 4242
		try {
			ServerSocket server = new ServerSocket(PORT,backlog);
			while(true) {
				Socket clientsocket = server.accept();
				handleRequest(clientsocket);
				server.close();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
