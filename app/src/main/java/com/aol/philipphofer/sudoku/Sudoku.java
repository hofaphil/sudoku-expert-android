package com.aol.philipphofer.sudoku;

import java.util.*;

public class Sudoku {

    private int THREADCOUNT;
    private Random random;
    private Block[] blocks;
    private Block[] solution;
    private Block[][] threadBlocks;
    private volatile int solutionFlag;

    public Sudoku(int threads) {
        THREADCOUNT = threads;

        random = new Random();
        threadBlocks = new Block[THREADCOUNT][9];
        blocks = new Block[9];
        solution = new Block[9];

        for (int i = 0; i < THREADCOUNT; i++)
            initBlock(threadBlocks[i]);

        initBlock(blocks);
    }

    public void create(int difficulty) {
        blocks[0].generateRandom();
        blocks[4].generateRandom();
        blocks[8].generateRandom();

        generate(1, 1);

        for (int i = 0; i < 9; i++)
            solution[i] = new Block(blocks[i]);

        for (int j = 0; j < THREADCOUNT; j++)
            for (int i = 0; i < 9; i++)
                threadBlocks[j][i].setNumbers(blocks[i].getNumbers());

        solutionFlag = -1;

        Thread[] threads = new Thread[THREADCOUNT];
        for (int i = 0; i < THREADCOUNT; i++) {
            final int j = i;
            threads[i] = new Thread(() -> deleteNumbers(j, difficulty, threadBlocks[j]));
            threads[i].start();
        }

        try {
            for (int i = 0; i < THREADCOUNT; i++)
                threads[i].join();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 9; i++)
            blocks[i].setNumbers(threadBlocks[solutionFlag][i].getNumbers());
    }

    private void deleteNumbers(int threadNumber, int difficulty, Block[] threadHolder) {
        int b = 0;
        int diff = difficulty * 7 + 40;
        //int diff = 2;

        ArrayList<Number> numbers = new ArrayList<>(81);
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 3; j++)
                for (int k = 0; k < 3; k++)
                    numbers.add(new Number(i, threadHolder[i].getNumbers()[j][k]));
        Collections.shuffle(numbers);

