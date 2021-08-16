package net.sayaya.ui.chart.column;

import elemental2.dom.*;
import elemental2.svg.SVGElement;
import elemental2.svg.SVGPolygonElement;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;
import lombok.experimental.Delegate;
import net.sayaya.ui.DropDownElement;
import net.sayaya.ui.HTMLElementBuilder;
import net.sayaya.ui.ListElement;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.event.HasValueChangeHandlers;
import org.gwtproject.event.shared.HandlerRegistration;
import org.jboss.elemento.HtmlContentBuilder;

import java.util.LinkedList;
import java.util.List;

import static org.jboss.elemento.Elements.*;
import static org.jboss.elemento.EventType.bind;

public final class ColumnDropDown implements ColumnBuilder {
	private final String id;
	private final ListElement<ListElement.SingleLineItem> list;
	@Delegate(excludes = ColumnBuilder.class) private final ColumnBuilderDefaultHelper<ColumnDropDown> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
	private final ColumnStyleDataChangeHelper<ColumnDropDown> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleColorHelper<ColumnDropDown> colorHelper = new ColumnStyleColorHelper<>(()->this);
	private final List<ColumnStyleColorConditionalHelper<ColumnDropDown>> colorConditionalHelpers = new LinkedList<>();
	@Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleAlignHelper<ColumnDropDown> alignHelper = new ColumnStyleAlignHelper<>(()->this);
	ColumnDropDown(String id, ListElement<ListElement.SingleLineItem> list) {
		this.id = id;
		this.list = list;
	}
	@Override
	public Column build() {
		Column column = defaultHelper.build().data(id).readOnly(true);
		return column.renderer((sheet, td, row, col, prop, value, ci)->{
			alignHelper.clearStyleAlign(td);
			colorHelper.clearStyleColor(td);
			for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.clearStyleColorConditional(td);
			alignHelper.apply(td, row, prop, value);
			colorHelper.apply(td, row, prop, value);
			dataChangeHelper.apply(sheet, td, row, prop);
			for(ColumnStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.apply(td, row, prop, value==null?"false":value);
			DropDownElement elem = DropDownElement.dropdown(list).value(value);
			td.innerHTML = "";
			td.appendChild(elem.element());
			return td;
		}).headerRenderer(n->span().textContent(defaultHelper.name()).element());
	}



	private static class DropDownElement extends HTMLElementBuilder<HTMLDivElement, DropDownElement> implements HasValueChangeHandlers<String> {
		public static DropDownElement dropdown(ListElement<?> list) {
			DropDownElement elem = new DropDownElement(div(), list);
			elem._mdc = inject(elem.element());
			elem.surfaceAdapter = surfaceAdaptor(elem._mdc);
			return elem;
		}
		private static native MDCDropdown inject(Element elem) /*-{
            return $wnd.mdc.select.MDCSelect.attachTo(elem);
        }-*/;
		private static native MDCMenuSurfaceAdapter surfaceAdaptor(MDCDropdown mdc) /*-{
            return mdc.menu.menuSurface.foundation.adapter;
        }-*/;
		private final static String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
		private final SVGPolygonElement inactive = (SVGPolygonElement) DomGlobal.document.createElementNS(SVG_NAMESPACE, "polygon");
		private final SVGPolygonElement active = (SVGPolygonElement) DomGlobal.document.createElementNS(SVG_NAMESPACE, "polygon");
		private final SVGElement svg = (SVGElement) DomGlobal.document.createElementNS(SVG_NAMESPACE, "svg");
		private final HtmlContentBuilder<HTMLElement> ripple = span().css("mdc-select__ripple");
		private final HtmlContentBuilder<HTMLElement> value = span().css("mdc-select__selected-text");
		private final HtmlContentBuilder<HTMLElement> arrow = span().css("mdc-select__dropdown-icon").add(svg);
		private final HtmlContentBuilder<HTMLElement> ripple2 = span().css("mdc-line-ripple").style("display: none;");
		private final HtmlContentBuilder<HTMLDivElement> anchor = div().css("mdc-select__anchor")
				.add(ripple)
				.add(value)
				.add(arrow)
				.add(ripple2);
		private final HtmlContentBuilder<HTMLDivElement> menu = div().css("mdc-select__menu", "mdc-menu", "mdc-menu-surface", "mdc-menu-surface--fixed", "mdc-menu-surface--fullwidth");
		private final ListElement<?> list;
		private final HtmlContentBuilder<HTMLDivElement> _this;
		private MDCDropdown _mdc;
		private MDCMenuSurfaceAdapter surfaceAdapter;
		public DropDownElement(HtmlContentBuilder<HTMLDivElement> e, ListElement<?> list) {
			super(e);
			_this = e;
			this.list = list;
			layout();
		}

		private void layout() {
			_this.css("mdc-select", "mdc-select--filled").style("width: 100%;")
					.add(anchor)
					.add(menu.add(list));
			svg.setAttribute( "viewBox", "7 10 10 5");
			svg.classList.add("mdc-select__dropdown-icon-graphic");
			svg.appendChild(inactive);
			svg.appendChild(active);
			inactive.setAttribute("points", "7 10 12 15 17 10");
			inactive.setAttribute("stroke", "none");
			inactive.setAttribute("fill-rule", "evenodd");
			inactive.classList.add("mdc-select__dropdown-icon-inactive");
			active.setAttribute("points", "7 15 12 10 17 15");
			active.setAttribute("stroke", "none");
			active.setAttribute("fill-rule", "evenodd");
			active.classList.add("mdc-select__dropdown-icon-active");
		}
		public DropDownElement value(String value) {
			this.value.textContent(value);
			return that();
		}
		@Override
		public String value() {
			return value.element().textContent;
		}
		@Override
		public HandlerRegistration onValueChange(ValueChangeEventListener<String> listener) {
			EventListener wrapper = evt->listener.handle(ValueChangeEvent.event(evt, value()));
			return bind(value.element(), "DOMSubtreeModified", wrapper);
		}
		@Override
		public DropDownElement that() {
			return this;
		}
		@JsType(isNative = true, namespace = "mdc.select", name="MDCSelect")
		private final static class MDCDropdown {}
	}
	@JsType(isNative = true, namespace= JsPackage.GLOBAL, name="Object")
	private final static class MDCMenuSurfaceAdapter {
		public native void setMaxHeight(String height);
	}
}
