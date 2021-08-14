package net.sayaya.ui.chart.column;

import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.function.CellEditor;
import net.sayaya.ui.chart.function.CellEditorFactory;

import static org.jboss.elemento.Elements.*;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ColumnString implements ColumnBuilder {
	private final String id;
	@Delegate
	private final ColumnBuilderDefaultHelper<ColumnString> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
	@Delegate
	private final ColumnStyleTextHelper<ColumnString> textHelper = new ColumnStyleTextHelper<>(()->this);
	@Delegate
	private final ColumnStyleDataChangeHelper<ColumnString> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
	@Delegate
	private final ColumnStyleColorHelper<ColumnString> colorHelper = new ColumnStyleColorHelper<>(()->this);
	@Delegate
	private final ColumnStyleColorConditionalHelper<ColumnString> colorConditionalHelper = new ColumnStyleColorConditionalHelper<>(()->this);
	@Delegate
	private final ColumnStyleAlignHelper<ColumnString> alignHelper = new ColumnStyleAlignHelper<>(()->this);
	@Override
	public Column build() {
		Column column = defaultHelper.build().data(id);
		return column.renderer((sheet, td, row, col, prop, value, ci)->{
			textHelper.clearStyleText(td);
			colorHelper.clearStyleColor(td);
			colorConditionalHelper.clearStyleColorConditional(td);
			alignHelper.clearStyleAlign(td);

			textHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			dataChangeHelper.apply(sheet, td, row, prop);
			colorConditionalHelper.apply(td, row, prop, value);
			alignHelper.apply(td, row, prop, value);
			
			td.innerHTML = value;
			return td;
		}).editor(this::textFieldEditor)
		.headerRenderer(n->span().textContent(defaultHelper.name()).element());
	}
	private CellEditor textFieldEditor(Object props) {
		TextEditorImpl impl = new TextEditorImpl();
		return CellEditorFactory.text(props, impl);
	}
	private final class TextEditorImpl implements CellEditorFactory.CellEditorTextImpl {
		private final HTMLInputElement elem = input("text").element();
		@Override
		public void prepare(int row, int col, String prop, HTMLElement td, String value, Object cell) {
			textHelper.clearStyleText(td);
			colorHelper.clearStyleColor(td);
			colorConditionalHelper.clearStyleColorConditional(td);
			alignHelper.clearStyleAlign(td);

			textHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			colorConditionalHelper.apply(td, row, prop, value);
			alignHelper.apply(td, row, prop, value);
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
