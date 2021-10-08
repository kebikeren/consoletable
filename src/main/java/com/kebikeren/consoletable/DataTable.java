package com.kebikeren.consoletable;

import com.kebikeren.consoletable.inner.IndexValue;
import com.kebikeren.consoletable.inner.Seperator;
import com.kebikeren.consoletable.utils.Validation;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @Classname DataTable
 * @Description TODO
 * @Date 2021-10-08
 * @Created by kebikeren
 */
public class DataTable {
    private List<DataRow> rows;
    private Integer width;
    private Integer[] widths;
    private boolean normal; //normal 是否标准表格 标准表格是指表里大部分行的列都是跟其他行的列对齐的(可能跨列，但是对齐)
    private String caption;

    public DataTable(boolean normal) {
        this(null, null, normal);
    }

    public DataTable(String caption, boolean normal) {
        this(caption, null, normal);
    }

    public DataTable(int width, boolean normal) {
        this(null, width, normal);
    }

    public DataTable(String caption, Integer width, boolean normal) {
        rows = new LinkedList<>();
        this.caption = caption;
        this.width = width;
        this.normal = normal;
    }

    public Integer[] getWidths() {
        return widths;
    }

    public void setWidths(Integer[] widths) {
        this.widths = Arrays.copyOf(widths, widths.length);
    }


    public List<DataRow> getRows() {
        return rows;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
        if (width != null)
            this.width = Validation.evenNumber(width);
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        caption = caption;
    }

    public DataRow newRowStandard() {
        DataRow row = new DataRow(this, false);

        return row;
    }

    public DataRow newRowSpecial() {
        DataRow row = new DataRow(this, true);

        return row;
    }

    public DataRow newRowAndAddSpecial() {
        DataRow row = newRowSpecial();
        rows.add(row);

        return row;
    }

    public DataRow newRowAndAddStandard() {
        DataRow row = newRowStandard();
        rows.add(row);

        return row;
    }

    private void layout() {
        if (normal) {
            layoutNormal();
            return;
        }

        for (DataRow row : rows) {
            row.autoAdjustCellsWidth();
            row.adjustSelfWidth();
            for (DataCell cell : row.getCells())
                cell.layout();
        }
    }

    private void layoutNormal() {
        if (rows.size() == 0)
            return;

        //计算出列数
        int columnSize = 0;
        for (DataRow r : rows) {
            if (r.getGroupNum() == null) {
                columnSize = r.getCells().size();
                break;
            }
        }

        //找出需要动态调整宽度的列集合
        List<IndexValue> neededList = new LinkedList<>();
        int fixedWidthSize = 0; //固定宽度的列的总宽度
        Set<Integer> notFixedWidthIndexs = new HashSet<>();
        Set<Integer> fixedWidthIndexs = new HashSet<>();
        int[] widths = new int[columnSize]; //各个列宽度
        for (int i = 0; i < widths.length; i++)
            widths[i] = 0;
        for (DataRow r : rows) {
            if (r.isSpecial())
                continue;

            r.adjustCellsWidth();
            for (int i = 0; i < r.getCells().size(); i++) {
                DataCell cell = r.getCells().get(i);
                if (cell.getSpan() != null)
                    continue;

                if (!cell.isFixedWidth())
                    notFixedWidthIndexs.add(i);
                else
                    fixedWidthIndexs.add(i);

                if (cell.getWidth() != null && cell.getWidth() > widths[i])
                    widths[i] = cell.getWidth();
            }
        }

        for (int i : fixedWidthIndexs)
            fixedWidthSize += widths[i];
        for (int i : notFixedWidthIndexs) {
            IndexValue indexValue = new IndexValue(i, widths[i]);
            neededList.add(indexValue);
        }

        //如果有tablewidth约束，则动态调整widths
        if (this.width != null && neededList.size() > 0 && fixedWidthSize < this.width) {
            IndexValue[] arr = new IndexValue[neededList.size()];
            neededList.toArray(arr);
            autoAjustWidth(arr, this.width - fixedWidthSize);
            for (IndexValue item : arr)
                widths[item.getIndex()] = item.getValue();
        }

        //给每行各列设置宽度
        for (DataRow r : rows) {
            if (r.isSpecial()) {
                r.autoAdjustCellsWidth();
            } else {
                if (r.getGroupNum() != null) {
                    for (int i = 0; i < r.getCells().size(); i++) {
                        DataCell cell = r.getCells().get(i);
                        if (cell.getGroup() == null)
                            cell.setWidth(widths[i]);
                        else {
                            int temp = 0;
                            for (int j = i; j < i + cell.getSpan(); j++) {
                                temp += widths[j];
                            }

                            cell.setWidth(temp);
                            i += cell.getSpan() - 1;
                        }
                    }
                } else {
                    for (int i = 0; i < r.getCells().size(); i++) {
                        DataCell cell = r.getCells().get(i);
                        cell.setWidth(widths[i]);
                    }
                }
            }

            r.layout();
        }

        //剔除宽度为0的cell
        for (DataRow r : rows) {
            for (int i = r.getCells().size() - 1; i > 0; i--) {
                DataCell cell = r.getCells().get(i);
                if (cell.getWidth() == 0)
                    r.getCells().remove(i);
            }
        }

        for (DataRow r : rows)
            r.adjustSelfWidth();

        this.widths = new Integer[widths.length];
        for (int i = 0; i < widths.length; i++)
            this.widths[i] = widths[i];
    }

