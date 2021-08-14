package net.sayaya.ui.chart.column;

import net.sayaya.ui.chart.Column;

@FunctionalInterface
public interface ColumnBuilder {
	Column build();

	static ColumnString string(String id) {
		return new ColumnString(id);
	}
	static ColumnCheckBox checkbox(String id) {
		return new ColumnCheckBox(id);
	}
}
