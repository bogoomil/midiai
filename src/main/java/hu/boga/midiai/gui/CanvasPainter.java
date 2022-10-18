package hu.boga.midiai.gui;

import hu.boga.midiai.core.boundaries.dtos.NoteDto;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class CanvasPainter {
    GraphicsContext gc;
    Canvas canvas;
    int tickWidth = 2;
    int octaves = 2;
    int lineHeight = 20;
    int measureNum = 3;
    int resolution = 128;

    public CanvasPainter(final Canvas canvas) {
        this.gc = canvas.getGraphicsContext2D();
        this.canvas = canvas;
    }

    public void paintNotes(List<NoteDto> notes){
        initializeCanvas();
        paintVerticalLines();
        paintHorizontalLines();
    }

    private void initializeCanvas() {
        canvas.setWidth(getWorkingWidth());
        canvas.setHeight(getWorkingHeight());
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        printStat();
    }
    
    private int getMeasureWidth(){
        return resolution * 4 * tickWidth;
    }
    
    private void paintVerticalLines(){
        gc.setLineWidth(1);
        for(int x = 0; x < getWorkingWidth(); x+= get32ndsWidth()){
            gc.strokeLine(x, 0, x, canvas.getHeight());
        }
    }

    private void paintHorizontalLines(){
        gc.setLineWidth(1);
        for(int y = 0; y < canvas.getHeight(); y += getOctaveHeight()){
            gc.strokeLine(0, y, canvas.getWidth(), y);

        }
    }

    private int getOctaveHeight() {
        return (int) (canvas.getHeight() / (octaves * 12));
    }

    private int get32ndsWidth() {
        return getMeasureWidth() / 32;
    }

    private int getWorkingWidth() {
        return getMeasureWidth() * measureNum;
    }

    private int getWorkingHeight(){
        return octaves * lineHeight * 12;
    }

    private void printStat(){
        System.out.println("working width: " + getWorkingWidth());
        System.out.println("measure width: " + getMeasureWidth());
        System.out.println("32nd width: " + get32ndsWidth());
    }

//    private int convertToYCoordinate(int midiCode){
//
//    }
}
