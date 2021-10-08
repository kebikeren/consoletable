package com.kebikeren.consoletable;

import com.kebikeren.consoletable.inner.IndexValue;
import com.kebikeren.consoletable.inner.Seperator;
import com.kebikeren.consoletable.utils.Validation;

import java.util.*;

/**
 * @Classname DataRow
 * @Description TODO
 * @Date 2021-10-08
 * @Created by kebikeren
 */
public class DataRow {
    private List<DataCell> cells;
    private Integer width;
    private DataTable table;
    private Integer groupNum;
    private boolean isSpecial;
    private Integer[] widths;

    public DataRow(DataTable table, boolean isSpecial) {
        this.table = table;
        cells = new LinkedList<>();
        this.isSpecial = isSpecial;
    }

    public Integer getGroupNum() {
        return groupNum;
    }

    public Integer[] getWidths() {
        return widths;
    }

    public void setWidths(Integer... widths) {
        this.widths = new Integer[widths.length];
        for (int i = 0; i < widths.length; i++) {
            this.widths[i] = widths[i];
        }
    }

    public DataTable getTable() {
        return table;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
        if (width != null)
            this.width = Validation.evenNumber(width);
    }

    public List<DataCell> getCells() {
        return cells;
    }

    //当前位置是否是分隔符
    public boolean isSeperator(int index) {
        if (cells.size() == 0)
            return false;
        else {
            int count = 0;
            int size = cells.size();
            for (int i = 0; i < size; i++) {
                DataCell cell = cells.get(i);
                count += (cell.getWidth() - 2) / 2;
                if (index == count + i)
                    return true;
                else if (index < count + i)
                    return false;
            }
        }

        return false;
    }



    public void add(Object... items) {
        for (Object item : items) {
            DataCell cell = new DataCell(item, null, null);
            cells.add(cell);
        }
    }

    public void addWidthSpan(Object item, int span) {
        if (groupNum == null)
            groupNum = 0;

        for (int i = 0; i < span; i++) {
            DataCell cell = new DataCell(item, groupNum, span);
            cells.add(cell);
        }
        groupNum++;
    }

    public void layout() {
        for (DataCell cell : cells)
            cell.layout();
    }

    public void adjustSelfWidth() {
        if (cells.size() == 0)
            return;

        width = 0;
        for (DataCell cell : cells)
            width += cell.getWidth();
    }

    public void autoAdjustCellsWidth() {
        adjustCellsWidth();

        //找出需要动态调整宽度的列集合
        List<IndexValue> neededList = new LinkedList<>();
        int fixedWidthSize = 0; //固定宽度的列的总宽度
        Set<Integer> notFixedWidthIndexs = new HashSet<>();
        Set<Integer> fixedWidthIndexs = new HashSet<>();
        int[] widths = new int[cells.size()]; //各个列宽度
        for (int i = 0; i < widths.length; i++)
            widths[i] = 0;
        for (int i = 0; i < widths.length; i++) {
            DataCell cell = cells.get(i);

            if (!cell.isFixedWidth())
                notFixedWidthIndexs.add(i);
            else
                fixedWidthIndexs.add(i);

            if (cell.getWidth() != null && cell.getWidth() > widths[i])
                widths[i] = cell.getWidth();
        }

        for (Integer i : fixedWidthIndexs)
            fixedWidthSize += widths[i];
        for (Integer i : notFixedWidthIndexs) {
            IndexValue indexValue = new IndexValue(i, widths[i]);
            neededList.add(indexValue);
        }

        //如果有tablewidth约束，则动态调整widths
        int width = -1;
        if (this.width != null)
            width = this.width;
        else if (table.getWidth() != null)
            width = table.getWidth();
        if (neededList.size() > 0 && fixedWidthSize < width) {
            IndexValue[] arr = new IndexValue[neededList.size()];
            neededList.toArray(arr);
            DataTable.autoAjustWidth(arr, width - fixedWidthSize);
            for (IndexValue item : arr)
                widths[item.getIndex()] = item.getValue();
        }

        for (int i = 0; i < widths.length; i++)
            cells.get(i).setWidth(widths[i]);
    }

    public void adjustCellsWidth() {
        if (cells.size() == 0)
            return;

        Integer[] widths = this.widths;
        if (widths == null)
            widths = table.getWidths();

        if (widths != null) {
            for (int i = 0; i < cells.size(); i++) {
                DataCell cell = cells.get(i);
                if (i < widths.length && widths[i] != null && cell.getGroup() == null) {
                    cell.setWidth(widths[i]);
                    cell.setFixedWidth(true);
                }

            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (cells.size() == 0) {
            return "";
        } else {
            int maxLine = getMaxLine();
            for (int i = 0; i < maxLine; i++) {
                sb.append(Seperator.VerticalLine);
                for (DataCell cell : cells) {
                    sb.append(cell.toString(i));
                    sb.append(Seperator.VerticalLine);
                }

                if (i < maxLine - 1)
                    sb.append(System.lineSeparator());
            }
        }

        return sb.toString();
    }

    private int getMaxLine() {
        int maxLine = 0;
        for (DataCell cell : cells) {
            if (cell.getLines().size() > maxLine)
                maxLine = cell.getLines().size();
        }

        return maxLine;
    }
}
