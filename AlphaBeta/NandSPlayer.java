import java.util.*;
import java.io.*;

public class NandSPlayer {

    static Random random=new Random();

    static void setupBoardState(State state, int player, char[][] board)
    {
        /* Set up the current state */
        state.player = player;
        NandSHelper.memcpy(state.board,board);

        /* Find the legal moves for the current state */
        NandSHelper.FindLegalMoves(state);
    }

    
    static void PerformMove(State state, int moveIndex)
    {
        NandSHelper.PerformMove(state.board, state.movelist[moveIndex], NandSHelper.MoveLength(state.movelist[moveIndex]));
        state.player = state.player%2+1;
        NandSHelper.FindLegalMoves(state);
    }


        /* Alpha beta pruning in minimax shuffling the moves beforehand */
    public static void FindBestMove(int player, char[][] board, char[] bestmove) {
        int myBestMoveIndex = 0;
        double moveVal = Double.NEGATIVE_INFINITY;
        
        State state = new State(); // , nextstate;
        setupBoardState(state, player, board);

        ArrayList<char[]> moves = new ArrayList<>();
        for (int i = 0; i < state.numLegalMoves; i++) moves.add(state.movelist[i]);
        // System.err.println(moves);
        Collections.shuffle(moves, new Random());
        for (int i = 0; i < state.numLegalMoves; i++) state.movelist[i] = moves.get(i);

        for (int x = 0; x < state.numLegalMoves; x++) {
            State nextState = new State(state);
            PerformMove(nextState, x);
            // printBoard(nextState);
            // System.err.println("Eval of board: " + evalBoard(nextState));
            double temp = min(nextState, moveVal, Double.POSITIVE_INFINITY, 7);
            if(temp > moveVal){
                moveVal = temp;
                myBestMoveIndex = x;
            }

        }
        System.err.println("Eval of move: " + moveVal);
        NandSHelper.memcpy(bestmove, state.movelist[myBestMoveIndex], NandSHelper.MoveLength(state.movelist[myBestMoveIndex]));
    }

    static double min(State state, double alpha, double beta, int depth){
        if (depth<=0) return 1/evalBoard(state);
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
        if (depth<=0) return evalBoard(state);
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
                     if(NandSHelper.empty(state.board[y][x]))
                     {
                         System.err.print(" ");
                     }
                     else if(NandSHelper.king(state.board[y][x]))
                     {
                         if(NandSHelper.color(state.board[y][x])==2) System.err.print("B");
                         else System.err.print("A");
                     }
                     else if(NandSHelper.piece(state.board[y][x]))
                     {
                         if(NandSHelper.color(state.board[y][x])==2) System.err.print("b");
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

    /* An example of how to walk through a board and determine what pieces are on it*/
    static double evalBoard(State state)
    {
        int y,x;
        double score, p1Pieces, p2Pieces, p1Rat, p2Rat;
        int  p1Cluster, p2Cluster, p1BackPawn, p2BackPawn;
        ArrayList<Integer> p1X, p1Y, p2X, p2Y;
        p1X = new ArrayList<>();
        p1Y = new ArrayList<>();
        p2X = new ArrayList<>();
        p2Y = new ArrayList<>();
        score = 0.0;
        p1Pieces = 0.0;
        p2Pieces = 0.0;
        p1BackPawn = 0;
        p2BackPawn = 0;

        for(y=0; y<8; y++) for(x=0; x<8; x++)
        {
            if(x%2 != y%2)
            {
                 if(NandSHelper.empty(state.board[y][x]))
                 {
                 }
                 else if(NandSHelper.king(state.board[y][x]))
                 {
                     if(NandSHelper.color(state.board[y][x])==2){
                        p2Pieces += 5.0;
                        p2X.add(x);
                        p2Y.add(y);
                     } else {
                        p1Pieces += 5.0;
                        p1X.add(x);
                        p1Y.add(y);
                     }
                 }
                 else if(NandSHelper.piece(state.board[y][x]))
                 {
                     if(NandSHelper.color(state.board[y][x])==2){
                        if (y == 0) p2BackPawn+=1;
                        p2Pieces += 3.0;
                        p2X.add(x);
                        p2Y.add(y);
                     } else {
                        if (y == 8) p1BackPawn+=1;
                        p1Pieces += 3.0;
                        p1X.add(x);
                        p1Y.add(y);
                     }
               }
            }
        }

        double totalPieces = p1Pieces + p2Pieces;
        p2Rat = p1Pieces!=0 ? p2Pieces/p1Pieces : Double.MAX_VALUE;
        p1Rat = p2Pieces!=0 ? p1Pieces/p2Pieces: Double.MAX_VALUE;

        //pairwise manhattan distance between pieces
        p1Cluster = 0;
        for (int i = 0; i < p1X.size() - 1; i++){
            for (int j = i+ 1; j < p1X.size(); j++){
                p1Cluster += (Math.abs(p1X.get(i) - p1X.get(j)) + Math.abs(p1Y.get(i) - p1Y.get(j)));
            }
        }
        p2Cluster = 0;
        for (int i = 0; i < p2X.size() - 1; i++){
            for (int j = i+ 1; j < p2X.size(); j++){
                p2Cluster += (Math.abs(p2X.get(i) - p2X.get(j)) + Math.abs(p2Y.get(i) - p2Y.get(j)));
            }
        }
        //divde by at least number of pieces yielding a cluster coefficient (otherwise less pieces -> more cluster value)
        p1Cluster = (int)(p1Cluster/p1Pieces);
        p2Cluster = (int)(p2Cluster/p1Pieces); 

        
        double trade1 = 0.0;
        double trade2 = 0.0;
        if (p1Pieces < 7.0 && p1Rat > 1.0) trade1 = -p1Pieces;
        if (p2Pieces < 7.0 && p2Rat > 1.0) trade2 = -p2Pieces;

        double p1Score = Math.floor(p1Rat*1000) + p1BackPawn -p1Cluster + trade1;
        double p2Score = Math.floor(p2Rat*1000) + p2BackPawn -p2Cluster + trade2;

        score = p2Score/p1Score;
        if(state.player==1) score = 1/score;   

        return score;

    }

}
