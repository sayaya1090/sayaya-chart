package net.sayaya.ui.chart.column;

import elemental2.dom.HTMLElement;

@FunctionalInterface
public interface ColumnStyleFn<T> {
	T apply(HTMLElement td, int row, String prop, String value);
}
