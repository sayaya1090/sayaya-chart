package net.sayaya.ui.chart.column;

import elemental2.dom.HTMLTableCellElement;

@FunctionalInterface
public interface ColumnStyleFn<T> {
	T apply(HTMLTableCellElement td, int row, String prop, String value);
}
