package client;

import server.models.RegistrationForm;

public class Request {
    String command;
    String args;
    RegistrationForm registrationForm;

    public Request(String command, String args){
        this.command = command;
        this.args = args;
    }

    public Request(String command, RegistrationForm registrationForm){
        this.command = command;
        this.registrationForm = registrationForm;
    }
}
