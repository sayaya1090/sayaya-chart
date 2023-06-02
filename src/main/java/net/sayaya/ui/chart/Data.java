package net.sayaya.ui.chart;

import elemental2.core.JsArray;
import elemental2.core.JsObject;
import elemental2.core.ObjectPropertyDescriptor;
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
public class Data implements HasStateChangeHandlers<Data.DataState>{
	public static Data create(String idx) {
		Data origin = new Data(idx);
		ObjectPropertyDescriptor<Data> hide = Js.cast(new Object());
		hide.setEnumerable(false);
		for(var field: JsObject.getOwnPropertyNames(origin).asList()) JsObject.defineProperty(origin, field, hide);
		return proxy(origin, Data::fireValueChangeEvent);
	}
	@JsIgnore
	private final String idx;
	@JsIgnore
	private final JsPropertyMap<Object> initialized = JsPropertyMap.of();
	@JsIgnore
	private final JsArray<StateChangeEventListener<DataState>> stateChangeListeners = JsArray.of();
	@JsIgnore
	private final JsArray<HasValueChangeHandlers.ValueChangeEventListener<Data>> valueChangeListeners = JsArray.of();
	@JsIgnore
	private DataState state;
	private Data(String idx) {
		this.idx = idx;
	}
	@JsIgnore
	@JsMethod
	public String idx() {
		return idx;
	}
	@JsIgnore
	public Data put(String key, String value) {
		Js.asPropertyMap(this).set(key, value);
		if(!initialized.has(key)) initialized.set(key, value);
		return this;
	}
	@JsIgnore
	public Data delete(String key) {
		Js.asPropertyMap(this).delete(key);
		return this;
	}
	@JsIgnore
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
	@JsIgnore
	public boolean isChanged(String key) {
		var v1 = initialized.get(key);
		var v2 = get(key);
		if(v1 == null && v2 == null) return false;
		else if(v1 == null || v2 == null) return true;
		return !Js.isTripleEqual(trim(Js.asString(v1)), trim(Js.asString(v2)));
	}
	@JsIgnore
	public boolean isChanged() {
		return JsObject.keys(initialized).asList().stream().anyMatch(this::isChanged);
	}
	private static String trim(String str) {
		if(str == null) return str;
		str = str.replace("\r", "").trim();
		if(str.isEmpty()) return null;
		else return str;
	}
	@JsIgnore
	@Override
	public Collection<StateChangeEventListener<DataState>> listeners() {
		return stateChangeListeners.asList();
	}
	@Override
	@JsIgnore
	public DataState state() {
		return state;
	}
	@JsIgnore
	public Data select(boolean select) {
		state = select?DataState.SELECTED:DataState.UNSELECTED;
		fireStateChangeEvent();
		return this;
	}
	@JsIgnore
	public HandlerRegistration onValueChange(HasValueChangeHandlers.ValueChangeEventListener<Data> listener) {
		valueChangeListeners.push(listener);
		return () -> valueChangeListeners.delete(valueChangeListeners.asList().indexOf(listener));
	}
	@JsIgnore
	private void fireValueChangeEvent() {
		var evt = HasValueChangeHandlers.ValueChangeEvent.event(new CustomEvent<>("change"), this);
		for (HasValueChangeHandlers.ValueChangeEventListener<Data> listener : valueChangeListeners.asList()) {
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
				if(result) consumer.@net.sayaya.ui.chart.Data.ChangeHandler::onInvoke(Lnet/sayaya/ui/chart/Data;)(target);
				return result;
			}
		});
		return proxy;
	}-*/;
	private interface ChangeHandler {
		void onInvoke(Data data);
	}
}
