package net.sayaya.ui.chart.column;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.regexp.shared.RegExp;
import elemental2.dom.HTMLElement;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import net.sayaya.ui.chart.Column;
import net.sayaya.ui.dom.MdChipElement;
import org.jboss.elemento.EventType;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.jboss.elemento.Elements.*;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Setter
@Accessors(fluent = true)
public class ColumnChip implements net.sayaya.ui.chart.column.ColumnBuilder {
    private final static String SPLITTER = "(?<!\\\\),";
    private final String id;
    @Delegate(excludes = ColumnBuilder.class) private final ColumnBuilderDefaultHelper<ColumnChip> defaultHelper = new ColumnBuilderDefaultHelper<>(()->this);
    private final ColumnStyleDataChangeHelper<ColumnChip> dataChangeHelper = new ColumnStyleDataChangeHelper<>(()->this);
    @Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleColorHelper<ColumnChip> colorHelper = new ColumnStyleColorHelper<>(()->this);
    private final List<ChipStyleColorConditionalHelper<ColumnChip>> colorConditionalHelpers = new LinkedList<>();
    @Delegate(excludes = ColumnStyleHelper.class) private final ColumnStyleFlexAlignHelper<ColumnChip> alignHelper = new ColumnStyleFlexAlignHelper<>(()->this);

    @Override
    public Column build() {
        Column column = defaultHelper.build().data(id);
        return column.readOnly(true).renderer((sheet, td, row, col, prop, value, ci)->{
            alignHelper.clear(td);
            colorHelper.clear(td);
            for(ChipStyleColorConditionalHelper<?> helper: colorConditionalHelpers) helper.clear(td);
            alignHelper.apply(td, row, prop, value);
            colorHelper.apply(td, row, prop, value);
            dataChangeHelper.apply(sheet, td, row, prop);
            var elem = div().style("display: flex; flex-direction: row; flex-wrap: wrap; gap: 8px; margin: 4px; width: calc(100% - 8px); height: calc(100% - 8px);");
            if(value==null) value = "";
            var match = value.split(SPLITTER);
            var tokens = Arrays.stream(match).filter(k->k!=null && !k.isEmpty()).map(text->{
                if(text.contains("\\,")) return text.replace("\\,", ",");
                return text;
            });
            if (readOnly()) tokens.map(token-> {
                var chip = htmlContainer("md-assist-chip", MdChipElement.MdAssistChipElement.class);
                chip.element().label = token;
                return chip;
            }).peek(e->style(e.element(), row, prop)).forEach(elem::add);
            else {
                tokens.map(token->{
                    var chip = htmlContainer("md-input-chip", MdChipElement.MdInputChipElement.class);
                    chip.element().label = token;
                    return chip;
                }).peek(e->style(e.element(), row, prop)).forEach(c->elem.add(c));
                var input = input("text").style("background: transparent; border: none; outline: none;");
                input.on(EventType.click, evt->input.element().focus());
                input.on(EventType.keydown, evt->{
                    evt.stopPropagation();
                    if("Enter".equalsIgnoreCase(evt.key)) Scheduler.get().scheduleDeferred(()->td.getElementsByTagName("input").asList().get(0).focus());
                });
                input.on(EventType.change, evt->{
                    var text = input.element().value;
                    if(text.contains(",")) text = text.replace(",", "\\,");
                    String nextValue = Stream.concat(Arrays.stream(match), Stream.of(text)).collect(Collectors.joining(","));
                    sheet.setDataAtCell(row, col, nextValue);
                });
                elem.add(input);
            }
            td.innerHTML = "";
            td.append(elem.element());
            alignHelper.apply(td, row, prop, value);
            return td;
        }).headerRenderer(n->span().textContent(defaultHelper.name()).element());
    }
    private void style(MdChipElement.MdAssistChipElement chip, int row, String prop) {
        String value = chip.label;
        for(ChipStyleColorConditionalHelper<?> helper: colorConditionalHelpers) {
            helper.apply(chip, row, prop, value);
        }
    }
    private void style(MdChipElement.MdInputChipElement chip, int row, String prop) {
        String value = chip.label;
        for(ChipStyleColorConditionalHelper<?> helper: colorConditionalHelpers) {
            helper.apply(chip, row, prop, value);
        }
    }
    public ChipStyleColorConditionalHelper<ColumnChip> pattern(String pattern) {
        var helper = new ChipStyleColorConditionalHelper<>(pattern, ()->this);
        colorConditionalHelpers.add(helper);
        return helper;
    }
    public final static class ColumnStyleFlexAlignHelper<SELF> implements ColumnStyleHelper<SELF> {
        private final Supplier<SELF> _self;
        private String horizontal;
        private String vertical;
        public ColumnStyleFlexAlignHelper(Supplier<SELF> columnBuilder) {
            _self = columnBuilder;
        }
        @Override
        public HTMLElement apply(HTMLElement td, int row, String prop, String value) {
            var children = td.getElementsByTagName("div").asList();
            if(children.size()<=0) return td;
            var flex = (HTMLElement) children.get(0);
            if(horizontal !=null)	flex.style.justifyContent = horizontal;
            if(vertical!=null)  	flex.style.alignContent   = vertical;
            return td;
        }
        @Override
        public SELF clear(HTMLElement td) {
            var children = td.getElementsByTagName("div").asList();
            if(children.size()<=0) return that();
            var flex = (HTMLElement) children.get(0);
            flex.style.removeProperty("justifyContent");
            flex.style.removeProperty("alignContent");
            return that();
        }
        public SELF horizontal(String horizontal) {
            if("left".equalsIgnoreCase(vertical)) vertical = "flex-start";
            if("right".equalsIgnoreCase(vertical)) vertical = "flex-end";
            this.horizontal = horizontal;
            return that();
        }
        public SELF vertical(String vertical) {
            if("top".equalsIgnoreCase(vertical)) vertical = "flex-start";
            if("bottom".equalsIgnoreCase(vertical)) vertical = "flex-end";
            this.vertical = vertical;
            return that();
        }
        private SELF that() {
            return _self.get();
        }
    }
    public final static class ChipStyleColorConditionalHelper<SELF> implements ColumnStyleHelper<SELF> {
        private final RegExp pattern;
        private ColumnStyleFn<String> color;
        private ColumnStyleFn<String> colorBackground;
        private final Supplier<SELF> _self;
        public ChipStyleColorConditionalHelper(String pattern, Supplier<SELF> columnBuilder) {
            this.pattern = RegExp.compile(pattern.trim());
            _self = columnBuilder;
        }
        @Override
        public HTMLElement apply(HTMLElement td, int row, String prop, String value) {
            if(value == null) return td;
            if(pattern.test(value.trim())) {
                if(color !=null)             td.style.color              = color.apply(td, row, prop, value);
                if(colorBackground !=null)   td.style.backgroundColor    = colorBackground.apply(td, row, prop, value);
            }
            return td;
        }
        @Override
        public SELF clear(HTMLElement td) {
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
}
