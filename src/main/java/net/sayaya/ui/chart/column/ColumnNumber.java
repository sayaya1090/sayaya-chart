package net.sayaya.ui.chart.column;

import com.google.gwt.i18n.client.NumberFormat;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.function.CellEditor;
import net.sayaya.ui.chart.function.CellEditorFactory;

import static org.jboss.elemento.Elements.input;
import static org.jboss.elemento.Elements.span;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Accessors(fluent = true)
public final class ColumnNumber implements ColumnBuilder {
	private final String id;
	private NumberFormat format = NumberFormat.getDecimalFormat();
	@Delegate private final ColumnBuilderDefaultHelper<ColumnNumber> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
	@Delegate private final ColumnStyleTextHelper<ColumnNumber> textHelper = new ColumnStyleTextHelper<>(()->this);
	@Delegate private final ColumnStyleDataChangeHelper<ColumnNumber> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
	@Delegate private final ColumnStyleColorHelper<ColumnNumber> colorHelper = new ColumnStyleColorHelper<>(()->this);
	@Delegate private final ColumnStyleColorConditionalHelper<ColumnNumber> colorConditionalHelper = new ColumnStyleColorConditionalHelper<>(()->this);
	@Delegate private final ColumnStyleAlignHelper<ColumnNumber> alignHelper = new ColumnStyleAlignHelper<>(()->this);
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
		}).editor(this::numberFieldEditor)
		.headerRenderer(n->span().textContent(defaultHelper.name()).element());
	}
	private CellEditor numberFieldEditor(Object props) {
		NumberEditorImpl impl = new NumberEditorImpl();
		return CellEditorFactory.text(props, impl);
	}
	private final class NumberEditorImpl implements CellEditorFactory.CellEditorTextImpl {
		private final HTMLInputElement elem = input("number").element();
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
