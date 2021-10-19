package net.sayaya.ui.chart.function;

import elemental2.dom.HTMLTableCellElement;
import elemental2.dom.MouseEvent;
import jsinterop.annotations.JsFunction;
import net.sayaya.ui.chart.CellCoord;

@JsFunction
public interface MouseEventHandler {
	void apply(MouseEvent event, CellCoord coords, HTMLTableCellElement td);
}
