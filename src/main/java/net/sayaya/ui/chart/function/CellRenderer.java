package net.sayaya.ui.chart.function;

import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTableCellElement;
import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.SheetElement;

@JsFunction
public interface CellRenderer {
	HTMLElement render(SheetElement.Handsontable instance, HTMLTableCellElement td, int row, int col, String prop, String value, Column columnInfo);
	@JsOverlay
	default String getFont() {
		return "'Montserrat', 'Noto Sans KR', sans-serif";
	}
	@JsOverlay
	default int getFontSize() {
		return 12;
	}
}
