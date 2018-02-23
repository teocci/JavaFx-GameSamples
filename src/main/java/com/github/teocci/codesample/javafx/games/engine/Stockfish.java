package com.github.teocci.codesample.javafx.games.engine;

import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2018-Feb-22
 */
public class Stockfish
{
    private static final String CMD_IS_READY = "isready";
    private static final String CMD_READY_OK = "readyok";
    private static final String CMD_NEXT_MOVE = "position startpos moves ";
    private static final String CMD_FEN_MOVE = "position fen ";
    private static final String CMD_GO_MOVETIME = "go movetime ";
    private static final String CMD_DRAW = "d";
    private static final String CMD_QUIT = "quit";

    private static final String OUT_BEST_MOVE = "bestmove ";
    private static final String OUT_INFO_DEPTH = "info depth ";
    private static final String OUT_SCORE_CP = "score cp ";
    private static final String OUT_NODES = " nodes";
    private static final String OUT_UPPER_BOUND_NODES = " upperbound nodes";

    private Process engineProcess;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    private static final String PATH = "engine/stockfish";

    /**
     * Starts Stockfish engine as a process and initializes it
     *
     * @return True on success. False otherwise
     */
    public boolean startEngine()
    {
        try {
            File stockfishPath = new ClassPathResource(PATH).getFile();

            engineProcess = Runtime.getRuntime().exec(stockfishPath.getAbsolutePath());
            processReader = new BufferedReader(new InputStreamReader(
                    engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(
                    engineProcess.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Takes in any valid UCI command and executes it
     *
     * @param command string command to execute
     */
    public void sendCommand(String command)
    {
        try {
            processWriter.write(command + "\n");
            processWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is generally called right after 'sendCommand' for getting the raw
     * output from Stockfish
     *
     * @param waitTime Time in milliseconds for which the function waits before
     *                 reading the output. Useful when a long running command is
     *                 executed
     * @return Raw output from Stockfish execution
     */
    public String getOutput(int waitTime)
    {
        StringBuilder buffer = new StringBuilder();
        try {
            Thread.sleep(waitTime);
            sendCommand(CMD_IS_READY);
            while (true) {
                String text = processReader.readLine();
                if (text.equals(CMD_READY_OK)) {
                    break;
                } else {
                    buffer.append(text);
                    buffer.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    /**
     * This function returns the best move for a given position after
     * calculating for 'waitTime' ms
     *
     * @param position Position string
     * @param waitTime in milliseconds
     * @return Best Move in PGN format
     */
    public String getBestMove(String position, int waitTime)
    {
        String cmd = CMD_NEXT_MOVE + position;
        sendCommand(cmd);
//        System.out.println(cmd);

        cmd = CMD_GO_MOVETIME + waitTime;
        sendCommand(cmd);

        String output = getOutput(waitTime + 50);
//        System.out.println("getOutput() " + output);

        return output.split(OUT_BEST_MOVE)[1].split(" ")[0];
    }

    /**
     * This function returns the best move for a given position after
     * calculating for 'waitTime' ms
     *
     * @param fen      Position string
     * @param waitTime in milliseconds
     * @return Best Move in PGN format
     */
    public String getFENBestMove(String fen, int waitTime)
    {
        sendCommand(CMD_FEN_MOVE + fen);
        sendCommand(CMD_GO_MOVETIME + waitTime);
        return getOutput(waitTime + 20).split(OUT_BEST_MOVE)[1].split(" ")[0];
    }

    /**
     * Stops Stockfish and cleans up before closing it
     */
    public void stopEngine()
    {
        try {
            sendCommand(CMD_QUIT);
            processReader.close();
            processWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a list of all legal moves from the given position
     *
     * @param fen Position string
     * @return String of moves
     */
    public String getLegalMoves(String fen)
    {
        sendCommand(CMD_FEN_MOVE + fen);
        sendCommand(CMD_DRAW);
        return getOutput(0).split("Legal moves: ")[1];
    }

    /**
     * Draws the current state of the chess board
     *
     * @param fen Position string
     */
    public void drawBoard(String fen)
    {
        sendCommand(CMD_FEN_MOVE + fen);
        sendCommand(CMD_DRAW);

        String[] rows = getOutput(0).split("\n");

        for (int i = 1; i < 18; i++) {
            System.out.println(rows[i]);
        }
    }

    /**
     * Get the evaluation score of a given board position
     *
     * @param fen      Position string
     * @param waitTime in milliseconds
     * @return evalScore
     */
    public float getEvalScore(String fen, int waitTime)
    {
        sendCommand(CMD_FEN_MOVE + fen);
        sendCommand(CMD_GO_MOVETIME + waitTime);

        float evalScore = 0.0f;
        String[] dump = getOutput(waitTime + 20).split("\n");
        for (int i = dump.length - 1; i >= 0; i--) {
            if (dump[i].startsWith(OUT_INFO_DEPTH)) {
                try {
                    evalScore = Float.parseFloat(
                            dump[i].split(OUT_SCORE_CP)[1].split(OUT_NODES)[0]
                    );
                } catch (Exception e) {
                    evalScore = Float.parseFloat(
                            dump[i].split(OUT_SCORE_CP)[1].split(OUT_UPPER_BOUND_NODES)[0]
                    );
                }
            }
        }
        return evalScore / 100;
    }
}
