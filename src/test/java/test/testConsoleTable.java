package test;

import com.kebikeren.consoletable.DataRow;
import com.kebikeren.consoletable.DataTable;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Classname testConsoleTable
 * @Description TODO
 * @Date 2021-10-08
 * @Created by kebikeren
 */
public class testConsoleTable {
    @Test
    public void standard() {
        DataTable dataTable = new DataTable(80, true);
        dataTable.newRowAndAddStandard().add("主要指标(元)", "18-06-30", "17-12-31", "17-06-30", "16-12-31", "16-06-30");
        dataTable.newRowAndAddStandard().add("每股收益", null, "1.2400", "0.9400", "2.1800", "0.1800");

        List<String> list1 = new LinkedList<>();
        list1.add("上市日期:2015-12-07");
        list1.add("主办券商:建建建建投证券股份有限公司");
        list1.add("电话:86-756-3328323");

        List<String> list2 = new LinkedList<>();
        list2.add("总股本(万):55411.21");
        list2.add("流通股(万):11137.25");
        list2.add("B股(万):5.00");
        list2.add("限售流通股(万):166.32");

        List<String> list3 = new LinkedList<>();
        list3.add("法人:张三");
        list3.add("总经理:王五");
        list3.add("行业:零售业");

        DataRow dr = dataTable.newRowAndAddStandard();

        dr.addWidthSpan(list1, 2);
        dr.addWidthSpan(list2, 2);
        dr.addWidthSpan(list3, 2);

        dr = dataTable.newRowAndAddSpecial();
        dr.add("主营范围", "从事互联网文化活动；技术服务、技术转让、技术开发、技术推广、技术咨询；软件开发；电脑动画设计");
        dr.setWidths(10);

        System.out.println(dataTable);
    }

    @Test
    public void special() {
        DataTable dataTable = new DataTable(80, false);
        Integer[] widths1 = new Integer[2];
        widths1[0] = 10;
        widths1[1] = null;

        Integer[] widths2 = new Integer[4];
        widths2[0] = 10;
        widths2[1] = (int)Math.ceil(80 / 3.0);
        widths2[2] = 10;
        widths2[3] = null;

        Integer[] widths3 = new Integer[4];
        widths3[0] = 10;
        widths3[1] = (int)Math.ceil(80 / 3.0);
        widths3[2] = 14;
        widths3[3] = null;

        Integer[] widths4 = new Integer[4];
        widths4[0] = 18;
        widths4[1] = widths3[0] + widths3[1] - widths4[0];
        widths4[2] = 20;
        widths4[3] = null;

        Integer[] widths5 = new Integer[2];
        widths5[0] = 18;
        widths5[1] = null;

        Map<String, String> data = new HashMap<>();
        data.put("公司名称", "某某公司");
        data.put("证券简称", "某某证券");
        data.put("证券代码", "700000");
        data.put("董    秘", "未知");
        data.put("证券事务代表", "未知");

        specialInner(dataTable, data, widths1, "公司名称");
        specialInner(dataTable, data, widths2, "证券简称", "证券代码");
        specialInner(dataTable, data, widths2, "证券类型", "上市日期");
        specialInner(dataTable, data, widths3, "董    秘", "证券事务代表");
        specialInner(dataTable, data, widths4, "注册资本(万)", "上市初总股本(万)");
        specialInner(dataTable, data, widths4, "最新流通股本(万)", "上市初流通股本(万)");
        specialInner(dataTable, data, widths5, "会计事务所(境内)");

        System.out.println(dataTable);
    }

    private void specialInner(DataTable table, Map<String, String>data, Integer[] widths, String... items) {
        DataRow row = table.newRowAndAddSpecial();
        for (String item : items) {
            String value = "";
            if (data.containsKey(item))
                value = data.get(item);
            row.add(item, value);
        }

        row.setWidths(widths);
    }
}
