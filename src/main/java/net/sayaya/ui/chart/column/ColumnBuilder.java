package net.sayaya.ui.chart.column;

import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.dom.MdSelectOptionElement;

import java.util.function.Function;

@FunctionalInterface
public interface ColumnBuilder {
	Column build();

	static ColumnString string(String id) { return new ColumnString(id); }
	static ColumnText text(String id, int minHeight, int maxHeight) {
		return new ColumnText(id, minHeight, maxHeight);
	}
	static ColumnLink link(String id, Function<Data, String> url) {
		return new ColumnLink(id, url);
	}
	static ColumnNumber number(String id) { return new ColumnNumber(id); }
	static ColumnDate date(String id) { return new ColumnDate(id); }
	static ColumnCheckBox checkbox(String id) {
		return new ColumnCheckBox(id);
	}
	static ColumnDropDown dropdown(String id, String... list) {
		return new ColumnDropDown(id, list);
	}
	static ColumnChip chip(String id) {
		return new ColumnChip(id);
	}
}
