import java.util.Scanner;
import java.util.Random;

public class battleship {
    private static final int BOARD_SIZE = 10;
    private static final char WATER = '~';
    private static final char SHIP = 'S';
    private static final char HIT = 'X';
    private static final char MISS = 'O';

    private char[][] playerBoard;
    private char[][] computerBoard;
    private boolean[][] playerShots;
    private boolean[][] computerShots;
    private Scanner scanner;
    private Random random;

    public battleship() {
        playerBoard = new char[BOARD_SIZE][BOARD_SIZE];
        computerBoard = new char[BOARD_SIZE][BOARD_SIZE];
        playerShots = new boolean[BOARD_SIZE][BOARD_SIZE];
        computerShots = new boolean[BOARD_SIZE][BOARD_SIZE];
        scanner = new Scanner(System.in);
        random = new Random();

        initializeBoards();
    }

    private void initializeBoards() {
        // Inicializa tabuleiros com água
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                playerBoard[i][j] = WATER;
                computerBoard[i][j] = WATER;
            }
        }
    }

    private void placeShipsRandomly(char[][] board) {
        int[] shipSizes = { 5, 4, 3, 3, 2 }; // Tamanhos dos navios

        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                int row = random.nextInt(BOARD_SIZE);
                int col = random.nextInt(BOARD_SIZE);
                boolean horizontal = random.nextBoolean();

                if (canPlaceShip(board, row, col, size, horizontal)) {
                    placeShip(board, row, col, size, horizontal);
                    placed = true;
                }
            }
        }
    }

    private boolean canPlaceShip(char[][] board, int row, int col, int size, boolean horizontal) {
        if (horizontal) {
            if (col + size > BOARD_SIZE)
                return false;
            for (int i = 0; i < size; i++) {
                if (board[row][col + i] != WATER)
                    return false;
            }
        } else {
            if (row + size > BOARD_SIZE)
                return false;
            for (int i = 0; i < size; i++) {
                if (board[row + i][col] != WATER)
                    return false;
            }
        }
        return true;
    }

    private void placeShip(char[][] board, int row, int col, int size, boolean horizontal) {
        if (horizontal) {
            for (int i = 0; i < size; i++) {
                board[row][col + i] = SHIP;
            }
        } else {
            for (int i = 0; i < size; i++) {
                board[row + i][col] = SHIP;
            }
        }
    }

    private void printBoard(char[][] board, boolean hideShips) {
        System.out.print("  ");
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print((i + 1) + " ");
        }
        System.out.println();

        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print((char) ('A' + i) + " ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                char cell = board[i][j];
                if (hideShips && cell == SHIP) {
                    System.out.print(WATER + " ");
                } else {
                    System.out.print(cell + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private void printGameBoards() {
        System.out.println("=== SEU TABULEIRO ===");
        printBoard(playerBoard, false);

        System.out.println("=== TABULEIRO INIMIGO ===");
        printBoard(computerBoard, true);
    }

    private int[] getPlayerShot() {
        while (true) {
            System.out.print("Digite sua jogada (ex: A5): ");
            String input = scanner.nextLine().toUpperCase().trim();

            if (input.length() < 2) {
                System.out.println("Entrada inválida! Use o formato A5.");
                continue;
            }

            char rowChar = input.charAt(0);
            String colStr = input.substring(1);

            if (rowChar < 'A' || rowChar >= 'A' + BOARD_SIZE) {
                System.out.println("Linha inválida! Use A-" + (char) ('A' + BOARD_SIZE - 1));
                continue;
            }

            int row = rowChar - 'A';
            int col;

            try {
                col = Integer.parseInt(colStr) - 1;
                if (col < 0 || col >= BOARD_SIZE) {
                    System.out.println("Coluna inválida! Use 1-" + BOARD_SIZE);
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Coluna inválida! Use 1-" + BOARD_SIZE);
                continue;
            }

            if (playerShots[row][col]) {
                System.out.println("Você já atirou nesta posição!");
                continue;
            }

            return new int[] { row, col };
        }
    }

    private int[] getComputerShot() {
        int row, col;
        do {
            row = random.nextInt(BOARD_SIZE);
            col = random.nextInt(BOARD_SIZE);
        } while (computerShots[row][col]);

        return new int[] { row, col };
    }

    private boolean processShot(char[][] board, int row, int col) {
        if (board[row][col] == SHIP) {
            board[row][col] = HIT;
            return true;
        } else {
            board[row][col] = MISS;
            return false;
        }
    }

    private boolean hasShipsRemaining(char[][] board) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == SHIP) {
                    return true;
                }
            }
        }
        return false;
    }

    public void playGame() {
        System.out.println("=== BEM-VINDO À BATALHA NAVAL ===\n");

        // Posiciona navios aleatoriamente
        placeShipsRandomly(playerBoard);
        placeShipsRandomly(computerBoard);

        System.out.println("Os navios foram posicionados! Que comece a batalha!\n");

        while (true) {
            // Turno do jogador
            printGameBoards();

            int[] playerShot = getPlayerShot();
            int row = playerShot[0];
            int col = playerShot[1];

            playerShots[row][col] = true;
            boolean playerHit = processShot(computerBoard, row, col);

            String position = (char) ('A' + row) + "" + (col + 1);
            if (playerHit) {
                System.out.println("🎯 ACERTOU! Você atingiu um navio em " + position + "!");
            } else {
                System.out.println("💧 ERROU! Água em " + position + ".");
            }

            // Verifica vitória do jogador
            if (!hasShipsRemaining(computerBoard)) {
                printGameBoards();
                System.out.println("🏆 PARABÉNS! VOCÊ VENCEU! 🏆");
                break;
            }

            // Turno do computador
            System.out.println("\nTurno do computador...");
            int[] computerShot = getComputerShot();
            row = computerShot[0];
            col = computerShot[1];

            computerShots[row][col] = true;
            boolean computerHit = processShot(playerBoard, row, col);

            position = (char) ('A' + row) + "" + (col + 1);
            if (computerHit) {
                System.out.println("💥 O computador acertou seu navio em " + position + "!");
            } else {
                System.out.println("🌊 O computador errou em " + position + ".");
            }

            // Verifica vitória do computador
            if (!hasShipsRemaining(playerBoard)) {
                printGameBoards();
                System.out.println("😞 VOCÊ PERDEU! O computador destruiu todos os seus navios!");
                break;
            }

            System.out.println("\nPressione Enter para continuar...");
            scanner.nextLine();
        }

        scanner.close();
    }

    public static void main(String[] args) {
        battleship game = new battleship();
        game.playGame();
    }
}