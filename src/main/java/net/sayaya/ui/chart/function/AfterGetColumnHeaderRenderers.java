package net.sayaya.ui.chart.function;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface AfterGetColumnHeaderRenderers {
	void apply(ColumnHeaderRenderer[] renderers);
}
