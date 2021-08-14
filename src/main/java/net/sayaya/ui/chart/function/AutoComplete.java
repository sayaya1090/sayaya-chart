package net.sayaya.ui.chart.function;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface AutoComplete {
	void exec(String query, Consumer<String[]> process);
}
