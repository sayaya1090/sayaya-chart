package net.sayaya.ui.chart.column;

import net.sayaya.ui.ListElement;
import net.sayaya.ui.chart.Column;

@FunctionalInterface
public interface ColumnBuilder {
	Column build();

	static ColumnString string(String id) { return new ColumnString(id); }
	static ColumnText text(String id, int minHeight, int maxHeight) {
		return new ColumnText(id, minHeight, maxHeight);
	}
	static ColumnNumber number(String id) { return new ColumnNumber(id); }
	static ColumnDate date(String id) { return new ColumnDate(id); }
	static ColumnCheckBox checkbox(String id) {
		return new ColumnCheckBox(id);
	}
	static ColumnDropDown dropdown(String id, ListElement<ListElement.SingleLineItem> list) {
		return new ColumnDropDown(id, list);
	}
}
