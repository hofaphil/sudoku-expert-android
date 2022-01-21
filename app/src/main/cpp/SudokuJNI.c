#include <jni.h>
#include "generator/Sudoku.h"

JNIEXPORT jobject JNICALL
Java_com_aol_philipphofer_logic_MainActivity_createSudokuNative(JNIEnv *env, jobject mainActivity, jint jFreeFields) {
    /**
     * Sudoku
     */
    jclass jSudokuClass = (*env)->FindClass(env, "com/aol/philipphofer/logic/sudoku/Sudoku");
    jmethodID jNewSudoku = (*env)->GetMethodID(env, jSudokuClass, "<init>", "()V");
    jobject jSudoku = (*env)->NewObject(env, jSudokuClass, jNewSudoku);
    jmethodID jGetSudoku = (*env)->GetMethodID(env, jSudokuClass, "getSudoku", "()[Lcom/aol/philipphofer/logic/sudoku/Block;");

    /**
     * Block
     */
    jclass jBlockClass = (*env)->FindClass(env, "com/aol/philipphofer/logic/sudoku/Block");
    jmethodID jSetNumber = (*env)->GetMethodID(env, jBlockClass, "setNumber", "(IILcom/aol/philipphofer/logic/sudoku/Number;)V");

    /**
     * Number
     */
    jclass jNumberClass = (*env)->FindClass(env, "com/aol/philipphofer/logic/sudoku/Number");
    jmethodID jNewNumber = (*env)->GetMethodID(env, jNumberClass, "<init>", "(IIZ)V");

    /**
     * Generate sudoku with c code
     */
    sudoku *sudoku = new_sudoku();
    create(sudoku, jFreeFields);

    /**
     * Set Sudoku
     */
    jobjectArray jBlocks = (*env)->CallObjectMethod(env, jSudoku, jGetSudoku);
    for (int b = 0; b < 9; b++) {
        jobject jBlock = (*env)->GetObjectArrayElement(env, jBlocks, b);
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                int number = sudoku->blocks[b].numbers[i][j];
                int solution = sudoku->solution[b].numbers[i][j];
                int changeable = number == 0 ? 1 : 0;
                jobject jNumber = (*env)->NewObject(env, jNumberClass, jNewNumber, number, solution, changeable);

                (*env)->CallVoidMethod(env, jBlock, jSetNumber, i, j, jNumber);
            }
    }

    return jSudoku;
}

JNIEXPORT void JNICALL
Java_com_aol_philipphofer_logic_ShareClass_solveSudokuNative(JNIEnv *env, jobject mainActivity, jobject jSudoku) {
    /**
     * Sudoku
     */
    jclass jSudokuClass = (*env)->FindClass(env, "com/aol/philipphofer/logic/sudoku/Sudoku");
    jmethodID jGetNumber = (*env)->GetMethodID(env, jSudokuClass, "getNumber", "(Lcom/aol/philipphofer/logic/Position;)Lcom/aol/philipphofer/logic/sudoku/Number;");

    /**
     * Number
     */
    jclass jNumberClass = (*env)->FindClass(env, "com/aol/philipphofer/logic/sudoku/Number");
    jmethodID jGetNumberValue = (*env)->GetMethodID(env, jNumberClass, "getNumber", "()I");
    jmethodID jSetSolutionValue = (*env)->GetMethodID(env, jNumberClass, "setSolution", "(I)V");

    /**
     * Position
     */
    jclass jPositionClass = (*env)->FindClass(env, "com/aol/philipphofer/logic/Position");
    jmethodID jNewPosition = (*env)->GetMethodID(env, jPositionClass, "<init>", "(III)V");

    sudoku *sudoku = new_sudoku();

    /**
     * Init the c-sudoku
     */
    for (int b = 0; b < 9; b++)
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                jobject jPosition = (*env)->NewObject(env, jPositionClass, jNewPosition, i, j, b);
                jobject jNumber = (*env)->CallObjectMethod(env, jSudoku, jGetNumber, jPosition);
                sudoku->blocks[b].numbers[i][j] = (*env)->CallIntMethod(env, jNumber, jGetNumberValue);
            }

    /**
     * Solve sudoku with c code
     */
    solve(sudoku);

    /**
     * Set Solution
     */
    for (int b = 0; b < 9; b++)
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                jobject jPosition = (*env)->NewObject(env, jPositionClass, jNewPosition, i, j, b);
                jobject jNumber = (*env)->CallObjectMethod(env, jSudoku, jGetNumber, jPosition);
                (*env)->CallVoidMethod(env, jNumber, jSetSolutionValue, sudoku->solution[b].numbers[i][j]);
            }
}