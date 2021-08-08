package com.xjx.ddtcrawler.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author XieJiaxing
 * @date 2021/8/1 16:57
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Result {

    @XStreamAsAttribute
    private Long total;
    @XStreamAsAttribute
    private Boolean value;
    @XStreamAsAttribute
    private String message;
    @XStreamImplicit
    @XStreamAsAttribute
    private List<Item> Item;

    private static XStream xStream;

    static {
        xStream = new XStream(new DomDriver());
        XStream.setupDefaultSecurity(xStream);
        xStream.allowTypes(new Class[]{Result.class, Item.class});
        xStream.processAnnotations(Result.class);
        xStream.alias("Result", Result.class);
        xStream.alias("Item", Item.class);
    }

    public boolean isSuccess() {
        return Boolean.TRUE.equals(value);
    }

    public static Result parseResult(String s) {
        Result result;
        try {
            result = (Result) xStream.fromXML(s.replace("&#x8;", "\t"));
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return "Result{" + "total=" + total + ", value=" + value + ", message='" + message + "'\n" +
                Item.stream().map(String::valueOf).collect(Collectors.joining("\n")) + "\n" +
                '}';
    }
}
