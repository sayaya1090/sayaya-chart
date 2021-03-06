package net.sayaya.ui.chart;

import elemental2.core.Function;
import elemental2.core.JsArray;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.event.HasStateChangeHandlers;

import java.util.Collection;

@JsType
public class Data implements HasStateChangeHandlers<Data.DataState> {
	private final String idx;
	private final JsPropertyMap<Object> initialized = JsPropertyMap.of();
	private final JsArray<StateChangeEventListener<DataState>> listeners = JsArray.of();
	private DataState state;
	public Data(String idx) {
		this.idx = idx;
	}
	@JsMethod
	public String idx() {
		return idx;
	}
	public Data put(String key, String value) {
		Js.asPropertyMap(this).set(key, value);
		if(!initialized.has(key)) initialized.set(key, value);
		return this;
	}
	public Data delete(String key) {
		Js.asPropertyMap(this).delete(key);
		return this;
	}
	public Data initialize(String key, String value) {
		Js.asPropertyMap(this).set(key, value);
		initialized.set(key, value);
		return this;
	}
	public String get(String key) {
		JsPropertyMap<Object> map = Js.asPropertyMap(this);
		if(!map.has(key)) return null;
		Object obj = map.get(key);
		if(obj instanceof String) return (String) Js.asPropertyMap(this).get(key);
		else return null;
	}
	public boolean isChanged(String key) {
		return !Js.isTripleEqual(trim(Js.asString(initialized.get(key))), trim(Js.asString(get(key))));
	}
	private static String trim(String str) {
		if(str == null) return str;
		str = str.replace("\r", "").trim();
		if(str.isEmpty()) return null;
		else return str;
	}
	@Override
	public Collection<StateChangeEventListener<DataState>> listeners() {
		return listeners.asList();
	}
	@Override
	@JsIgnore
	public DataState state() {
		return state;
	}
	public Data select(boolean select) {
		state = select?DataState.SELECTED:DataState.UNSELECTED;
		fireStateChangeEvent();
		return this;
	}

	public enum DataState {
		UNSELECTED, SELECTED
	}

	private native Data proxy(Data origin, Function callback) /*-{
		var proxy = new Proxy(origin, {
			set: function(target, key, value, receiver) {
				var result = Reflect.set(target, key, value, receiver);
				callback(key, value);
				return result;
			}
		});
		return proxy;
	}-*/;
}
