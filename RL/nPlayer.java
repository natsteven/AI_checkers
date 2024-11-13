import java.util.*;
import java.io.*;

public class nPlayer {

    static Random random=new Random();

    static void setupBoardState(State state, int player, char[][] board)
    {
        /* Set up the current state */
        state.player = player;
        nHelper.memcpy(state.board,board);

        /* Find the legal moves for the current state */
        nHelper.FindLegalMoves(state);
    }

    
    static void PerformMove(State state, int moveIndex)
    {
        nHelper.PerformMove(state.board, state.movelist[moveIndex], nHelper.MoveLength(state.movelist[moveIndex]));
        state.player = state.player%2+1;
        nHelper.FindLegalMoves(state);
    }


        /* Alpha beta pruning in minimax shuffling the moves beforehand */
    public static void FindBestMove(int player, char[][] board, char[] bestmove) {
        int myBestMoveIndex = 0;
        double moveVal = -1;
        
        State state = new State(); // , nextstate;
        setupBoardState(state, player, board);

        // myBestMoveIndex = random.nextInt(state.numLegalMoves);
        ArrayList<char[]> moves = new ArrayList<>();
        for (int i = 0; i < state.numLegalMoves; i++) moves.add(state.movelist[i]);
        // System.err.println(moves);
        Collections.shuffle(moves, new Random());
        for (int i = 0; i < state.numLegalMoves; i++) state.movelist[i] = moves.get(i);

        for (int x = 0; x < state.numLegalMoves; x++) {
            State nextState = new State(state);
            PerformMove(nextState, x);
        //     // printBoard(nextState);
        //     // System.err.println("Eval of board: " + evalBoard(nextState));
            double temp = min(nextState, moveVal, Double.POSITIVE_INFINITY, 7);
            // double temp = nHelper.getPrediction(nextState);
            if(temp > moveVal){
                moveVal = temp;
                myBestMoveIndex = x;
            }

        }
        System.err.println("Eval of move: " + moveVal);
        nHelper.memcpy(bestmove, state.movelist[myBestMoveIndex], nHelper.MoveLength(state.movelist[myBestMoveIndex]));
    }

    static double min(State state, double alpha, double beta, int depth){
        if (depth<=0) return nHelper.getPrediction(state);
        depth--;
 
        for (int x = 0; x <state.numLegalMoves; x++){
            State nextState = new State(state);
            PerformMove(nextState, x);

            beta = Math.min(beta, max(nextState, alpha, beta, depth));

            if (beta <= alpha) return alpha;
        }
        return beta;
    }

    static double max(State state, double alpha, double beta, int depth){
        if (depth<=0) return 1-nHelper.getPrediction(state);
        depth--;

        for (int x = 0; x <state.numLegalMoves; x++){
            State nextState = new State(state);
            PerformMove(nextState, x);

            alpha = Math.max(alpha, min(nextState, alpha, beta, depth));

            if (alpha >= beta) return beta;
        }
        return alpha;
    }

    static void printBoard(State state)
    {
        int y,x;

        for(y=0; y<8; y++) 
        {
            for(x=0; x<8; x++)
            {
                if(x%2 != y%2)
                {
                     if(nHelper.empty(state.board[y][x]))
                     {
                         System.err.print(" ");
                     }
                     else if(nHelper.king(state.board[y][x]))
                     {
                         if(nHelper.color(state.board[y][x])==2) System.err.print("B");
                         else System.err.print("A");
                     }
                     else if(nHelper.piece(state.board[y][x]))
                     {
                         if(nHelper.color(state.board[y][x])==2) System.err.print("b");
                         else System.err.print("a");
                     }
                }
                else
                {
                    System.err.print("@");
                }
            }
            System.err.print("\n");
        }
    }


    // static double evalBoard(State state)
    // {
    //     String stateRep = nHelper.convolve(state.board);

    //     Double score = nHelper.stateValues.get(stateRep);
        
    //     if (score == null) score = 0.5;

    //     return score;



    // }

    // public static int[] convolve(char[][] board){
    //     double[][] transpose = new double[8][4];
    //     int i = 0;
    //     // first step of removing irrelevant spaces and assigning pieces
    //     for(int y=0; y<8; y++) for(int x=0; x<8; x++)
    //     {
    //         if(x%2 != y%2)
    //         {
    //              if(nHelper.empty(board[y][x]))
    //              {
    //                 transpose[y][i++] = 0;
    //              }
    //              else if(nHelper.king(board[y][x]))
    //              {
    //                  if(nHelper.color(board[y][x])==2) transpose[y][i++] = 1.3;
    //                  else transpose[y][i++] = -1.3;
    //              }
    //              else if(nHelper.piece(board[y][x]))
    //              {
    //                  if(nHelper.color(board[y][x])==2)transpose[y][i++] = 1; 
    //                  else transpose[y][i++] = -1;
    //            }
    //         }
    //     }

    //     double[][] conv1 = new double[7][3];
    //     for (i=0;i<7;i++) for (int j=0;j<3;j++)
    //     {
    //         conv1[i][j] = transpose[i][j] + transpose[i][j+1] + transpose[i+1][j]+transpose[i+1][j+1];
    //     }

    //     double[][] conv2 = new double[6][2];
    //     for (i=0;i<6;i++) for (int j=0;j<2;j++)
    //     {
    //         conv2[i][j] = conv1[i][j] + conv1[i][j+1] + conv1[i+1][j]+conv1[i+1][j+1];
    //     }

    //     double[] last = new double[5];
    //     for (i=0;i<5;i++){
    //         last[i] = conv2[i][i] + conv2[i][i+1] + conv2[i+1][i]+conv2[i+1][i+1];
    //     }
        
    //     int[] stateRep = new int[5];
    //     for (i=0;i<5;i++){
    //         if (last[i] > 0) stateRep[i] = 1;
    //         else if (last[i] < 0) stateRep[i] = -1;
    //         else stateRep[i] = 0;
    //     }

    //     return stateRep;
    // }

}
