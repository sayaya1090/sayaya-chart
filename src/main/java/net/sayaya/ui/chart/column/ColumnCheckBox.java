package net.sayaya.ui.chart.column;

import elemental2.core.JsRegExp;
import elemental2.core.RegExpResult;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.elements.CheckboxElementBuilder;

import java.util.LinkedList;
import java.util.List;

import static org.jboss.elemento.Elements.*;

public final class ColumnCheckBox implements ColumnBuilder {
	private final static JsRegExp CHK_BOOLEAN = new JsRegExp("^true|false$");
	private static String normalize(String str) {
		if(str == null) return null;
		str = str.trim().toLowerCase();
		RegExpResult chkBool = CHK_BOOLEAN.exec(str);
		if(chkBool != null) return str;
		else return str;
	}
	private final String id;
	@Delegate(excludes = ColumnBuilder.class) private final ColumnBuilderDefaultHelper<ColumnCheckBox> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
	private final ColumnStyleDataChangeHelper<ColumnCheckBox> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleColorHelper<ColumnCheckBox> colorHelper = new ColumnStyleColorHelper<>(()->this);
	private final List<ColumnStyleColorConditionalHelper<ColumnCheckBox>> colorConditionalHelpers = new LinkedList<>();
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleAlignHelper<ColumnCheckBox> alignHelper = new ColumnStyleAlignHelper<>(()->this);
	ColumnCheckBox(String id) {
		this.id = id;
		alignHelper.horizontal("center");
	}
	@Override
	public Column build() {
		Column column = defaultHelper.build().data(id).readOnly(true);
		return column.renderer((sheet, td, row, col, prop, value, ci)->{
			Data data = sheet.spreadsheet.values()[row];
			value = normalize(value);
			alignHelper.clear(td);
			colorHelper.clear(td);
			for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.clear(td);
			alignHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			dataChangeHelper.apply(sheet, td, row, prop);
			for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.apply(td, row, prop, value==null?"false":value);

			var elem = CheckboxElementBuilder.checkbox().select(Boolean.parseBoolean(value)).style("    vertical-align: middle;");
			if(defaultHelper.readOnly()) elem.element().setAttribute("disabled", "true");
			else if(data!=null) elem.onChange(evt->{
				data.put(id, evt!=null?String.valueOf(elem.isSelected()):"false");
				String v = normalize(String.valueOf(elem.isSelected()));
				colorHelper.clear(td);
				for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.clear(td);

				colorHelper.apply(td, row, prop, v);
				dataChangeHelper.apply(sheet, td, row, prop);
				for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.apply(td, row, prop, v==null?"false":v);
			});
			td.innerHTML = "";
			td.appendChild(elem.element());
			return td;
		}).headerRenderer(n->span().textContent(defaultHelper.name()).element());
	}
	public ColumnStyleColorConditionalHelper<ColumnCheckBox> isTrue() {
		ColumnStyleColorConditionalHelper<ColumnCheckBox> helper = new ColumnStyleColorConditionalHelper<>("true", ()->this);
		colorConditionalHelpers.add(helper);
		return helper;
	}
	public ColumnStyleColorConditionalHelper<ColumnCheckBox> isFalse() {
		ColumnStyleColorConditionalHelper<ColumnCheckBox> helper = new ColumnStyleColorConditionalHelper<>("false", () -> this);
		colorConditionalHelpers.add(helper);
		return helper;
	}
}
