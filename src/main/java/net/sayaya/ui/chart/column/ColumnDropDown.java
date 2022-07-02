package net.sayaya.ui.chart.column;

import elemental2.dom.CSSProperties;
import elemental2.dom.HTMLElement;
import lombok.experimental.Delegate;
import net.sayaya.ui.DropDownElement;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.Data;

import java.util.LinkedList;
import java.util.List;

import static org.jboss.elemento.Elements.span;

public final class ColumnDropDown implements ColumnBuilder {
	private final String id;
	private final ListElement.SingleLineItem[] list;
	@Delegate(excludes = ColumnBuilder.class) private final ColumnBuilderDefaultHelper<ColumnDropDown> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
	private final ColumnStyleDataChangeHelper<ColumnDropDown> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleColorHelper<ColumnDropDown> colorHelper = new ColumnStyleColorHelper<>(()->this);
	private final List<ColumnStyleColorConditionalHelper<ColumnDropDown>> colorConditionalHelpers = new LinkedList<>();
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleAlignHelper<ColumnDropDown> alignHelper = new ColumnStyleAlignHelper<>(()->this);
	ColumnDropDown(String id, ListElement.SingleLineItem... list) {
		this.id = id;
		this.list = list;
	}
	@Override
	public Column build() {
		Column column = defaultHelper.build().data(id);
		return column.readOnly(true).renderer((sheet, td, row, col, prop, value, ci)->{
			Data data = sheet.spreadsheet.values()[row];

			alignHelper.clear(td);
			colorHelper.clear(td);
			for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.clear(td);
			alignHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			dataChangeHelper.apply(sheet, td, row, prop);
			for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.apply(td, row, prop, value);
			ListElement<ListElement.SingleLineItem> list = ListElement.singleLineList().add(ListElement.singleLine().label(""));
			for(var item: this.list) list.add(ListElement.singleLine().label(item.value()));
			DropDownElement elem = DropDownElement.filled(list).select(value);
			if(defaultHelper.readOnly()) elem.enabled(false);
			else if(data!=null) elem.onValueChange(evt->{
				String v = evt.value();
				data.put(id, v);
				colorHelper.clear(td);
				for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.clear(td);

				colorHelper.apply(td, row, prop, v);
				dataChangeHelper.apply(sheet, td, row, prop);
				for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.apply(td, row, prop, v);
			});
			elem.element().getElementsByClassName("mdc-line-ripple").asList().stream().map(e->(HTMLElement)e).forEach(e-> e.style.display = "none");
			elem.element().getElementsByClassName("mdc-select__anchor").asList().stream().map(e->(HTMLElement)e).findFirst().ifPresent(e-> e.style.height = CSSProperties.HeightUnionType.of("100%"));
			elem.element().getElementsByClassName("mdc-select__selected-text").asList().stream().map(e->(HTMLElement)e).findFirst().ifPresent(e->{
				e.style.color = "inherit";
				e.style.textAlign = "inherit";
			});
			td.innerHTML = "";
			td.style.padding = CSSProperties.PaddingUnionType.of("0");
			td.appendChild(elem.element());
			return td;
		}).headerRenderer(n->span().textContent(defaultHelper.name()).element());
	}
	public ColumnStyleColorConditionalHelper<ColumnDropDown> pattern(String pattern) {
		ColumnStyleColorConditionalHelper<ColumnDropDown> helper = new ColumnStyleColorConditionalHelper<>(pattern, ()->this);
		colorConditionalHelpers.add(helper);
		return helper;
	}
}
