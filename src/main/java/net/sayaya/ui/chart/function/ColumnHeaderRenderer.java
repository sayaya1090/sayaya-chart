package net.sayaya.ui.chart.function;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTableCellElement;
import jsinterop.annotations.JsFunction;

@JsFunction
public interface ColumnHeaderRenderer {
	HTMLTableCellElement render(int col, HTMLTableCellElement th);
}
