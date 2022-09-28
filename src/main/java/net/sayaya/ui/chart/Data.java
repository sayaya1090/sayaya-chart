package net.sayaya.ui.chart;

import elemental2.core.JsArray;
import elemental2.dom.CustomEvent;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;
import net.sayaya.ui.event.HasStateChangeHandlers;
import net.sayaya.ui.event.HasValueChangeHandlers;
import org.gwtproject.event.shared.HandlerRegistration;

import java.util.Collection;

@JsType
public class Data implements HasStateChangeHandlers<Data.DataState>, HasValueChangeHandlers<Data> {
	public static Data create(String idx) {
		return proxy(new Data(idx), Data::fireValueChangeEvent);
	}
	private final String idx;
	private final JsPropertyMap<Object> initialized = JsPropertyMap.of();
	private final JsArray<StateChangeEventListener<DataState>> stateChangeListeners = JsArray.of();
	private final JsArray<ValueChangeEventListener<Data>> valueChangeListeners = JsArray.of();
	private DataState state;
	private Data(String idx) {
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
		var v1 = initialized.get(key);
		var v2 = get(key);
		if(v1 == null && v2 == null) return false;
		else if(v1 == null || v2 == null) return true;
		return !Js.isTripleEqual(trim(Js.asString(v1)), trim(Js.asString(v2)));
	}
	private static String trim(String str) {
		if(str == null) return str;
		str = str.replace("\r", "").trim();
		if(str.isEmpty()) return null;
		else return str;
	}
	@Override
	public Collection<StateChangeEventListener<DataState>> listeners() {
		return stateChangeListeners.asList();
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
	@Override
	public Data value() {
		return this;
	}
	@Override
	public HandlerRegistration onValueChange(ValueChangeEventListener<Data> listener) {
		valueChangeListeners.push(listener);
		return () -> valueChangeListeners.delete(valueChangeListeners.asList().indexOf(listener));
	}
	private void fireValueChangeEvent() {

		var evt = ValueChangeEvent.event(new CustomEvent<>("change"), this);
		for (ValueChangeEventListener<Data> listener : valueChangeListeners.asList()) {
			if (listener == null) break;
			listener.handle(evt);
		}
	}
	public enum DataState {
		UNSELECTED, SELECTED
	}
	private native static Data proxy(Data origin, ChangeHandler consumer) /*-{
		var proxy = new Proxy(origin, {
			set: function(target, key, value, receiver) {
				if(target[key]==value) return true;
				var result = Reflect.set(target, key, value, receiver);
				consumer.@net.sayaya.ui.chart.Data.ChangeHandler::onInvoke(Lnet/sayaya/ui/chart/Data;)(target);
				return result;
			}
		});
		return proxy;
	}-*/;
	private interface ChangeHandler {
		void onInvoke(Data data);
	}
}
