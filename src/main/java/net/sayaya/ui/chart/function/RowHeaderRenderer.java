package net.sayaya.ui.chart.function;

import elemental2.dom.HTMLTableCellElement;
import jsinterop.annotations.JsFunction;

@JsFunction
public interface RowHeaderRenderer {
	HTMLTableCellElement render(int row, HTMLTableCellElement th);
}
