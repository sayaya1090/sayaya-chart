package net.sayaya.ui.chart.column;

import elemental2.dom.HTMLTableCellElement;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;

import java.util.function.Supplier;

public final class ColumnStyleDataChangeHelper<SELF> {
	private final Supplier<SELF> _self;
	ColumnStyleDataChangeHelper(Supplier<SELF> columnBuilder) {
		_self = columnBuilder;
	}
	HTMLTableCellElement apply(SheetElement.Handsontable instance, HTMLTableCellElement td, int row, String prop) {
		Data data = instance.spreadsheet.values()[row];
		if(data!=null && data.isChanged(prop)) td.classList.add("changed");
		return td;
	}
	private SELF that() {
		return _self.get();
	}
}
