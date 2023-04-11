package client;

public class Request {
    String command;
    String args;

    public Request(String command, String args){
        this.command = command;
        this.args = args;
    }
}
