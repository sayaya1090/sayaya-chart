package net.sayaya.ui.chart.column;

import elemental2.dom.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.SheetElement;
import net.sayaya.ui.chart.function.CellEditor;
import net.sayaya.ui.chart.function.CellEditorFactory;
import org.jboss.elemento.HtmlContentBuilder;
import org.jboss.elemento.TextContentBuilder;

import java.util.LinkedList;
import java.util.List;

import static org.jboss.elemento.Elements.*;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ColumnText implements ColumnBuilder {
	private final String id;
	private final int heightMin;
	private final int heightMax;
	@Delegate(excludes = ColumnBuilder.class) private final ColumnBuilderDefaultHelper<ColumnText> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleTextHelper<ColumnText> textHelper = new ColumnStyleTextHelper<>(()->this);
	private final ColumnStyleDataChangeHelper<ColumnText> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleColorHelper<ColumnText> colorHelper = new ColumnStyleColorHelper<>(()->this);
	private final List<ColumnStyleColorConditionalHelper<ColumnText>> colorConditionalHelpers = new LinkedList<>();
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleAlignHelper<ColumnText> alignHelper = new ColumnStyleAlignHelper<>(()->this);
	@Override
	public Column build() {
		Column column = defaultHelper.build().data(id);
		return column.renderer((sheet, td, row, col, prop, value, ci)->{
			textHelper.clear(td);
			colorHelper.clear(td);
			for(ColumnStyleColorConditionalHelper<ColumnText> helper: colorConditionalHelpers) helper.clear(td);
			alignHelper.clear(td);

			textHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			dataChangeHelper.apply(sheet, td, row, prop);
			for(ColumnStyleColorConditionalHelper<ColumnText> helper: colorConditionalHelpers) helper.apply(td, row, prop, value);
			alignHelper.apply(td, row, prop, value);

			String cssHeightMin = "min-height: " + (heightMin>0?heightMin:24) + "px;";
			String cssHeightMax = "max-height: " + (heightMax>0?heightMax:200) + "px;";
			HTMLDivElement div = div().style(cssHeightMin + cssHeightMax + "display: block; overflow-y: auto; word-break: break-all;").element();
			div.innerHTML= value;
			td.innerHTML = "";
			td.appendChild(div);
			return td;
		}).editor(this::textAreaEditor)
		.headerRenderer(n->span().textContent(defaultHelper.name()).element());
	}
	public ColumnStyleColorConditionalHelper<ColumnText> pattern(String pattern) {
		ColumnStyleColorConditionalHelper<ColumnText> helper = new ColumnStyleColorConditionalHelper<>(pattern, ()->this);
		colorConditionalHelpers.add(helper);
		return helper;
	}
	private CellEditor textAreaEditor(Object props) {
		TextEditorImpl impl = new TextEditorImpl();
		return CellEditorFactory.text(props, impl);
	}
	private final class TextEditorImpl implements CellEditorFactory.CellEditorTextImpl {
		private final HTMLTextAreaElement elem = textarea().element();
		@Override
		public void prepare(int row, int col, String prop, HTMLElement td, String value, Object cell) {
			textHelper.clear(td);
			colorHelper.clear(td);
			for(ColumnStyleColorConditionalHelper<ColumnText> helper: colorConditionalHelpers) helper.clear(td);
			alignHelper.clear(td);

			textHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			for(ColumnStyleColorConditionalHelper<ColumnText> helper: colorConditionalHelpers) helper.apply(td, row, prop, value);
			alignHelper.apply(td, row, prop, value);

			//td.style.minHeight = CSSProperties.MinHeightUnionType.of(heightMin>0?heightMin:24);
			//td.style.maxHeight = CSSProperties.MaxHeightUnionType.of(heightMax>0?heightMax:200);
		}
		@Override
		public String toValue(String value) {
			return value;
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
