#include <jni.h>
#include "generator/Sudoku.h"

JNIEXPORT jobject JNICALL
Java_com_aol_philipphofer_logic_MainActivity_createSudokuNative(JNIEnv *env, jobject mainActivity, jint jDifficulty)
{
    /**
     * Sudoku
     */
    jclass jSudokuClass = (*env)->FindClass(env, "com/aol/philipphofer/sudoku/Sudoku");
    jmethodID jNewSudoku = (*env)->GetMethodID(env, jSudokuClass, "<init>", "()V");
    jobject jSudoku = (*env)->NewObject(env, jSudokuClass, jNewSudoku);

    jmethodID jSetSudoku = (*env)->GetMethodID(env, jSudokuClass, "setSudoku", "([Lcom/aol/philipphofer/sudoku/Block;)V");
    jmethodID jGetSudoku = (*env)->GetMethodID(env, jSudokuClass, "getSudoku", "()[Lcom/aol/philipphofer/sudoku/Block;");
    jmethodID jSetGame = (*env)->GetMethodID(env, jSudokuClass, "setGame", "([Lcom/aol/philipphofer/sudoku/Block;)V");
    jmethodID jGetGame = (*env)->GetMethodID(env, jSudokuClass, "getGame", "()[Lcom/aol/philipphofer/sudoku/Block;");

    /**
     * Block
     */
    jclass jBlockClass = (*env)->FindClass(env, "com/aol/philipphofer/sudoku/Block");
    jmethodID jSetNumber = (*env)->GetMethodID(env, jBlockClass, "setNumber", "(Lcom/aol/philipphofer/sudoku/Number;II)V");

    /**
     * Number
     */
    jclass jNumberClass = (*env)->FindClass(env, "com/aol/philipphofer/sudoku/Number");
    jmethodID jNewNumber = (*env)->GetMethodID(env, jNumberClass, "<init>", "(IIZ)V");

    /**
     * Generate sudoku with c code
     */
    sudoku *sudoku = new_sudoku();
    create(sudoku, jDifficulty);

    /**
     * Set Sudoku
     */
    jobjectArray jBlocks = (*env)->CallObjectMethod(env, jSudoku, jGetSudoku);
    jobjectArray jGame = (*env)->CallObjectMethod(env, jSudoku, jGetGame);
    for (int b = 0; b < 9; b++) {
        jobject jBlock = (*env)->GetObjectArrayElement(env, jBlocks, b);
        jobject jGameBlock = (*env)->GetObjectArrayElement(env, jGame, b);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                int number = sudoku->blocks[b].numbers[i][j];
                int solution = sudoku->solution[b].numbers[i][j];
                int changeable = number == 0 ? 1 : 0;
                jobject jNumber = (*env)->NewObject(env, jNumberClass, jNewNumber, number, solution, changeable);

                (*env)->CallVoidMethod(env, jBlock, jSetNumber, jNumber, i, j);
                (*env)->CallVoidMethod(env, jGameBlock, jSetNumber, jNumber, i, j);
            }
    }
    (*env)->CallVoidMethod(env, jSudoku, jSetSudoku, jBlocks);
    (*env)->CallVoidMethod(env, jSudoku, jSetGame, jBlocks);

    return jSudoku;
}

//JNIEXPORT jobject JNICALL
//Java_com_aol_philipphofer_logic_ShareClass_solveSudokuNative(JNIEnv *env, jobject mainActivity, jobject jSudoku)
//{
//    /**
//     * Sudoku
//     */
//    jclass jSudokuClass = (*env)->FindClass(env, "com/aol/philipphofer/sudoku/Sudoku");
//    jmethodID jGetSudoku = (*env)->GetMethodID(env, jSudokuClass, "getSudoku", "()[Lcom/aol/philipphofer/sudoku/Block;");
//    jmethodID jGetSolution = (*env)->GetMethodID(env, jSudokuClass, "getSolution", "()[Lcom/aol/philipphofer/sudoku/Block;");
//
//    /**
//     * Block
//     */
//    jclass jBlockClass = (*env)->FindClass(env, "com/aol/philipphofer/sudoku/Block");
//    jmethodID jInsert = (*env)->GetMethodID(env, jBlockClass, "insert", "(III)V");
//    jmethodID jGetNumber = (*env)->GetMethodID(env, jBlockClass, "getNumber", "(II)I");
//
//    jobjectArray jBlocks = (*env)->CallObjectMethod(env, jSudoku, jGetSudoku);
//    sudoku *sudoku = new_sudoku();
//
//    /**
//     * Init the sudoku
//     */
//    for (int b = 0; b < 9; b++) {
//        jobject jBlock = (*env)->GetObjectArrayElement(env, jBlocks, b);
//        for (int i = 0; i < 3; i++)
//            for (int j = 0; j < 3; j++)
//                insert(&sudoku->blocks[b], (*env)->CallIntMethod(env, jBlock, jGetNumber, i, j), i, j);
//    }
//
//    /**
//     * Solve sudoku with c code
//     */
//    solve(sudoku);
//
//    /**
//     * Set Solution
//     */
//    jBlocks = (*env)->CallObjectMethod(env, jSudoku, jGetSolution);
//    for (int b = 0; b < 9; b++) {
//        jobject jBlock = (*env)->GetObjectArrayElement(env, jBlocks, b);
//        for (int i = 0; i < 3; i++)
//            for (int j = 0; j < 3; j++)
//                (*env)->CallVoidMethod(env, jBlock, jInsert, sudoku->solution[b].numbers[i][j], i, j);
//    }
//
//    return jBlocks;
//}