        while (solutionFlag == -1) {
            while ((b < diff && !numbers.isEmpty()) && solutionFlag == -1) {
                int index = numbers.get(0).block;
                int number = numbers.get(0).number;
                numbers.remove(0);

                Block holder = new Block(threadHolder[index]);
                if (threadHolder[index].delete(number) && solutionFlag == -1) {
                    b++;
                    if (!checkSolutions(index, threadHolder[index].latestDelIndexX, threadHolder[index].latestDelIndexZ, number, threadHolder)) {
                        threadHolder[index].setNumbers(holder.getNumbers());
                        b--;
                    }
                }
            }
            if (solutionFlag == -1)
                solutionFlag = threadNumber;
        }
    }

    private void initBlock(Block[] block) {
        for (int i = 0; i < 9; i++)
            block[i] = new Block();

        block[0].setRowPartner(block[1], block[2]);
        block[0].setColumnPartner(block[3], block[6]);

        block[1].setRowPartner(block[0], block[2]);
        block[1].setColumnPartner(block[4], block[7]);

        block[2].setRowPartner(block[0], block[1]);
        block[2].setColumnPartner(block[5], block[8]);

        block[3].setRowPartner(block[4], block[5]);
        block[3].setColumnPartner(block[0], block[6]);

        block[4].setRowPartner(block[3], block[5]);
        block[4].setColumnPartner(block[1], block[7]);

        block[5].setRowPartner(block[3], block[4]);
        block[5].setColumnPartner(block[2], block[8]);

        block[6].setRowPartner(block[7], block[8]);
        block[6].setColumnPartner(block[0], block[3]);

        block[7].setRowPartner(block[6], block[8]);
        block[7].setColumnPartner(block[1], block[4]);

        block[8].setRowPartner(block[6], block[7]);
        block[8].setColumnPartner(block[2], block[5]);
    }

    private boolean checkSolutions(final int index, final int x, final int z, int number, Block[] holderNum) {
        Block[] holder = new Block[9];
        for (int i = 0; i < 9; i++)
            holder[i] = new Block(holderNum[i]);

        for (int j = 1; j < 10; j++) {
            if (solutionFlag != -1)
                return true;
            if (j == number)
                continue;
            if (holderNum[index].insert(j, x, z)) {
                if (solve(holderNum, 1, 0)) {
                    for (int i = 0; i < 9; i++)
                        holderNum[i].setNumbers(holder[i].getNumbers());
                    return false;
                }
                holderNum[index].setNumbers(holder[index].getNumbers());
            }
        }
        return true;
    }

    private boolean solve(Block[] blocks, int number, int block) {
        if (number == 10 || solutionFlag != -1)
            return true;

        if (blocks[block].contains(number))
            return solve(blocks, block == 8 ? number + 1 : number, block == 8 ? 0 : block + 1);

        int counter = 0;
        boolean ready;
        int r = 0, c = 0;

        do {
            blocks[block].deleteWithPosition(number, r, c);
            c = (c + 1) % 3;
            if (c == 0)
                r = (r + 1) % 3;
            while (!blocks[block].insertWithoutBlockConflict(number, r, c) && counter++ < 9) {
                c = (c + 1) % 3;
                if (c == 0)
                    r = (r + 1) % 3;
            }

            if (counter >= 9) {
                blocks[block].deleteWithPosition(number, r, c);
                return false;
            }

            ready = solve(blocks, block == 8 ? number + 1 : number, block == 8 ? 0 : block + 1);
        } while (!ready && solutionFlag == -1);
        return true;
    }

    private boolean generate(int number, int block) {
        if (number == 10)
            return true;
        int counter = 0;
        boolean ready;
        int r = random.nextInt(3), c = random.nextInt(3);

        do {
            blocks[block].delete(number);
            c = (c + 1) % 3;
            if (c == 0)
                r = (r + 1) % 3;

            while (!blocks[block].insert(number, r, c) && counter++ < 9) {
                c = (c + 1) % 3;
                if (c == 0)
                    r = (r + 1) % 3;
            }

            if (counter >= 9)
                return false;

            if (block == 7)
                ready = generate(number + 1, 1);
            else if (block == 3)
                ready = generate(number, block + 2);
            else
                ready = generate(number, block + 1);
        } while (!ready);
        return true;
    }

    public Block[] getSolution() {
        return this.solution;
    }

    public Block[] getSolution(Block[] sudoku) throws Exception {
        solutionFlag = -1;
        Block[] solution = new Block[9];
        initBlock(solution);
        for (int i = 0; i < 9; i++)
            solution[i].setNumbers(sudoku[i].getNumbers());

        if (!solve(solution, 1, 0))
            throw new Exception("More than one solution");

        return solution;
    }

    public void setSolution(Block[] blocks) {
        this.solution = blocks;
    }

    public Block[] getSudoku() {
        return this.blocks;
    }

    public void setSudoku(Block[] blocks) {
        this.blocks = blocks;
    }

    public static Block[][] oneD2twoD(Block[] blocks) {
        Block[][] block = new Block[3][3];
        int k = 0;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                block[i][j] = blocks[k++];

        return block;
    }

    public void printSudoku() {
        for (int i = 0; i < 9; i = i + 3) {
            for (int k = 0; k < 3; k++) {
                for (int j = i; j < i + 3; j++) {
                    for (int a = 0; a < 3; a++)
                        System.out.print(blocks[j].getNumbers()[k][a]);
                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public void printBlocks(Block[] blocks) {
        for (int i = 0; i < 9; i = i + 3) {
            for (int k = 0; k < 3; k++) {
                for (int j = i; j < i + 3; j++) {
                    for (int a = 0; a < 3; a++)
                        System.out.print(blocks[j].getNumbers()[k][a]);
                    System.out.print(" ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
