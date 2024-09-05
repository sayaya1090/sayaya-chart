package net.sayaya.ui.chart.column;

import com.google.gwt.core.client.Scheduler;
import elemental2.dom.*;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.dom.MdSelectOptionElement;
import net.sayaya.ui.elements.SelectElementBuilder;

import java.util.LinkedList;
import java.util.List;

import static org.jboss.elemento.Elements.span;

public final class ColumnDropDown implements ColumnBuilder {
	private final String id;
	private final MdSelectOptionElement[] list;
	@Delegate(excludes = ColumnBuilder.class) private final ColumnBuilderDefaultHelper<ColumnDropDown> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
	private final ColumnStyleDataChangeHelper<ColumnDropDown> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleColorHelper<ColumnDropDown> colorHelper = new ColumnStyleColorHelper<>(()->this);
	private final List<ColumnStyleColorConditionalHelper<ColumnDropDown>> colorConditionalHelpers = new LinkedList<>();
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleAlignHelper<ColumnDropDown> alignHelper = new ColumnStyleAlignHelper<>(()->this);
	ColumnDropDown(String id, MdSelectOptionElement... list) {
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
			var elem = SelectElementBuilder.select().outlined().option().value(null).headline("").end();
			Scheduler.get().scheduleDeferred(()->{
				var label = elem.element().shadowRoot.getElementById("label");
				label.style.paddingTop = CSSProperties.PaddingTopUnionType.of("0px");
				label.style.paddingBottom = CSSProperties.PaddingBottomUnionType.of("0px");
				label.style.fontSize = CSSProperties.FontSizeUnionType.of("10px");

				var field = elem.element().shadowRoot.getElementById("field");
				field.style.height = CSSProperties.HeightUnionType.of("22px");
				field.style.setProperty("--_outline-width",  "0px");
				field.style.setProperty("--_hover-outline-width",  "0px");
				field.style.setProperty("--_focus-outline-width",  "0px");
				field.style.setProperty("--_disabled-outline-width",  "0px");
			});
			for(MdSelectOptionElement option: list) elem.option().value(option.value).headline(option.textContent);
			if(defaultHelper.readOnly()) elem.enable(false);
			else if(data!=null) elem.onChange(evt->{
				String v = elem.value();
				data.put(id, v);
				colorHelper.clear(td);
				for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.clear(td);

				colorHelper.apply(td, row, prop, v);
				dataChangeHelper.apply(sheet, td, row, prop);
				for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.apply(td, row, prop, v);
			});
			td.innerHTML = "";
			td.style.padding = CSSProperties.PaddingUnionType.of("0");
			td.appendChild(elem.style("width", "100%").style("white-space", "normal").element());
			return td;
		}).headerRenderer(n->span().textContent(defaultHelper.name()).element());
	}
	public ColumnStyleColorConditionalHelper<ColumnDropDown> pattern(String pattern) {
		ColumnStyleColorConditionalHelper<ColumnDropDown> helper = new ColumnStyleColorConditionalHelper<>(pattern, ()->this);
		colorConditionalHelpers.add(helper);
		return helper;
	}
}
