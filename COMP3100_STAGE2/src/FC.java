import java.net.*;
import java.io.*;

public class FC {
    Socket s;
    DataOutputStream outStream;
    BufferedReader inputStream;
    // Constructor

    public FC(String address, int port) throws Exception {
        s = new Socket(address, port);
        outStream = new DataOutputStream(s.getOutputStream());
        inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));

    }

    public static void main(String[] args) throws Exception {
        FC c = new FC("127.0.0.1", 50000);
        System.out.println("yeet");
        c.byClient();

        c.s.close();
        c.inputStream.close();
        c.outStream.close();
    }

    public void byClient() throws Exception {
        sendMessage("HELO"); //send HELO
        recieveMessage(); //recieve OK
        sendMessage("AUTH " + System.getProperty("user.name")); //send AUTH along with the user
        String currentMessage = recieveMessage(); //recieve OK
        
        while(currentMessage.contains("NONE") == false) {
            sendMessage("REDY"); //send REDY
            currentMessage = recieveMessage(); //recieve a message
            String[] currentMessageArray = currentMessage.split(" ");
            if(currentMessage.contains("JOBN")) { //if the message recieved at step 10 is of type JOBN
                getFirstCapable(currentMessageArray);
                currentMessage = recieveMessage();
            }
        }
        sendMessage("QUIT");
        recieveMessage();

    }

    public void getFirstCapable(String[] job) throws Exception {
        sendMessage("GETS Capable "+ job[4]+ " " + job[5] + " " + job[6]); //send GETS Capable
        String dataString = recieveMessage(); // recieve DATA
        String[] dataArray = dataString.split(" ");
        String[] serverArray =  new String[5];
        int nRecs = Integer.parseInt(dataArray[1]);
        sendMessage("OK"); //send OK

        for(int i = 0;i<nRecs;i++) {
            String currentRecord = recieveMessage(); //recieve each record
            String[] currentRecordArray = currentRecord.split(" ");
            if(i == 0) {
                serverArray = currentRecordArray;
            }
        }
        sendMessage("OK"); //send OK
        recieveMessage(); //recieve .
        sendMessage("SCHD " + job[2]+ " " + serverArray[0] + " " + serverArray[1]);
    }


    public void sendMessage(String message) throws Exception {
        this.outStream.write((message + "\n").getBytes("UTF-8"));
    }
    public String recieveMessage() throws Exception {
        return this.inputStream.readLine();
    }
}