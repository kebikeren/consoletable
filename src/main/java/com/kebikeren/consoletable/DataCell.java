package com.kebikeren.consoletable;

import com.kebikeren.consoletable.utils.MyStringUtils;
import com.kebikeren.consoletable.utils.Validation;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @Classname DataCell
 * @Description 单元格
 * @Date 2021-10-08
 * @Created by kebikeren
 */
public class DataCell {
    private Integer width; //单元格宽度
    private boolean isFixedWidth; //是否冻结宽度
    private List<String> lines;
    private Object item;
    private Integer group;
    private Integer span;
    private int padding;
    private int alignment; //-1:左对齐 0:居中 1:右对齐

    public DataCell() {
        this(null, null, null);
    }

    public DataCell(Object item, Integer group, Integer span) {
        this.item = item;
        padding = 0;
        alignment = -1;
        lines = new LinkedList<>();
        this.group = group;
        this.span = span;

        initItemWidth();
    }

    public Integer getGroup() {
        return group;
    }

    public Integer getSpan() {
        return span;
    }

    public List<String> getLines() {
        return lines;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }

    public boolean isFixedWidth() {
        return isFixedWidth;
    }

    public void setFixedWidth(boolean fixedWidth) {
        isFixedWidth = fixedWidth;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
        if (width != null)
            this.width = Validation.evenNumber(width);
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    private void initItemWidth() {
        if (group != null) {
            width = 0;
            return;
        }

        if (item == null) {
            item = "";
            width = 2;
            return;
        }

        if (item instanceof List) {
            List<String> list = (List<String>)item;
            if (list.size() == 0) {
                item = "";
                width = 2;
            } else {
                String maxString = Collections.max(list, (a, b) -> {
                    return MyStringUtils.getByteLength(a) - MyStringUtils.getByteLength(b);
                });
                int maxWidth = MyStringUtils.getByteLength(maxString);
                width = maxWidth + 2 * padding + 2;
            }
        } else if (item instanceof String) {
            int textWidth = MyStringUtils.getByteLength((String)item);
            width = textWidth + 2 * padding + 2;
        } else {
            item = "";
            width = 2;
        }
    }

    public void layout() {
        lines.clear();
        if (width == 0)
            return;

        if (item instanceof List) {
            for (String str : (List<String>)item) {
                int expectedTextWidth = width - 2 * padding - 2;
                MyStringUtils.breakString(str, expectedTextWidth, lines);
            }
        } else if (item instanceof String) {
            int expectedTextWidth = width - 2 * padding - 2;
            MyStringUtils.breakString((String)item, expectedTextWidth, lines);
        } else {
            lines.add("");
        }
    }

    public String toString(int index) {
        String ret = "";
        int contentWidth = width - 2;
        if (index >= lines.size())
            ret = StringUtils.repeat(' ', contentWidth);
        else {
            String s = StringUtils.repeat(' ', padding) + lines.get(index) + StringUtils.repeat(' ', padding);
            if (alignment == -1)
                ret = MyStringUtils.rightPad(s, contentWidth, ' ');
            else if (alignment == 0)
                ret = MyStringUtils.center(s, contentWidth, ' ');
            else
                ret = MyStringUtils.leftPad(s, contentWidth, ' ');
        }

        return ret;
    }

    @Override
    public String toString() {
        return item.toString();
    }
}
