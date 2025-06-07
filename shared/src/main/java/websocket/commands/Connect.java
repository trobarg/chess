package websocket.commands;

public class Connect extends UserGameCommand {
    //player color or chess game needed?
    public Connect(String authToken, Integer gameID) {
        super(CommandType.CONNECT, authToken, gameID);
    }
}