    @Override
    public String toString() {
        if (rows.size() == 0)
            return "";

        layout();

        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isEmpty(caption)) {
            sb.append(" " + caption);
            sb.append(System.lineSeparator());
        }

        DataRow preRow = null;
        for (DataRow row : rows) {
            //上边框
            if (preRow == null)
                drawTopLine(sb, row);
            else
                drawMiddleLine(sb, preRow, row);

            //换行
            sb.append(System.lineSeparator());

            //行内容
            String content = row.toString();
            sb.append(content);

            //换行
            if (!StringUtils.isEmpty(content))
                sb.append(System.lineSeparator());

            preRow = row;
        }

        //最后底边框
        drawBottomLine(sb, rows.get(rows.size() - 1));

        return sb.toString();
    }

    private void drawMiddleLine(StringBuilder sb, DataRow preRow, DataRow curRow) {
        sb.append(Seperator.LeftJoint);

        drawMiddleLineNormal(sb, preRow, curRow);

        int preRowWidth = preRow.getWidth();
        int curRowWidth = curRow.getWidth();
        if (preRowWidth == curRowWidth)
            sb.append(Seperator.RightJoint);
        else if (preRowWidth < curRowWidth)
            sb.append(Seperator.TopRightJoint);
        else
            sb.append(Seperator.BottomRightJoint);
    }

    private void drawMiddleLineNormal(StringBuilder sb, DataRow preRow, DataRow curRow) {
        int preRowWidth = (preRow.getWidth() - 2) / 2;
        int curRowWidth = (curRow.getWidth() - 2) / 2;
        int count = Math.max(preRowWidth, curRowWidth);
        for (int i = 0; i < count; i++) {
            boolean pre = preRow.isSeperator(i);
            boolean cur = curRow.isSeperator(i);
            if (!pre && !cur)
                sb.append(Seperator.HorizontalLine);
            else if (pre && !cur)
                sb.append(Seperator.BottomJoint);
            else if (!pre && cur)
                sb.append(Seperator.TopJoint);
            else
                sb.append(Seperator.MiddleJoint);
        }
    }

    private void drawBottomLine(StringBuilder sb, DataRow row) {
        sb.append(Seperator.BottomLeftJoint);
        if (row.getCells().size() == 0) {
            sb.append(StringUtils.repeat(Seperator.HorizontalLine, (row.getWidth() - 2) / 2));
            sb.append(Seperator.BottomRightJoint);
        } else {
            int n = row.getCells().size();
            for (int i = 0; i < n; i++) {
                DataCell cell = row.getCells().get(i);
                sb.append(StringUtils.repeat(Seperator.HorizontalLine, (cell.getWidth() - 2) / 2));
                if (i < n - 1)
                    sb.append(Seperator.BottomJoint);
                else
                    sb.append(Seperator.BottomRightJoint);
            }
        }
    }

    private void drawTopLine(StringBuilder sb, DataRow row) {
        sb.append(Seperator.TopLeftJoint);
        if (row.getCells().size() == 0) {
            sb.append(StringUtils.repeat(Seperator.HorizontalLine, (row.getWidth() - 2) / 2));
            sb.append(Seperator.TopRightJoint);
        } else {
            int n = row.getCells().size();
            for (int i = 0; i < n; i++) {
                DataCell cell = row.getCells().get(i);
                sb.append(StringUtils.repeat(Seperator.HorizontalLine, (cell.getWidth() - 2) / 2));
                if (i < n - 1)
                    sb.append(Seperator.TopJoint);
                else
                    sb.append(Seperator.TopRightJoint);
            }
        }
    }

    //input里的元素动态扩张或缩短
    public static void autoAjustWidth(IndexValue[] input, int width) {
        int sum = 0;
        for (IndexValue item : input)
            sum += item.getValue();

        if (sum == width)
            return;

        Arrays.sort(input, (o1, o2) -> o2.getValue() - o1.getValue());

        int count = Math.abs(width - sum);
        for (IndexValue item : input)
        item.setRatio((double)item.getValue() / sum);

        int i = 0;
        while (width != sum) {
            int pad = (int)(input[i].getRatio() * count);
            if (pad == 0)
                pad = 2;
            pad = pad % 2 == 0 ? pad : pad + 1;
            pad = width - sum > 0 ? pad : -pad;

            input[i].setValue(input[i].getValue() + pad);
            sum += pad;

            i = (i + 1) % input.length;
            count = Math.abs(width - sum);
        }
    }
}
