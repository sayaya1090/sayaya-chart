package net.sayaya.ui.chart.column;

import com.google.gwt.regexp.shared.RegExp;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLElement;

import java.util.function.Supplier;

public final class ColumnStyleColorConditionalHelper<SELF> {
	private final RegExp pattern;
	private ColumnStyleFn<String> color;
	private ColumnStyleFn<String> colorBackground;
	private final Supplier<SELF> _self;
	public ColumnStyleColorConditionalHelper(String pattern, Supplier<SELF> columnBuilder) {
		this.pattern = RegExp.compile(pattern.trim());
		_self = columnBuilder;
	}
	public HTMLElement apply(HTMLElement td, int row, String prop, String value) {
		if(value == null) return td;
		DomGlobal.console.log(value);
		if(pattern.test(value.trim())) {
			if(color !=null)             td.style.color              = color.apply(td, row, prop, value);
			if(colorBackground !=null)   td.style.backgroundColor    = colorBackground.apply(td, row, prop, value);
		}
		return td;
	}
	public SELF clearStyleColorConditional(HTMLElement td) {
		td.style.removeProperty("color");
		td.style.removeProperty("backgroundColor");
		return that();
	}
	public SELF than(String color) {
		if(color == null) return than((ColumnStyleFn<String>)null);
		return than((td, row, prop, value)->color);
	}
	public SELF than(ColumnStyleFn<String> color) {
		this.color = color;
		return that();
	}
	public SELF than(String color, String background) {
		if(background == null) return than(color);
		if(color == null) return than(null, (td, row, prop, value)->background);
		return than((td, row, prop, value)->color, (td, row, prop, value)->background);
	}
	public SELF than(ColumnStyleFn<String> color, ColumnStyleFn<String> background) {
		this.color = color;
		this.colorBackground = background;
		return that();
	}
	private SELF that() {
		return _self.get();
	}
}
