package websocket.commands;

public class Leave extends UserGameCommand{
    public Leave(String authToken, int gameID) {
        super(CommandType.LEAVE, authToken, gameID);
    }
}
