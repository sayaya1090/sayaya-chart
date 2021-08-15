package net.sayaya.ui.chart.column;

import com.google.gwt.i18n.client.NumberFormat;
import elemental2.core.JsRegExp;
import elemental2.core.RegExpResult;
import elemental2.dom.Element;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.chart.function.CellEditor;
import net.sayaya.ui.chart.function.CellEditorFactory;

import java.util.LinkedList;
import java.util.List;

import static org.jboss.elemento.Elements.input;
import static org.jboss.elemento.Elements.span;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Accessors(fluent = true)
public final class ColumnNumber implements ColumnBuilder {
	private final static JsRegExp CHK_NUMBER = new JsRegExp("^\\d*(\\.\\d*)?$");
	private final String id;
	private NumberFormat format = NumberFormat.getDecimalFormat();
	@Delegate private final ColumnBuilderDefaultHelper<ColumnNumber> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
	@Delegate private final ColumnStyleTextHelper<ColumnNumber> textHelper = new ColumnStyleTextHelper<>(()->this);
	@Delegate private final ColumnStyleDataChangeHelper<ColumnNumber> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
	@Delegate private final ColumnStyleColorHelper<ColumnNumber> colorHelper = new ColumnStyleColorHelper<>(()->this);
	private final List<ColumnStyleColorRangeHelper<ColumnNumber>> colorRangeHelpers = new LinkedList<>();
	@Delegate private final ColumnStyleAlignHelper<ColumnNumber> alignHelper = new ColumnStyleAlignHelper<>(()->this);
	private static String toString(NumberFormat NF, Object value) throws RuntimeException {
		if(value == null) return null;
		else if(value instanceof Long) return NF.format((Long)value);
		else if(value instanceof Integer) return NF.format((Integer)value);
		else if(value instanceof Double) return NF.format((Double)value);
		else if(value instanceof String) {
			String cast = (String)value;
			cast = cast.trim();
			RegExpResult chkNumber = CHK_NUMBER.exec(cast);
			if(chkNumber != null) return NF.format(Double.parseDouble(cast));
			else throw new RuntimeException();
		} else throw new RuntimeException();
	}
	@Override
	public Column build() {
		Column column = defaultHelper.build().data(id);
		return column.renderer((sheet, td, row, col, prop, value, ci)->{
			textHelper.clearStyleText(td);
			colorHelper.clearStyleColor(td);
			for(ColumnStyleColorRangeHelper<ColumnNumber> helper: colorRangeHelpers) helper.clearStyleColorRange(td);
			alignHelper.clearStyleAlign(td);

			textHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			dataChangeHelper.apply(sheet, td, row, prop);
			for(ColumnStyleColorRangeHelper<ColumnNumber> helper: colorRangeHelpers) helper.apply(td, row, prop, value);
			alignHelper.apply(td, row, prop, value);
			td.innerHTML = toString(format, value);
			return td;
		}).editor(this::numberFieldEditor)
		.headerRenderer(n->span().textContent(defaultHelper.name()).element());
	}
	public ColumnStyleColorRangeHelper<ColumnNumber> eq(double value) {
		return range(ColumnStyleColorRangeHelper.Operation.EQ, value);
	}
	public ColumnStyleColorRangeHelper<ColumnNumber> lt(double value) {
		return range(ColumnStyleColorRangeHelper.Operation.LT, value);
	}
	public ColumnStyleColorRangeHelper<ColumnNumber> gt(double value) {
		return range(ColumnStyleColorRangeHelper.Operation.GT, value);
	}
	public ColumnStyleColorRangeHelper<ColumnNumber> le(double value) {
		return range(ColumnStyleColorRangeHelper.Operation.LE, value);
	}
	public ColumnStyleColorRangeHelper<ColumnNumber> ge(double value) {
		return range(ColumnStyleColorRangeHelper.Operation.GE, value);
	}
	public ColumnStyleColorRangeHelper<ColumnNumber> bw(double less, double grater) {
		return range(ColumnStyleColorRangeHelper.Operation.BW, less, grater);
	}
	public ColumnStyleColorRangeHelper<ColumnNumber> bwe(double less, double grater) {
		return range(ColumnStyleColorRangeHelper.Operation.BWE, less, grater);
	}
	private ColumnStyleColorRangeHelper<ColumnNumber> range(ColumnStyleColorRangeHelper.Operation op, double value) {
		ColumnStyleColorRangeHelper<ColumnNumber> helper = new ColumnStyleColorRangeHelper<>(op, value, ()->this);
		colorRangeHelpers.add(helper);
		return helper;
	}
	private ColumnStyleColorRangeHelper<ColumnNumber> range(ColumnStyleColorRangeHelper.Operation op, double value, double value2) {
		ColumnStyleColorRangeHelper<ColumnNumber> helper = new ColumnStyleColorRangeHelper<>(op, value, value2, ()->this);
		colorRangeHelpers.add(helper);
		return helper;
	}
	private CellEditor numberFieldEditor(Object props) {
		NumberEditorImpl impl = new NumberEditorImpl();
		return CellEditorFactory.text(props, impl);
	}
	private final class NumberEditorImpl implements CellEditorFactory.CellEditorTextImpl {
		private final HTMLInputElement elem = input("text").element();
		@Override
		public void prepare(int row, int col, String prop, HTMLElement td, String value, Object cell) {
			textHelper.clearStyleText(td);
			colorHelper.clearStyleColor(td);
			for(ColumnStyleColorRangeHelper<ColumnNumber> helper: colorRangeHelpers) helper.clearStyleColorRange(td);
			alignHelper.clearStyleAlign(td);

			textHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			for(ColumnStyleColorRangeHelper<ColumnNumber> helper: colorRangeHelpers) helper.apply(td, row, prop, value);
			alignHelper.apply(td, row, prop, value);
		}

		@Override
		public String toValue(String value) {
			if(value == null || value.trim().isEmpty()) return null;
			try {
				return String.valueOf(format.parse(value));
			} catch(Exception e) {
				return null;
			}
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
