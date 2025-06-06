package client;

public interface Client {
    public String eval(String input);
    public int getChangeClientLayer();
    public void resetChangeClientLayer();
}
