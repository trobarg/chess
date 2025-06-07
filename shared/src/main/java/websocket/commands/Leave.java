package websocket.commands;

public class Leave extends UserGameCommand{

    public Leave(String authToken, int gameID) {
        super(CommandType.LEAVE, authToken, gameID);
    }
    //not sure what else I actually need in here
}
