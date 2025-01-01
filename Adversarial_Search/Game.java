import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Game {

    public enum gameMode{
        HumanVsAI, AIVsAI
    }
    static gameMode currMode;
    static Scanner scan = new Scanner(System.in);

    final static int MaxDepth = 10;

    static int bestMove = -1;



    double minimax (MancalaBoard board, int depth, double alpha, double beta, boolean extraMove, int heuristicsChoice)
    {
        double bestScore,score;
        ArrayList<Integer> moves = board.getNextMoves();
        if(board.isGameOver() || depth>=MaxDepth)
        {
            return board.calculateHeuristics(heuristicsChoice);
        }
        if(depth==0) bestMove = moves.get(0);
        if(board.p1Turn)
        {
            bestScore = Double.NEGATIVE_INFINITY;
            for(int nextMove : moves)
            {
                MancalaBoard nextBoard = new MancalaBoard(board);
                boolean localExtraMove = nextBoard.executeMove(nextMove);
                if(!localExtraMove) nextBoard.p1Turn = !nextBoard.p1Turn;
                score = minimax(nextBoard,depth+1,alpha,beta,localExtraMove,heuristicsChoice);
                if(score > bestScore)
                {
                    bestScore = score;
                    if(depth==0) bestMove = nextMove;
                }
                alpha =  Math.max(alpha,bestScore);
                if(alpha>=beta) break;
            }
        }
        else
        {
            bestScore = Double.POSITIVE_INFINITY;
            for(int nextMove : moves)
            {
                MancalaBoard nextBoard = new MancalaBoard(board);
                boolean localExtraMove = nextBoard.executeMove(nextMove);
                if(!localExtraMove) nextBoard.p1Turn = !nextBoard.p1Turn;
                score = minimax(nextBoard,depth+1,alpha,beta,localExtraMove,heuristicsChoice);
                if(score < bestScore)
                {
                    bestScore = score;
                    if(depth==0) bestMove = nextMove;
                }
                beta =  Math.min(beta,bestScore);
                if(alpha>=beta) break;
            }
        }

        return bestScore;

    }

    boolean HumanTurn(MancalaBoard board)
    {
        System.out.println("Choose your pit : " + (board.p1Turn ? " 1 to 6" : "8 to 13"));
        int move;
        move = scan.nextInt();
        while(true)
        {
            move--;
            if(board.p1Turn && move>=board.P1StartingPit && move<=board.P1EndingPit && (board.board.get(move)>0)) break;
            if(!board.p1Turn && move>=board.P2StartingPit && move<=board.P2EndingPit && (board.board.get(move)>0)) break;
            System.out.println("Invalid move! Try again..");
            move = scan.nextInt();
        }
        return board.executeMove(move);
    }

     boolean AITurn(MancalaBoard board, int heuristics)
    {
        minimax(board, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,false, heuristics);
        return board.executeMove(bestMove);
    }

    void showResult(MancalaBoard board)
    {
        int p1Stones = board.board.get(board.P1StoragePit);
        int p2Stones = board.board.get(board.P2StoragePit);
        System.out.println("Game over!!");
        System.out.println("Player1 got : " + p1Stones +" stones");
        System.out.println("Player2 got : " + p2Stones +" stones");
        if(p1Stones > p2Stones) System.out.println("Player1 has won the game!");
        else if(p1Stones < p2Stones) System.out.println("Player2 has won the game!");
        else System.out.println("The match is a tie!");
    }

    int getResult(MancalaBoard board)
    {
        int p1Stones = board.board.get(board.P1StoragePit);
        int p2Stones = board.board.get(board.P2StoragePit);
        if(p1Stones > p2Stones) return 1;
        else if(p1Stones < p2Stones) return 2;
        else return -1;
    }

    void generateReport(ArrayList<Integer> Stats, int round)
    {
        System.out.println("Total games played : " + round * 12 );
        for(int i = 1; i<=4; i++)
        {
            System.out.println("Games won with heuristics " + i + ": " + Stats.get(i));
        }
        System.out.println("Games drawn: " + Stats.get(0));
    }
     int playGame(boolean humanPlayerTurn, int h1, int h2, boolean print)
    {
        MancalaBoard board = new MancalaBoard();

        if(print) board.printBoard();
        while(!board.isGameOver())
        {
            boolean extraTurn = false;
            if(currMode == gameMode.HumanVsAI)
            {
                if(humanPlayerTurn) extraTurn = HumanTurn(board);
                else extraTurn = AITurn(board,h1);
            }
            else if(currMode == gameMode.AIVsAI)
            {
                if(board.p1Turn) extraTurn = AITurn(board,h1);
                else extraTurn = AITurn (board,h2);
            }

            if(print) board.printBoard();

            if(!extraTurn)
            {
                if(currMode == gameMode.HumanVsAI) humanPlayerTurn = !humanPlayerTurn;
                board.p1Turn = !board.p1Turn;
            }
            else if(print) System.out.println("Player-" + (board.p1Turn ? "1" : "2") + " has got another turn!");
        }
        if(print)
        {
            board.printBoard();
            showResult(board);
        }
        return getResult(board);
    }


    public static void main(String[] args) {
        Game game = new Game();
        int gameOption;
        System.out.println("Choose game option : (1/2/3)");
        System.out.println("1. Human vs AI");
        System.out.println("2. AI vs AI");
        System.out.println("3. Get Heuristic Report");
        gameOption = scan.nextInt();

        if(gameOption==1)
        {
            currMode = gameMode.HumanVsAI;
            System.out.println("Which player do you want to play : (player1/player2)");
            int humanPlayer = scan.nextInt();
            boolean humanPlayerTurn = (humanPlayer==1);
            game.playGame(humanPlayerTurn,4,4, true);
        }
        else if(gameOption==2)
        {
            currMode = gameMode.AIVsAI;
            int h1,h2;
            System.out.println("Select heuristics for player1 : (1-4)");
            h1 = scan.nextInt();
            System.out.println("Select heuristics for player2 : (1-4)");
            h2 = scan.nextInt();
            game.playGame(true,h1,h2, true);
        }
        else if(gameOption==3)
        {
            currMode = gameMode.AIVsAI;
            ArrayList<Integer> Stats = new ArrayList<>(Collections.nCopies(5,0));
            int round, h1, h2, playerWhoWon;
            System.out.println("How many rounds (each round has 12 games) do you want? ");
            round = scan.nextInt();
            System.out.println("Generating report...");
            for(int k = 0; k<round; k++)
            {
                for(int i = 1;i<=4; i++)
                {
                    for(int j = 1;j<=4; j++)
                    {
                        if(i!=j)
                        {
                            h1 = i;
                            h2 = j;
                            playerWhoWon = game.playGame(true,h1,h2,false);
                            if(playerWhoWon==-1) Stats.set(0,(Stats.get(0)+1));
                            else if(playerWhoWon==1) Stats.set(h1,(Stats.get(h1)+1));
                            else if(playerWhoWon==2) Stats.set(h2,(Stats.get(h2)+1));
                        }
                    }
                }
            }
            game.generateReport(Stats,round);
        }
        else System.out.println("Invalid input");
    }
}
