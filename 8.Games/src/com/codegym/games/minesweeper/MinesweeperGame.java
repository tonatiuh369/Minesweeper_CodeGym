package com.codegym.games.minesweeper;
import com.codegym.engine.cell.*;
import java.util.*;

public class MinesweeperGame extends Game {
    
    private static final int SIDE = 9; 
    private GameObject[][] gameField = new GameObject[SIDE][SIDE]; 
    //GameObject[][] gameField matrix (two-dimensional array) whose dimensions are SIDExSIDE.
    private int countMinesOnField;
    private static final String MINE = "\uD83D\uDCA3"; // Mine/Bomb UTF-16 symbol
    private static final String FLAG = "\uD83D\uDEA9"; // The number of flags (countFlags) must be equal to the number of mines (countMinesOnField).
    private int countFlags;
    
    public void initialize(){
        setScreenSize(SIDE, SIDE);
        createGame();
    }
    
    
    private boolean isGameStopped;
    
    private void createGame(){
        
        this.countMinesOnField = 0;
        boolean isMine;
        /*
        for(int x = 0; x < gameField.length; x++){
            for(int y = 0; y < gameField[x].length; y++){
                setCellValue(x, y, ""); // Clear all items (flags, mines, numbers) from the playing field for each cell 
                // setCellColor should be the same as the game object, x/y not y/x
                // remember that (x,y) coords differ from accessing multi-dimensional [y][x] arrays
                    
            }
        }*/
        
        for(int x = 0; x < gameField.length; x++){
            for(int y = 0; y < gameField[x].length; y++){
                
                setCellValue(x, y, ""); // Clear all items (flags, mines, numbers) from the playing field for each cell 

                int n = getRandomNumber(10);
                //boolean isMine = (n == 3) ?  true : false; // randomly determine whether each cell will have a mine. In this case if n == 3
                if (n == 3){
                    isMine = true;
                    this.countMinesOnField++;
                }
                else {
                    isMine = false; }

                gameField[y][x] = new GameObject(x, y, isMine); //  GameObject(x, y, isMine);
                setCellColor(x, y, Color.ORANGE); // for each cell in the gameField array, call the setCellColor(int, int, Color) method 
                // setCellColor should be the same as the game object, x/y not y/x
                // remember that (x,y) coords differ from accessing multi-dimensional [y][x] arrays
                    
            }
        }
        countMineNeighbors();
        countFlags = countMinesOnField;
    }
    
    private void countMineNeighbors(){ // countMineNeighbors For all the gameField ??
    /* The countMineNeighbors() method should, for each non-mined cell in the gameField 
    matrix, count the number of adjacent mined cells and assign this value to the countMineNeighbors field.*/
        for(int x = 0; x < gameField.length; x++){ // gameField.length = SIDE
            for(int y = 0; y < gameField[x].length; y++){
                if(gameField[y][x].isMine == false)
                    for(GameObject g : getNeighbors(gameField[y][x])){ //The countMineNeighbors() method should use the getNeighbors(GameObject gameObject) method.
                        if(g.isMine)
                          gameField[y][x].countMineNeighbors++;
                    }
            }
        } 
    }
    
    private List<GameObject> getNeighbors(GameObject gameObject) { // getNeighbors For the gameField[y][x] coordinate ?? 
        List<GameObject> result = new ArrayList<>();
        for (int y = gameObject.y - 1; y <= gameObject.y + 1; y++) {
            for (int x = gameObject.x - 1; x <= gameObject.x + 1; x++) {
                if (y < 0 || y >= SIDE) {
                    continue; // The continue statement stops the current execution of the iteration and proceeds to the next iteration.
                }
                if (x < 0 || x >= SIDE) {
                    continue; // The continue statement stops the current execution of the iteration and proceeds to the next iteration.
                }
                if (gameField[y][x] == gameObject) {
                    continue; // The continue statement stops the current execution of the iteration and proceeds to the next iteration.
                }
                result.add(gameField[y][x]);
            }
        }
        return result;
    }
    
    private int countClosedTiles = SIDE * SIDE;
    private int score = 0;
    
    private void openTile(int x, int y){
        
        if (gameField[y][x].isOpen == true) return;
        
        if (gameField[y][x].isFlag == true) return;
        
        if (isGameStopped == true) return;
        
        gameField[y][x].isOpen = true;
        countClosedTiles--;
        setCellColor(x, y, Color.GREEN);
        
        if(countMinesOnField == countClosedTiles && gameField[y][x].isMine == false){
            win();
        }
        
        if(gameField[y][x].isMine) {
            setCellValue(x, y, MINE);  // draw MINE 
            setCellValueEx(x, y, Color.RED, MINE);
            gameOver();
        }
        else{ // if gameField[y][x].isMine == false
        
        //    setCellNumber(x, y, gameField[y][x].countMineNeighbors);// Not necessary ??
        //    getNeighbors(gameField[y][x]); //Not necessary ??
            score += 5;
            setScore(score);
            if(gameField[y][x].countMineNeighbors == 0){
                setCellValue(x, y, "");
                getNeighbors(gameField[y][x]);
                
                // The openTile(int, int) method must be called recursively on each neighbor that hasn't been revealed.
                for(GameObject g : getNeighbors(gameField[y][x])){ 
                        if(g.isOpen == false) // if (g.isOpen == false) // if the tile isn't Open
                            openTile(g.x, g.y);
                    }
                
            }
            else if (gameField[y][x].countMineNeighbors != 0){
                setCellNumber(x, y, gameField[y][x].countMineNeighbors); //  Display the number of mined neighbors on the playing field
            }
        } 
    }
    
    private void markTile(int x, int y){
        if (isGameStopped == true) return;
        
        if (gameField[y][x].isOpen == true) return; //  if the element is already revealed not do anything
        else if (countFlags == 0 && gameField[y][x].isFlag == false) return; // 	not do anything if the number of unused flags (countFlags) is zero and the current element is not a flag 
        
        else if (gameField[y][x].isFlag == false){
            gameField[y][x].isFlag = true;
            countFlags--;
            setCellValue(x, y, FLAG); // draw FLAG
            setCellColor(x, y, Color.YELLOW);
            
        }
        
        else if (gameField[y][x].isFlag == true){
            gameField[y][x].isFlag = false;
            countFlags++;
            setCellValue(x, y, ""); // draw an empty cell
            setCellColor(x, y, Color.ORANGE); // Return the original color of the cell
        }
    }
    
    private void win(){
        isGameStopped = true;
        showMessageDialog(Color.WHITE, "CONGRATULATIONS! YOU WIN!", Color.BLACK, 25);
    } 
    
    private void gameOver(){
        isGameStopped = true;
        showMessageDialog(Color.BLACK, "GAME OVER", Color.RED, 25);
    }
    
    private void restart(){
        isGameStopped = false;
        
        countClosedTiles = SIDE * SIDE;
        score = 0;
        this.countMinesOnField = 0;
        
        setScore(score);
        createGame();

        
    } 
    
    @Override 
    public void onMouseLeftClick(int x, int y){
        if (isGameStopped == true){
            restart();
            return; // return = don't continue
        }
        
        openTile(x, y);
    }
    
    @Override 
    public void onMouseRightClick(int x, int y){
        markTile(x, y);
    }
 
}
