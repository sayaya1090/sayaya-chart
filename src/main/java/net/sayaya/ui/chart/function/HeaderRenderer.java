package net.sayaya.ui.chart.function;

import elemental2.dom.HTMLElement;
import jsinterop.annotations.JsFunction;

@JsFunction
public interface HeaderRenderer {
	HTMLElement render(int n);
}
