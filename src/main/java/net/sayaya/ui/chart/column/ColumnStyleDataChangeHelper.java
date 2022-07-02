package net.sayaya.ui.chart.column;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLTableCellElement;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.SheetElement;

import java.util.function.Supplier;

public final class ColumnStyleDataChangeHelper<SELF> {
	private final Supplier<SELF> _self;
	public ColumnStyleDataChangeHelper(Supplier<SELF> columnBuilder) {
		_self = columnBuilder;
	}
	public HTMLElement apply(SheetElement.Handsontable instance, HTMLTableCellElement td, int row, String prop) {
		DomGlobal.console.log("1" + td);
		DomGlobal.console.log("1" + prop);
		Data data = instance.spreadsheet.values()[row];
		DomGlobal.console.log(data);
		DomGlobal.console.log(data.isChanged(prop));
		DomGlobal.console.log(td.classList);
		if(data!=null && data.isChanged(prop)) td.classList.add("changed");
		else td.classList.remove("changed");
		DomGlobal.console.log("2");
		return td;
	}
	private SELF that() {
		return _self.get();
	}
}
