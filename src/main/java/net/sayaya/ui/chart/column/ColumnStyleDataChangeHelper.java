package net.sayaya.ui.chart.column;

import elemental2.dom.HTMLElement;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;

import java.util.function.Supplier;

public final class ColumnStyleDataChangeHelper<SELF> {
	private final Supplier<SELF> _self;
	public ColumnStyleDataChangeHelper(Supplier<SELF> columnBuilder) {
		_self = columnBuilder;
	}
	public HTMLElement apply(SheetElement.Handsontable instance, HTMLElement td, int row, String prop) {
		Data data = instance.spreadsheet.values()[row];
		if(data!=null && data.isChanged(prop)) td.classList.add("changed");
		else td.classList.remove("changed");
		return td;
	}
	private SELF that() {
		return _self.get();
	}
}
