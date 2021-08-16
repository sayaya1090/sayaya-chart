package net.sayaya.ui.chart.column;

import elemental2.dom.HTMLElement;

public interface ColumnStyleHelper {
	HTMLElement apply(HTMLElement td, int row, String prop, String value);
}
