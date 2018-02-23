package com.github.teocci.codesample.javafx.games.engine;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Feb-22
 */
public class EngineConnector
{
    private Stockfish client;

    public EngineConnector()
    {
        client = new Stockfish();
        // Initialize and connect to engine
        if (client.startEngine()) {
            System.out.println("Engine has started..");
            client.sendCommand("uci");
            System.out.println(client.getOutput(0));
            client.sendCommand("setoption name Hash value 1024");
            System.out.println(client.getOutput(0));
            client.sendCommand("setoption name Threads value 1");
            System.out.println(client.getOutput(0));

        } else {
            System.out.println("Oops! Something went wrong..");
        }
    }

    public String getNextMove(String position)
    {
        // Get the best move for a position with a given think time
        String bestMove = client.getBestMove(position, 100);
        System.out.println("Best move : " + bestMove);
        return bestMove;
    }

    public void close()
    {
        client.stopEngine();
    }
}
