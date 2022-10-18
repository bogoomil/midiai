package hu.boga.midiai.gui.trackeditor;

import hu.boga.midiai.core.boundaries.dtos.NoteDto;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class NotesPainter {
    Pane graphicPane;
    int octaves = 8;
    int lineHeight = 20;
    int measureNum = 100;
    private int resolution = 120;
    private float zoomFactor = 1f;

    private List<NoteDto> notes;

    public NotesPainter(final Pane canvas) {
        this.graphicPane = canvas;
    }

    public void paintNotes(){
        this.graphicPane.getChildren().clear();
        initializeCanvas();
        paintVerticalLines();
        paintHorizontalLines();
        notes.forEach(noteDto -> {
            paintNote(noteDto);
        });
    }

    private void paintNote(NoteDto noteDto) {
        try {
            Rectangle rect = getNoteRectangle(noteDto);
            graphicPane.getChildren().add(rect);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private Rectangle getNoteRectangle(NoteDto noteDto) {
        Rectangle rect = new Rectangle();
        rect.setX((int) (noteDto.tick * getTickWidth()));
        rect.setY(getYByMidiCode((int) noteDto.midiCode));
        rect.setWidth(this.getTickWidth() * noteDto.lengthInTicks);
        rect.setHeight(getOctaveHeight());
        rect.setStroke(Color.ALICEBLUE);
        return rect;
    }

    private void initializeCanvas() {
        graphicPane.setPrefWidth(getWorkingWidth());
        graphicPane.setPrefHeight(getWorkingHeight());
        graphicPane.getChildren().removeAll();
//        printStat();
    }

    private void paintVerticalLines(){
        int w32nds = get32ndsWidth();
        int counter = 0;
        for(int x = 0; x < getWorkingWidth(); x+= w32nds){

            Line line = new Line();
            line.setStartX(x);
            line.setStartY(0);
            line.setEndX(x);
            line.setEndY(graphicPane.getPrefHeight());
            if(counter % 32 == 0 ){
                line.setStroke(Color.RED);
            } else{
                line.setStroke(Color.BLACK);
            }
            counter++;

            graphicPane.getChildren().addAll(line);
        }
    }

    private void paintHorizontalLines(){
        for(int y = 0; y < graphicPane.getPrefHeight(); y += getOctaveHeight()){

            Line line = new Line();
            line.setStartX(0);
            line.setStartY(y);
            line.setEndX(graphicPane.getPrefWidth());
            line.setEndY(y);

            graphicPane.getChildren().add(line);

        }
    }

    private int getOctaveHeight() {
        return (int) (graphicPane.getPrefHeight() / (octaves * 12));
    }

    private int get32ndsWidth() {
        return (int) (((resolution * 4) / 32) * zoomFactor);
    }

    private int getWorkingWidth() {
        return (int) (getMeasureWidth() * measureNum);
    }

    private double getMeasureWidth() {
        return get32ndsWidth() * 32;
    }

    private int getWorkingHeight(){
        return octaves * lineHeight * 12;
    }

    private double getTickWidth() {
        return getMeasureWidth() / (resolution * 4);
    }

    private int getYByMidiCode(int midiCode){
        return (int) (graphicPane.getPrefHeight() - (getOctaveHeight() * midiCode));
    }

    public void setZoomFactor(float zoomFactor){
        this.zoomFactor = zoomFactor;
    }

    public void setNotes(List<NoteDto> notes){
        this.notes = notes;
    }

    public void setResolution(int resolution){
        this.resolution = resolution;
    }

}
