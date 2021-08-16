package net.sayaya.ui.chart.column;

import elemental2.dom.HTMLElement;

import java.util.function.Supplier;

public final class ColumnStyleAlignHelper<SELF> implements ColumnStyleHelper {
	private final Supplier<SELF> _self;
	private ColumnStyleFn<String> align;
	public ColumnStyleAlignHelper(Supplier<SELF> columnBuilder) {
		_self = columnBuilder;
	}
	@Override
	public HTMLElement apply(HTMLElement td, int row, String prop, String value) {
		if(align!=null)     td.style.textAlign  = align.apply(td, row, prop, value);
		return td;
	}
	public SELF clearStyleAlign(HTMLElement td) {
		td.style.removeProperty("textAlign");
		return that();
	}
	public SELF align(String align) {
		if(align == null) return align((ColumnStyleFn<String>)null);
		return align((td, row, prop, value)->align);
	}
	public SELF align(ColumnStyleFn<String> align) {
		this.align = align;
		return that();
	}
	private SELF that() {
		return _self.get();
	}
}
