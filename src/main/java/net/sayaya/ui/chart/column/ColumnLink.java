package net.sayaya.ui.chart.column;

import elemental2.dom.Element;
import elemental2.dom.HTMLAnchorElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.Data;
import net.sayaya.ui.chart.function.CellEditor;
import net.sayaya.ui.chart.function.CellEditorFactory;
import net.sayaya.ui.chart.function.Consumer;
import org.jboss.elemento.EventType;
import org.jboss.elemento.HTMLContainerBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import static org.jboss.elemento.Elements.*;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ColumnLink implements ColumnBuilder {
	private final String id;
	private final Function<Data, String> mapper;
	private String target;
	private Consumer<Data> callbackFn;
	@Delegate(excludes = ColumnBuilder.class) private final ColumnBuilderDefaultHelper<ColumnLink> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleTextHelper<ColumnLink> textHelper = new ColumnStyleTextHelper<>(()->this);
	private final ColumnStyleDataChangeHelper<ColumnLink> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleColorHelper<ColumnLink> colorHelper = new ColumnStyleColorHelper<>(()->this);
	private final List<ColumnStyleColorConditionalHelper<ColumnLink>> colorConditionalHelpers = new LinkedList<>();
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleAlignHelper<ColumnLink> alignHelper = new ColumnStyleAlignHelper<>(()->this);
	public ColumnLink target(String target) {
		this.target = target;
		return this;
	}
	public ColumnLink onClick(Consumer<Data> callbackFn) {
		this.callbackFn = callbackFn;
		return this;
	}
	@Override
	public Column build() {
		Column column = defaultHelper.build().data(id);
		return column.renderer((sheet, td, row, col, prop, value, ci)->{
			Data data = sheet.spreadsheet.values()[row];
			textHelper.clear(td);
			colorHelper.clear(td);
			for(ColumnStyleColorConditionalHelper<ColumnLink> helper: colorConditionalHelpers) helper.clear(td);
			alignHelper.clear(td);

			textHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			dataChangeHelper.apply(sheet, td, row, prop);
			for(ColumnStyleColorConditionalHelper<ColumnLink> helper: colorConditionalHelpers) helper.apply(td, row, prop, value);
			alignHelper.apply(td, row, prop, value);
			td.innerHTML = "";
			if(value!=null && !value.trim().isEmpty()) {
				String url = mapper.apply(data);
				HTMLContainerBuilder<HTMLAnchorElement> a = a(url, target != null ? target : "_blank").add(value);
				if(callbackFn!=null) a.on(EventType.click, evt->{
					evt.stopPropagation();
					evt.preventDefault();
					callbackFn.accept(data);
				});
				td.appendChild(a.element());
			}
			return td;
		}).editor(this::textFieldEditor)
		.headerRenderer(n->span().textContent(defaultHelper.name()).element());
	}
	public ColumnStyleColorConditionalHelper<ColumnLink> pattern(String pattern) {
		ColumnStyleColorConditionalHelper<ColumnLink> helper = new ColumnStyleColorConditionalHelper<>(pattern, ()->this);
		colorConditionalHelpers.add(helper);
		return helper;
	}
	private CellEditor textFieldEditor(Object props) {
		TextEditorImpl impl = new TextEditorImpl();
		return CellEditorFactory.text(props, impl);
	}
	private final class TextEditorImpl implements CellEditorFactory.CellEditorTextImpl {
		private final HTMLInputElement elem = input("text").element();
		@Override
		public void prepare(int row, int col, String prop, HTMLElement td, String value, Object cell) {
			textHelper.clear(td);
			colorHelper.clear(td);
			for(ColumnStyleColorConditionalHelper<ColumnLink> helper: colorConditionalHelpers) helper.clear(td);
			alignHelper.clear(td);

			textHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			for(ColumnStyleColorConditionalHelper<ColumnLink> helper: colorConditionalHelpers) helper.apply(td, row, prop, value);
			alignHelper.apply(td, row, prop, value);
		}

		@Override
		public String toValue(String value) {
			return value;
		}
		@Override
		public void setValue(String value) {
			elem.value = value;
		}
		@Override
		public Element createElement() {
			return elem;
		}
		@Override
		public void initialize(Element element) {

		}
	}
}
