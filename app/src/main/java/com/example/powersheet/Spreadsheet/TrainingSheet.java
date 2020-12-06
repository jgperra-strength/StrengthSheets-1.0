package com.example.powersheet.Spreadsheet;

import com.example.powersheet.Training.Block;
import com.example.powersheet.Training.Day;
import com.example.powersheet.Training.Exercise;
import com.example.powersheet.Training.ExerciseAttribute;
import com.example.powersheet.Training.Week;
import com.google.android.gms.common.util.NumberUtils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TrainingSheet extends Sheet {
    private final int LEFTBOUND = 2;
    private final int BLOCKHEIGHT = 0;
    private final int WEEKHEIGHT = 2;
    private final int EXTRAWEEKSPACING = 2;
    private final int DAYHEIGHT = 5;
    private final int EXTRADAYSPACING = 2;

    private ArrayList<Block> blocks;

    public TrainingSheet(List<List<Object>> c, String st, HashMap<String, String> metadata) {
        super(c, st, metadata);
        blocks = new ArrayList<>();
        convertFromCellFormat();
    }

    public TrainingSheet() {
        blocks = new ArrayList<>();
    }

    private void setDimensions() {
        if (metadata.containsKey("dimensions")) {
                String dimensionsValue = metadata.get("dimensions");
                String[] dBlocks = dimensionsValue.split(":");
                for (int i = 0; i < dBlocks.length; ++i) {
                    blocks.add(new Block(i));
                    String block = dBlocks[i];
                    System.out.println("BLOCK " + (i + 1) + " = " + block);
                    String[] weeks = block.split(" ");
                    for (int j = 0; j < weeks.length; ++j) {
                        blocks.get(i).addWeek(new Week(j, blocks.get(i)));
                        String week = weeks[j];
                        System.out.println("WEEK " + (j + 1) + " = " + week);
                        String[] days = week.split(",");
                        for (int k = 0; k < days.length; ++k) {
                            if (k == days.length - 1) {
                                System.out.println("COLUMNS = " + days[k]);
                                blocks.get(i).getWeeks().get(j).setNumCols(Integer.parseInt(days[k]));
                            }
                            else {
                                blocks.get(i).getWeeks().get(j).addDay(new Day(Integer.parseInt(days[k]), k, blocks.get(i).getWeeks().get(j)));
                                String day = days[k];
                                System.out.println("Day " + (k + 1) + " = " + day);
                            }
                        }
                    }
                }
            }
    }

    private void convertFromCellFormat() {
        //printCells();
        setDimensions();

        int horizontalIdx = LEFTBOUND;
        int verticalIdx;

        for (int b = 0; b < blocks.size(); ++b) {
            verticalIdx = BLOCKHEIGHT;
            Block curBlock = blocks.get(b);
            curBlock.setBlockTitle(cells.get(verticalIdx).get(horizontalIdx).getValue());

            for (int w = 0; w < curBlock.getWeeks().size(); ++w) {
                verticalIdx = WEEKHEIGHT;
                Week curWeek = blocks.get(b).getWeeks().get(w);
                curWeek.setTitle(cells.get(verticalIdx).get(horizontalIdx).getValue());

                verticalIdx = DAYHEIGHT;
                //read in attribute columns
                int numCols = curWeek.getNumCols();
                String cols[] = new String[numCols];
                for (int c = 0; c < numCols; ++c) {
                    cols[c] = cells.get(verticalIdx).get(horizontalIdx + c + 1).getValue();
                }

                for (int d = 0; d < curWeek.getDays().size(); ++d) {
                    Day curDay = curWeek.getDays().get(d);
                    curDay.setDayTitle(cells.get(verticalIdx).get(horizontalIdx).getValue());

                    for (int e = 0; e < curDay.getNumExercises(); ++e) {
                        System.out.println(cells.get(verticalIdx + e + 1).get(horizontalIdx).getValue());
                        Exercise exercise = new Exercise(cells.get(verticalIdx + e + 1).get(horizontalIdx).getValue());
                        for (int a = 0; a < numCols; ++a) {
                            String colVal = cells.get(verticalIdx + e + 1).get(horizontalIdx + a + 1).getValue();
                            if (!colVal.isEmpty()) {
                                exercise.getAttributes().add(new ExerciseAttribute(cols[a], colVal.equals("-") ? "" : colVal));
                            }
                        }
                        curDay.addExercise(exercise);
                    }
                    verticalIdx += curDay.getNumExercises() + EXTRADAYSPACING;
                }
                horizontalIdx += curWeek.getNumCols() + EXTRAWEEKSPACING;
            }
        }

       /* System.out.println("CELLS = " + cells.get(5).size());
        int row = height + 1;
        int col = leftBound;
        for (int b = 0; b < numBlocks; ++b) {
            System.out.println("NUM BLOCKS = " + b);
            Block bl = new Block("Test Block", 4, b);
            for (int w = 0; w < bl.getNumWeeks(); ++w) {
                System.out.println("NUM WEEKS = " + w);
                Week wk = new Week("Week " + (w+1), null, 3, w, bl);
                for (int d = 0; d < wk.getNumDays(); ++d) {
                    System.out.println("NUM Days = " + d);
                    Day day = new Day("Day " + (d + 1), 3, d, wk);
                    for (int e = 0; e < day.getNumExercises(); ++e) {
                        System.out.println("NUM Ex = " + e);
                        // System.out.println(cells.get(row + e).get(col).getValue());
                        // System.out.println(NumberUtils.isNumeric(cells.get(row + e).get(col + 1).getValue()) ? Integer.parseInt(cells.get(row + e).get(col + 1).getValue()) : 0);
                        // System.out.println(NumberUtils.isNumeric(cells.get(row + e).get(col + 1).getValue()) ? Integer.parseInt(cells.get(row + e).get(col + 2).getValue()) : 0);
                        Exercise ex = new Exercise(cells.get(row + e).get(col).getValue(),
                                NumberUtils.isNumeric(cells.get(row + e).get(col + 1).getValue()) ? Integer.parseInt(cells.get(row + e).get(col + 1).getValue()) : 0,
                                NumberUtils.isNumeric(cells.get(row + e).get(col + 1).getValue()) ? Integer.parseInt(cells.get(row + e).get(col + 2).getValue()) : 0,
                                NumberUtils.isNumeric(cells.get(row + e).get(col + 1).getValue()) ? Integer.parseInt(cells.get(row + e).get(col + 3).getValue()) : 0,
                                cells.get(row + e).get(col + 4).getValue());

                        day.addExercise(ex);

                        System.out.println(ex.toString() + row + " " + col);

                    }
                    wk.addDay(day);
                    System.out.println("ROW = " + row);
                    row += day.getNumExercises() + 1;
                    //++row;
                }
                bl.addWeek(wk);
                //col += wk.getNumDays();
                col += horizontalSpacing;
                row = height + 1;
            }
            blocks.add(bl);
        }*/
    }

    private void convertToCellFormat() {
        // Calculate dimensions of the sheet and initialize
        // the 2D array that represents it.
        Integer[] sheetDimensions = createDimensions();

        int numCols = sheetDimensions[0];
        int numRows = sheetDimensions[1];

        List<List<Object>> values = new ArrayList<>();
        for (int i = 0; i < numRows; ++i) {
            List<Object> row = new ArrayList<>();
            for (int j = 0; j < numCols; ++j) {
                row.add((Object) "");
            }
            values.add(row);
        }

        int horizontalIdx = 0;
        int verticalIdx = 0;

        for (Block block : blocks) {

            for (Week week : block.getWeeks()) {

                for (Day day : week.getDays()) {

                }
            }
        }

    }

    private String calculateWeekRange(int blockIdx, int weekIdx) {
        String range = "";

        int leftBoundX = LEFTBOUND;
        int leftBoundY = WEEKHEIGHT;

        int rightBoundX = 0;
        int rightBoundY = DAYHEIGHT;

        // Calculate height of week, which is right bound Y
        Week curWeek = blocks.get(blockIdx).getWeeks().get(weekIdx);
        for (Day day : curWeek.getDays()) {
            rightBoundY += day.getNumExercises() + 2;
        }

        // Calculate left and right bound X of range by iterating through
        // the entire sheet.
        int bIdx = 0;
        for (Block block : blocks) {
            int wIdx = 0;
            for (Week week : block.getWeeks()) {
                if (bIdx == blockIdx && wIdx == weekIdx) {
                    rightBoundX = leftBoundX + week.getNumCols();
                    range = getSheetRange(leftBoundX, leftBoundY, rightBoundX, rightBoundY);
                    System.out.println("WEEK " + weekIdx + " RANGE OF BLOCK " + blockIdx + " = " + range);
                    return range;
                }

                leftBoundX += week.getNumCols() + 2;
                ++wIdx;
            }
            ++bIdx;
        }

        return range;
    }

    // Create dimensions that will be used in metadata of sheet.
    // When a sheet is read, these dimensions are used to convert
    // from cell format to data structures. Return the number of
    // rows and columns needed.
    private Integer[] createDimensions() {
        String dimensions = "";

        // numCols = sumOfWeekWidths + numWeeks * 2 - 1
        int sumOfWeekWidths = 0;
        int numWeeks = 0;

        // numRows = max(numWeeklyExercises + numberOfDays * 2 - 1)
        int numRows = 0;

        for (Block block : blocks) {
            int numWeeklyExercises = 0;
            int numberOfDays = 0;
            for (Week week : block.getWeeks()) {
                for (Day day : week.getDays()) {
                    dimensions += day.getExercises().size() + ",";
                    numWeeklyExercises += day.getExercises().size();
                    ++numberOfDays;
                }
                dimensions += week.getNumCols() + " ";


                sumOfWeekWidths += week.getNumCols();
                ++numWeeks;

                numRows = Math.max(numRows, numWeeklyExercises + numberOfDays * 2 - 1);
            }
            dimensions += ":";
        }

        int numCols = sumOfWeekWidths + numWeeks * 2 - 1;

        metadata.put("dimensions", dimensions);

        Integer[] sheetDimensions = { numCols, numRows };

        return sheetDimensions;
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public ArrayList<ArrayList<Cell>> getSheetData() {
        return super.cells;
    }
}

   /* public void serialize(String fileName) {
        System.out.println("SERIALIZING");
        //Serialize sheet metadata
        /*ArrayList<Cell> metadata = new ArrayList<>();
        for (int i = 0; i < 5; ++i) {
           String val = "";
           switch (i) {
               case 0:
                   val = title;
                   break;
               case 1:
                   val = Integer.toString(height);
                   break;
               case 2:
                   val = Integer.toString(leftBound);
                   break;
               case 3:
                   val = Integer.toString(horizontalSpacing);
                   break;
               case 4:
                   val = Integer.toString(numBlocks);
                   break;
           }
           metadata.add(new Cell(-1,-1,val));
        }

        cells.add(metadata);*/

        /*try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            //out.writeObject(cells);
            out.writeObject(this);
            out.close();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        //if (!cells.isEmpty()) cells.remove(cells.size() - 1);
    }

    public void deserialize(String fileName) {
        System.out.println("DESERIALIZING");
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(fis);
            //cells = (ArrayList<ArrayList<Cell>>) in.readObject();
             //= (TrainingSheet) in.readObject();
            in.close();
            fis.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
/*
        //deserialize metadata
        if (!cells.isEmpty()) {
            ArrayList<Cell> metadata = cells.get(cells.size() - 1);
            for (int i = 0; i < 5; ++i) {
                switch (i) {
                    case 0:
                        title = metadata.get(i).getValue();
                        break;
                    case 1:
                        height = Integer.parseInt(metadata.get(i).getValue());
                        break;
                    case 2:
                        leftBound = Integer.parseInt(metadata.get(i).getValue());
                        break;
                    case 3:
                        horizontalSpacing = Integer.parseInt(metadata.get(i).getValue());
                        break;
                    case 4:
                        numBlocks = Integer.parseInt(metadata.get(i).getValue());
                        break;
                }
            }
            cells.remove(cells.size() - 1);

            convertFromCellFormat();
        }
    }
}*/
